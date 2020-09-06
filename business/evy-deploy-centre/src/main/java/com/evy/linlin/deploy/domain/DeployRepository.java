package com.evy.linlin.deploy.domain;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.JsonUtils;
import com.evy.linlin.deploy.dto.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final String SHELL_CMD = "/bin/sh";

    /**
     * 通过git链接,调用shell脚本,获取并返回对应分支列表
     * @param gitBrchanDo  com.evy.linlin.deploy.dto.GitBrchanDO
     * @return msg 返回Null，表示获取失败
     */
    public GitBrchanOutDO getGitBrchansShell(GitBrchanDO gitBrchanDo) {
        String gitPath = gitBrchanDo.getGitPath();
        GitBrchanOutDO gitBrchanOutDo;
        ShellOutDO shellOutDo = execShell(SHELL_GET_GIT_BRCHANS, gitPath, subProjectNameFromGitPath(gitPath));

        String errorCode = shellOutDo.getErrorCode();
        String msg = shellOutDo.getMsg();
        if (BusinessConstant.ZERO.equals(errorCode) || !StringUtils.isEmpty(msg)) {
            //获取分支信息成功
            //分支信息第一行为HEAD指向[ origin/HEAD -> origin/master ],跳过第一行
            List<String> branchs = Arrays.stream(msg.split("\n", -1)).skip(1).collect(Collectors.toList());
            gitBrchanOutDo = GitBrchanOutDO.create(gitPath, branchs);
        } else {
            gitBrchanOutDo = GitBrchanOutDO.create(gitPath, null);
        }
        return gitBrchanOutDo;
    }

    /**
     * 自动编译及部署到对应服务器
     */
    public AutoDeployOutDO autoDeploy(AutoDeployDO autoDeployDO) {

        return null;
    }

    /**
     * 启用gitBuild.sh<br/>
     * sh -x gitBuild.sh ${git项目名称} ${git地址} ${git分支}<br/>
     * 返回: {"errorCode":"0","msg":"已经是最新的。"}<br/>
     */
    private void gitBuildShell(String projectName, String gitPath, String branch) {

    }

    /**
     * 启用buildJar.sh<br/>
     * sh -vx buildJar.sh evy-business test-demo 1 2020-08-07<br/>
     * 返回编译后的jar路径,如 {"errorCode":"0","msg":"/cdadmin/gitProject/history/test-demo/2020-08-25"}
     */
    private void buildJarShell(String projectName, String appName, boolean switchJunit, String deployTimestamp) {

    }

    /**
     * 启用startJar.sh<br/>
     * sh -x startJar.sh 192.168.152.128 /cdadmin/gitProject/history/test-demo/2020-08-30 -Xms=512m<br/>
     * 返回远程服务器启动pid 如: {"errorCode":"0","msg":"5464"}
     */
    private void startJarShell(String targetHost, String jarPath, String jvmParam) {

    }

    /**
     * 执行linux shell
     *
     * @param shellCmd shell脚本
     * @param params   参数
     * @return shell脚本返回
     */
    private ShellOutDO execShell(String shellCmd, String... params) {
        String[] param = new String[params.length + 1];
        param[0] = shellCmd;
        ShellOutDO outDo = new ShellOutDO();
        if (params.length > 0) {
            System.arraycopy(params, 0, param, 1, params.length);
        }

        try {
            Process process = RUNTIME.exec(SHELL_CMD, param);
            BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
            byte[] bytes = new byte[2048];
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bis.available());
            int i;

            while ((i = bis.read(bytes)) != -1) {
                baos.write(bytes, 0, i);
            }

            String json = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            outDo = JsonUtils.convertToObject(json, ShellOutDO.class);
        } catch (IOException e) {
            CommandLog.errorThrow("execShell异常", e);
            outDo.setErrorCode(ErrorConstant.ERROR_01);
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

        return gitPath.substring(gitPath.lastIndexOf(flag1) + 1, gitPath.lastIndexOf(flag2));
    }
}
