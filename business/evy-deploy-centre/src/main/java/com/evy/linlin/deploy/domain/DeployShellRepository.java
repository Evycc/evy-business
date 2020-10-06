package com.evy.linlin.deploy.domain;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.DateUtils;
import com.evy.common.utils.JsonUtils;
import com.evy.linlin.deploy.domain.repository.DeployDataRepository;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import com.evy.linlin.deploy.tunnel.constant.DeployErrorConstant;
import com.evy.linlin.deploy.tunnel.constant.DeployStageEnum;
import com.evy.linlin.deploy.tunnel.model.*;
import com.evy.linlin.deploy.tunnel.po.DeployQryOutPO;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 提供自动化部署功能
 *
 * @Author: EvyLiuu
 * @Date: 2020/9/5 22:43
 */
@Repository
public class DeployShellRepository {
    /**
     * 通过该实例调用shell脚本
     */
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final String SHELL_GIT_BUILD = "/cdadmin/gitBuild.sh";
    private static final String SHELL_BUILD_JAR = "/cdadmin/buildJar.sh";
    private static final String SHELL_START_JAR = "/cdadmin/startJar.sh";
    private static final String SHELL_GET_GIT_BRCHANS = "/cdadmin/getGitBrchans.sh";
    private static final String CHMOD_755 = "755";
    private static final String CMD_CHMOD = "/bin/chmod";
    private static final String SHELL_CMD = "/bin/bash";
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService(DeployShellRepository.class.getName());
    private static final String BRCHAN_FILTER_STR = "origin/";
    private static final String JVM_PARAM_DEFAULT = "-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/applog/current/dump.log";
    private final DeployDataRepository deployDataRepository;

    public DeployShellRepository(DeployDataRepository deployDataRepository) {
        this.deployDataRepository = deployDataRepository;
    }

    /**
     * 通过git链接,调用shell脚本,获取并返回对应分支列表
     *
     * @param gitBrchanDo GitBrchanDO
     * @return branchs 返回Null，表示获取失败
     */
    public GitBrchanOutDO getGitBrchansShell(GitBrchanDO gitBrchanDo) {
        String gitPath = gitBrchanDo.getGitPath();
        GitBrchanOutDO gitBrchanOutDo;
        ShellOutDO shellOutDo = execShell(chmod755(SHELL_GET_GIT_BRCHANS), gitPath, DeployAssembler.subProjectNameFromGitPath(gitPath));

        String errorCode = shellOutDo.getErrorCode();
        String msg = shellOutDo.getMsg();
        if (BusinessConstant.ZERO.equals(errorCode) || !StringUtils.isEmpty(msg)) {
            //获取分支信息成功
            //分支信息第一行为HEAD指向[ origin/HEAD -> origin/master ],跳过第一行
            List<String> branchs = Arrays.stream(msg.split("\n", -1))
                    .skip(1)
                    .map(str -> str.replaceAll(BusinessConstant.WHITE_EMPTY_STR, BusinessConstant.EMPTY_STR).replaceAll(BRCHAN_FILTER_STR, BusinessConstant.EMPTY_STR))
                    .collect(Collectors.toList());
            gitBrchanOutDo = DeployAssembler.createGitBrchanOutDO(gitPath, branchs);
        } else {
            gitBrchanOutDo = DeployAssembler.createGitBrchanOutDO(gitPath, null);
        }
        return gitBrchanOutDo;
    }

    /**
     * 异步执行: 编译jar包，并返回编译唯一序列
     */
    public void build(BuildInfoDO buildInfoDo) {
        EXECUTOR_SERVICE.submit(() -> {
            String buildSeq = buildInfoDo.getBuildSeq();
            try {
                DeployQryOutPO deployQryOutPo = deployDataRepository.qryForSeq(DeployAssembler.createFromBuildSeq(buildSeq));

                //标记为编译中
                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePo(DeployStageEnum.BUILD_ING.convertToFlag(), buildSeq));

                //1.调用gitBuild.sh errorCode=0 && msg != ""
                String gitPath = deployQryOutPo.getGitPath();
                String projectName = DeployAssembler.subProjectNameFromGitPath(gitPath);
                if (checkGitBuildShell(gitBuildShell(projectName, gitPath, deployQryOutPo.getGitBrchan()))) {
                    //2.调用buildJar.sh 获取msg编译后目录
                    String timeStamp = DateUtils.nowStr3().replace(BusinessConstant.WHITE_EMPTY_STR, "T");
                    ShellOutDO buildJarOutDo = buildJarShell(projectName, deployQryOutPo.getAppName(), buildInfoDo.isSwitchJunit(), timeStamp);

                    if (checkBuildJarShell(buildJarOutDo)) {
                        //标记为编译成功
                        //记录jarPath到数据库
                        deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePoBuildSuccess(buildJarOutDo.getMsg(),
                                DeployStageEnum.BUILD_SUCCESS.convertToFlag(), buildSeq));
                    }
                } else {
                    throw new BasicException(DeployErrorConstant.BUILD_ERROR_1, "gitBuild.sh编译失败 seq:" + buildSeq);
                }
            } catch (Exception exception) {
                //标记为编译失败
                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePo(DeployStageEnum.BUILD_FAILD.convertToFlag(), buildSeq));
                CommandLog.errorThrow("DeployShellRepository#build异常", exception);
            }
        });
    }

    /**
     * 异步执行:自动署到对应服务器
     */
    public void autoDeploy(AutoDeployDO autoDeployDO) {
        EXECUTOR_SERVICE.submit(() -> {
            String buildSeq = autoDeployDO.getBuildSeq();
            try {
                DeployQryOutPO deployQryOutPo = deployDataRepository.qryForSeq(DeployAssembler.createFromBuildSeq(buildSeq));
                List<DeployStatusOutDO> deployStatusOutDoList = new ArrayList<>(8);

                //标记为部署中
                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePo(DeployStageEnum.DEPLOY_ING.convertToFlag(), buildSeq));

                //1.调用startJar.sh 部署到指定服务器 (根据服务器列表,是否分批参数,选择并行还是串行)
                String jarPath = deployQryOutPo.getJarPath();
                String targetHost = deployQryOutPo.getTargetHost();
                String jvmParam = deployQryOutPo.getJvmParam() + JVM_PARAM_DEFAULT;
                String[] targetHosts = targetHost.split(BusinessConstant.SPLIT_DOUBLE_LINE, -1);
                boolean isMoreHost = targetHost.length() > BusinessConstant.ONE_NUM;

                if (isMoreHost) {
                    if (deployQryOutPo.getSwitchBatchDeploy() == BusinessConstant.ZERO_NUM) {
                        //并行部署
                        for (String host : targetHosts) {
                            EXECUTOR_SERVICE.submit(() -> {
                                deployStatusOutDoList.add(excelAndGetStartJarShell(host, jarPath, jvmParam));
                            });
                        }
                    } else {
                        //串行部署
                        for (String host : targetHosts) {
                            DeployStatusOutDO deployStatusOutDo = excelAndGetStartJarShell(host, jarPath, jvmParam);
                            deployStatusOutDoList.add(deployStatusOutDo);
                            if (!checkGetStartJarShell(deployStatusOutDo)) {
                                //PID为空则认为部署失败，直接返回
                                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePoBuildLog(host + ":部署异常",
                                        DeployStageEnum.DEPLOY_FAILD.convertToFlag(), buildSeq));
                                return;
                            }
                        }
                    }
                } else {
                    //单服务器
                    deployStatusOutDoList.add(excelAndGetStartJarShell(targetHost, jarPath, jvmParam));
                }

                deployStatusOutDoList.forEach(deployStatusOutDo -> {
                    if (!checkGetStartJarShell(deployStatusOutDo)) {
                        //标记为部署失败
                        deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePoBuildLog(deployStatusOutDo.getTargetHost() + ":部署异常",
                                DeployStageEnum.DEPLOY_FAILD.convertToFlag(), buildSeq));
                        return;
                    }
                });

                //标记为部署成功
                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePo(DeployStageEnum.DEPLOY_SUCCESS.convertToFlag(), buildSeq));
            } catch (Exception exception) {
                //标记为部署失败
                deployDataRepository.updateStage(DeployAssembler.createDeployUpdatePo(DeployStageEnum.DEPLOY_FAILD.convertToFlag(), buildSeq));
                CommandLog.errorThrow("DeployShellRepository#autoDeploy部署异常", exception);
            }
        });
    }

    /**
     * 检查gitBuild.sh执行是否成功
     */
    private boolean checkGitBuildShell(ShellOutDO shellOutDo) {
        return BusinessConstant.ZERO.equals(shellOutDo.getErrorCode()) || !Objects.isNull(shellOutDo.getMsg());
    }

    /**
     * 检查buildJar.sh执行是否成功<br/>
     * true 成功 false 失败
     */
    private boolean checkBuildJarShell(ShellOutDO shellOutDo) {
        return !StringUtils.isEmpty(shellOutDo.getMsg());
    }

    /**
     * 检查startJar.sh执行是否成功，失败则赋值错误码<br/>
     * true 成功 false 失败
     */
    private boolean checkGetStartJarShell(DeployStatusOutDO deployStatusOutDo) {
        return !StringUtils.isEmpty(deployStatusOutDo.getPid());
    }

    /**
     * 启用gitBuild.sh<br/>
     * sh -x gitBuild.sh ${git项目名称} ${git地址} ${git分支}<br/>
     * 返回: {"errorCode":"0","msg":"已经是最新的。"}<br/>
     */
    private ShellOutDO gitBuildShell(String projectName, String gitPath, String branch) {
        return execShell(chmod755(SHELL_GIT_BUILD), projectName, gitPath, branch);
    }

    /**
     * 启用buildJar.sh<br/>
     * sh -vx buildJar.sh evy-business test-demo 1 2020-08-07<br/>
     * 返回编译后的jar路径,如 {"errorCode":"0","msg":"/cdadmin/gitProject/history/test-demo/2020-08-25"}
     */
    private ShellOutDO buildJarShell(String projectName, String appName, boolean switchJunit, String deployTimestamp) {
        return execShell(chmod755(SHELL_BUILD_JAR), projectName, appName,
                switchJunit ? BusinessConstant.ZERO : BusinessConstant.ONE, deployTimestamp);
    }

    /**
     * 启用startJar.sh<br/>
     * sh -x startJar.sh 192.168.152.128 /cdadmin/gitProject/history/test-demo/2020-08-30 -Xms=512m<br/>
     * 返回远程服务器启动pid 如: {"errorCode":"0","msg":"5464"}
     */
    private ShellOutDO startJarShell(String targetHost, String jarPath, String jvmParam) {
        return execShell(chmod755(SHELL_START_JAR), targetHost, jarPath, jvmParam);
    }

    /**
     * 执行startJar.sh,并返回DeployStatusOutDO
     *
     * @param targetHost 目标服务器host
     * @param jarPath    jar包路径
     * @param jvmParam   jvm参数
     * @return DeployStatusOutDO pid为空表示部署失败
     */
    private DeployStatusOutDO excelAndGetStartJarShell(String targetHost, String jarPath, String jvmParam) {
        ShellOutDO shellOutDo = startJarShell(targetHost, jarPath, jvmParam);
        return DeployAssembler.createDeployStatusOutDO(shellOutDo.getMsg(), targetHost);
    }

    /**
     * 执行linux shell
     *
     * @param shellCmd shell脚本绝对路径
     * @param params   参数
     * @return shell脚本返回
     */
    private ShellOutDO execShell(String shellCmd, String... params) {
        List<String> paramList = Arrays.asList(params);
        ShellOutDO outDo = new ShellOutDO();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SHELL_CMD).append(BusinessConstant.WHITE_EMPTY_STR)
                .append(shellCmd).append(BusinessConstant.WHITE_EMPTY_STR);
        paramList.forEach(param -> stringBuilder.append(param).append(BusinessConstant.WHITE_EMPTY_STR));

        String result = excelCmd(stringBuilder.toString(), true);
        if (StringUtils.isEmpty(result)) {
            outDo.setErrorCode(ErrorConstant.ERROR_01);
        } else {
            outDo = JsonUtils.convertToObject(result, ShellOutDO.class);
        }

        return outDo;
    }

    /**
     * 为sh脚本设置755权限
     *
     * @param shellFilePath sh脚本绝对路径
     * @return sh脚本绝对路径
     */
    private String chmod755(String shellFilePath) {
        String stringBuilder = CMD_CHMOD + BusinessConstant.WHITE_EMPTY_STR +
                CHMOD_755 + BusinessConstant.WHITE_EMPTY_STR + shellFilePath;
        excelCmd(stringBuilder, false);
        return shellFilePath;
    }

    /**
     * 执行系统命令
     *
     * @param cmd       完整的系统命令字符串
     * @param hasReturn 是否存在返回值 true : 存在返回 false : 不存在返回
     * @return 执行结果，执行错误返回空字符串
     */
    private String excelCmd(String cmd, boolean hasReturn) {
        CommandLog.info("执行系统命令:{}", cmd);
        BufferedInputStream bis = null;
        BufferedInputStream errorInput;
        String json = BusinessConstant.EMPTY_STR;

        try {
            Process process = RUNTIME.exec(cmd);
//            process.waitFor(60L, TimeUnit.SECONDS);
            process.waitFor();
            bis = new BufferedInputStream(process.getInputStream());
            int available = bis.available();

            if (!hasReturn) {
                return json;
            } else if (available <= BusinessConstant.ZERO_NUM) {
                errorInput = new BufferedInputStream(process.getErrorStream());
                String errorMsg = readInputStream(errorInput);
                errorInput.close();
                throw new Exception(errorMsg);
            }

            json = readInputStream(bis);
        } catch (Exception e) {
            CommandLog.errorThrow("execShell异常", e);
        } finally {
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }

        if (!StringUtils.isEmpty(json)) {
            CommandLog.info("执行系统命令返回:{}", json);
        }

        return json;
    }

    /**
     * 读取数据流并返回字符串
     * @param inputStream 数据流
     * @return  异常则返回空串
     */
    private String readInputStream(InputStream inputStream) {
        ByteArrayOutputStream baos = null;
        byte[] bytes = new byte[2048];
        String json = BusinessConstant.EMPTY_STR;

        try {
            int available = inputStream.available();

            if ((available > BusinessConstant.ZERO_NUM)) {
                baos = new ByteArrayOutputStream();
                int i;

                while ((i = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, i);
                    available -= i;
                    if (available == BusinessConstant.ZERO_NUM) {
                        break;
                    }
                }

                json = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            CommandLog.errorThrow("DeployShellRepository#readInputStream读取异常", e);
            if (Objects.nonNull(baos)) {
                try {
                    baos.close();
                } catch (IOException ioException) {
                    CommandLog.errorThrow("DeployShellRepository#readInputStream关闭流异常", ioException);
                }
            }
        }

        return json;
    }
}
