package com.evy.linlin.deploy.domain;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.DateUtils;
import com.evy.common.utils.JsonUtils;
import com.evy.linlin.deploy.tunnel.constant.DeployErrorConstant;
import com.evy.linlin.deploy.tunnel.dto.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 提供自动化部署功能
 *
 * @Author: EvyLiuu
 * @Date: 2020/9/5 22:43
 */
@Repository
public class DeployRepository {
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
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService("DeployRepository");

    /**
     * 通过git链接,调用shell脚本,获取并返回对应分支列表
     *
     * @param gitBrchanDo GitBrchanDO
     * @return branchs 返回Null，表示获取失败
     */
    public GitBrchanOutDO getGitBrchansShell(GitBrchanDO gitBrchanDo) {
        String gitPath = gitBrchanDo.getGitPath();
        GitBrchanOutDO gitBrchanOutDo;
        ShellOutDO shellOutDo = execShell(chmod755(SHELL_GET_GIT_BRCHANS), gitPath, subProjectNameFromGitPath(gitPath));

        String errorCode = shellOutDo.getErrorCode();
        String msg = shellOutDo.getMsg();
        if (BusinessConstant.ZERO.equals(errorCode) || !StringUtils.isEmpty(msg)) {
            //获取分支信息成功
            //分支信息第一行为HEAD指向[ origin/HEAD -> origin/master ],跳过第一行
            List<String> branchs = Arrays.stream(msg.split("\n", -1))
                    .skip(1)
                    .map(str -> str.replaceAll(BusinessConstant.WHITE_EMPTY_STR, BusinessConstant.EMPTY_STR))
                    .collect(Collectors.toList());
            gitBrchanOutDo = GitBrchanOutDO.create(gitPath, branchs);
        } else {
            gitBrchanOutDo = GitBrchanOutDO.create(gitPath, null);
        }
        return gitBrchanOutDo;
    }

    /**
     * 自动编译及部署到对应服务器
     * @return 返回各个服务器部署pid，errorCode不为0则部署失败
     */
    public AutoDeployOutDO autoDeploy(AutoDeployDO autoDeployDO) {
        AutoDeployOutDO autoDeployOutDo = new AutoDeployOutDO();
        //1.调用gitBuild.sh errorCode=0 && msg != ""
        String gitPath = autoDeployDO.getGitPath();
        String projectName = subProjectNameFromGitPath(gitPath);
        List<DeployStatusOutDO> deployStatusOutDos = new ArrayList<>(8);

        ShellOutDO gitBuildShellOutDo = gitBuildShell(projectName, gitPath, autoDeployDO.getBrchanName());
        if (checkGitBuildShell(gitBuildShellOutDo, autoDeployOutDo)) {
            //2.调用buildJar.sh 获取msg编译后目录
            String timeStamp = DateUtils.nowStr3().replace(BusinessConstant.WHITE_EMPTY_STR, BusinessConstant.STRIKE_THROUGH_STR);
            ShellOutDO buildJarOutDo = buildJarShell(projectName, autoDeployDO.getAppName(), autoDeployDO.isSwitchJunit(), timeStamp);
            String jarPath = buildJarOutDo.getMsg();

            if (checkBuildJarShell(buildJarOutDo, autoDeployOutDo)) {
                //3.调用startJar.sh 部署到指定服务器 (根据服务器列表,是否分批参数,选择并行还是串行)
                String targetHost = autoDeployDO.getTargetHost();
                String jvmParam = autoDeployDO.getJvmParam();
                String[] targetHosts = targetHost.split(BusinessConstant.SPLIT_LINE, -1);
                boolean isMoreHost = targetHost.length() > BusinessConstant.ONE_NUM;

                if (isMoreHost) {
                    if (!autoDeployDO.isSwitchBatchDeploy()) {
                        //并行部署
                        for (String host : targetHosts) {
                            EXECUTOR_SERVICE.submit(() -> {
                                DeployStatusOutDO deployStatusOutDo = excelAndGetStartJarShell(host, jarPath, jvmParam);
                                deployStatusOutDos.add(deployStatusOutDo);
                            });
                        }
                    } else {
                        //串行部署
                        for (String host : targetHosts) {
                            DeployStatusOutDO deployStatusOutDo = excelAndGetStartJarShell(host, jarPath, jvmParam);
                            deployStatusOutDos.add(deployStatusOutDo);
                            if (checkGetStartJarShell(deployStatusOutDo, autoDeployOutDo)) {
                                //PID为空则认为部署失败，直接返回
                                break;
                            }
                        }
                    }
                } else {
                    //单服务器
                    DeployStatusOutDO deployStatusOutDo = excelAndGetStartJarShell(targetHost, jarPath, jvmParam);
                    deployStatusOutDos.add(deployStatusOutDo);
                }
            }
        }

        autoDeployOutDo.setDeployStatusOutDOList(deployStatusOutDos);
        return autoDeployOutDo;
    }

    /**
     * 检查gitBuild.sh执行是否成功，失败则赋值错误码
     */
    private boolean checkGitBuildShell(ShellOutDO shellOutDo, AutoDeployOutDO autoDeployOutDo) {
        if (!BusinessConstant.ZERO.equals(shellOutDo.getErrorCode()) && Objects.isNull(shellOutDo.getMsg())) {
            autoDeployOutDo.setErrorCode(DeployErrorConstant.DEPLOY_ERROR_1);
            return false;
        }
        return true;
    }

    /**
     * 检查buildJar.sh执行是否成功，失败则赋值错误码
     */
    private boolean checkBuildJarShell(ShellOutDO shellOutDo, AutoDeployOutDO autoDeployOutDo) {
        if (StringUtils.isEmpty(shellOutDo.getMsg())) {
            autoDeployOutDo.setErrorCode(DeployErrorConstant.DEPLOY_ERROR_1);
            return false;
        }
        return true;
    }

    /**
     * 检查startJar.sh执行是否成功，失败则赋值错误码
     */
    private boolean checkGetStartJarShell(DeployStatusOutDO deployStatusOutDo, AutoDeployOutDO autoDeployOutDo) {
        if (StringUtils.isEmpty(deployStatusOutDo.getPid())) {
            autoDeployOutDo.setErrorCode(DeployErrorConstant.DEPLOY_ERROR_1);
            return false;
        }
        return true;
    }


    /**
     * 启用gitBuild.sh<br/>
     * sh -x gitBuild.sh ${git项目名称} ${git地址} ${git分支}<br/>
     * 返回: {"errorCode":"0","msg":"已经是最新的。"}<br/>
     */
    private ShellOutDO gitBuildShell(String projectName, String gitPath, String branch) {
        return execShell(chmod755(SHELL_GIT_BUILD), gitPath, projectName, branch);
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
     * @param targetHost 目标服务器host
     * @param jarPath   jar包路径
     * @param jvmParam  jvm参数
     * @return DeployStatusOutDO pid为空表示部署失败
     */
    private DeployStatusOutDO excelAndGetStartJarShell(String targetHost, String jarPath, String jvmParam) {
        ShellOutDO shellOutDo = startJarShell(targetHost, jarPath, jvmParam);
        return DeployStatusOutDO.create(shellOutDo.getMsg(), targetHost);
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
     * 截取git 项目名称
     *
     * @param gitPath git路径
     * @return git路径项目名称
     */
    private String subProjectNameFromGitPath(String gitPath) {
        String flag1 = "/";
        String flag2 = ".git";
        String result = gitPath.substring(gitPath.lastIndexOf(flag1) + 1, gitPath.lastIndexOf(flag2));
        CommandLog.info("subProjectNameFromGitPath gitPath:{}", gitPath);
        CommandLog.info("subProjectNameFromGitPath result:{}", result);

        return result;
    }

    /**
     * 为sh脚本设置755权限
     * @param shellFilePath sh脚本绝对路径
     * @return  sh脚本绝对路径
     */
    private String chmod755(String shellFilePath) {
        String stringBuilder = CMD_CHMOD + BusinessConstant.WHITE_EMPTY_STR +
                CHMOD_755 + BusinessConstant.WHITE_EMPTY_STR + shellFilePath;
        excelCmd(stringBuilder, false);
        return shellFilePath;
    }

    /**
     * 执行系统命令
     * @param cmd 完整的系统命令字符串
     * @param hasReturn 是否存在返回值 true : 存在返回 false : 不存在返回
     * @return  执行结果，执行错误返回空字符串
     */
    private String excelCmd(String cmd, boolean hasReturn) {
        CommandLog.info("执行系统命令:{}", cmd);
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        String json = BusinessConstant.EMPTY_STR;
        try {
            Process process = RUNTIME.exec(cmd);
            bis = new BufferedInputStream(process.getInputStream());

            process.waitFor(10L, TimeUnit.SECONDS);
            int available = bis.available();

            if (!hasReturn) {
                return json;
            } else if (available <= BusinessConstant.ZERO_NUM) {
                throw new Exception();
            }

            byte[] bytes = new byte[2048];
            baos = new ByteArrayOutputStream(bis.available());
            int i;

            while ((i = bis.read(bytes)) != -1) {
                baos.write(bytes, 0, i);
                available -= i;
                if (available == BusinessConstant.ZERO_NUM) {
                    break;
                }
            }

            json = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            CommandLog.errorThrow("execShell异常", e);
        } finally {
            if (Objects.nonNull(bis)) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(baos)) {
                try {
                    baos.close();
                } catch (IOException ignored) {
                }
            }
        }

        if (!StringUtils.isEmpty(json)) {
            CommandLog.info("执行系统命令返回:{}", json);
        }

        return json;
    }
}
