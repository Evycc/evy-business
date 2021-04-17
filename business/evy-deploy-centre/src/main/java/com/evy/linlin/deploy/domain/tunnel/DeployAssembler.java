package com.evy.linlin.deploy.domain.tunnel;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.SequenceUtils;
import com.evy.linlin.deploy.domain.tunnel.model.*;
import com.evy.linlin.deploy.domain.tunnel.po.*;
import com.evy.linlin.deploy.dto.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于装配DTO、DO、PO之间的关系<br/>
 * 充当与repository之间的粘合剂
 *
 * @Author: EvyLiuu
 * @Date: 2020/10/3 0:20
 */
public class DeployAssembler {
    /*---------------- doConvertDto DO转DTO ----------------*/

    public static GetGitBrchansOutDTO doConvertDto(GitBrchanOutDO gitBrchanOutDo) {
        return new GetGitBrchansOutDTO(gitBrchanOutDo.getGitPath(), gitBrchanOutDo.getGitBrchans());
    }

    /*---------------- dtoConvertPo DTO转PO ----------------*/

    /**
     * QryDeployInfoDTO -> DeployQryPO
     */
    public static DeployQryPO dtoConvertPo(QryDeployInfoDTO qryDeployInfoDTO) {
        return DeployAssembler.createFromUserSeq(qryDeployInfoDTO.getUserSeq(), qryDeployInfoDTO.getDeploySeq());
    }

    /**
     * NextDeployBuildSeqDTO -> DeployInsertPO
     */
    public static DeployInsertPO dtoConvertPo(NextDeployBuildSeqDTO nextDeployBuildSeqDTO) {
        String nextSeq = SequenceUtils.getNextSeq();
        String projectName = subProjectNameFromGitPath(nextDeployBuildSeqDTO.getGitPath());
        return new DeployInsertPO(nextSeq, nextDeployBuildSeqDTO.getUserSeq(), nextDeployBuildSeqDTO.getDeploySeq(),
                projectName, nextDeployBuildSeqDTO.getAppName(), nextDeployBuildSeqDTO.getGitPath(),
                nextDeployBuildSeqDTO.getBrchanName(), nextDeployBuildSeqDTO.getRemarks(),
                nextDeployBuildSeqDTO.getJvmParam(), nextDeployBuildSeqDTO.getTargetHost(),
                nextDeployBuildSeqDTO.getSwitchBatchDeploy() ? 0 : 1, nextDeployBuildSeqDTO.getSwitchJunit() ? 0 : 1);
    }

    /**
     * CreateDeployInfoDTO -> DeployInsertPO
     */
    public static DeployInsertPO dtoConvertPo(CreateDeployInfoDTO createDeployInfoDTO) {
        String nextSeq = SequenceUtils.getNextSeq();
        String projectName = subProjectNameFromGitPath(createDeployInfoDTO.getGitPath());
        return new DeployInsertPO(null, createDeployInfoDTO.getUserSeq(), nextSeq,
                projectName, createDeployInfoDTO.getAppName(), createDeployInfoDTO.getGitPath(),
                createDeployInfoDTO.getBrchanName(), null,
                createDeployInfoDTO.getJvmParam(), createDeployInfoDTO.getTargetHost(),
                createDeployInfoDTO.getSwitchBatchDeploy(), createDeployInfoDTO.getSwitchJunit());
    }

    /*---------------- dtoConvertDo DTO转DO ----------------*/

    /**
     * AutoDeployDTO -> AutoDeployDO
     */
    public static AutoDeployDO dtoConvertDo(AutoDeployDTO deployDTO) {
        return new AutoDeployDO(deployDTO.getBuildSeq());
    }

    /**
     * BuildProjectDTO -> BuildInfoDO
     */
    public static BuildInfoDO dtoConvertDo(BuildProjectDTO dto) {
        return new BuildInfoDO(dto.getSwitchJunit() == BusinessConstant.ZERO_NUM, dto.getBuildSeq());
    }

    /**
     * GetGitBrchansDTO -> GitBrchanDO
     */
    public static GitBrchanDO dtoConvertDo(GetGitBrchansDTO dto) {
        return new GitBrchanDO(dto.getGitPath());
    }

    /*---------------- create 创建实例 ----------------*/

    /**
     * 创建实例 : GetGitBrchansOutDTO
     */
    public static GetGitBrchansOutDTO createGetGitBrchansOutDTO(String gitPath, List<String> branchs) {
        return new GetGitBrchansOutDTO(gitPath, branchs);
    }

    /**
     * 创建实例 : GetGitBrchansOutDTO
     */
    public static GetGitBrchansOutDTO createGetGitBrchansOutDTO(String errorCode) {
        GetGitBrchansOutDTO getGitBrchansOutDTO = new GetGitBrchansOutDTO();
        getGitBrchansOutDTO.setErrorCode(errorCode);
        return getGitBrchansOutDTO;
    }

    /**
     * 创建实例 : GitBrchanOutDO
     */
    public static GitBrchanOutDO createGitBrchanOutDO(String gitPath, List<String> brchans) {
        return new GitBrchanOutDO(gitPath, brchans);
    }

    /**
     * 创建实例 : GitBrchanDO
     */
    public static GitBrchanDO createGitBrchanDO(String gitPath) {
        return new GitBrchanDO(gitPath);
    }

    /**
     * 创建实例 : DeployStatusOutDO
     */
    public static DeployStatusOutDO createDeployStatusOutDO(String pid, String targetHost) {
        return new DeployStatusOutDO(pid, targetHost);
    }

    /**
     * 创建实例 : QryDeployInfoOutDTO
     */
    public static QryDeployInfoOutDTO createQryDeployInfoOutDto() {
        return new QryDeployInfoOutDTO();
    }

    /**
     * 创建实例 : createDeployUpdatePo
     */
    public static DeployUpdatePO createDeployUpdatePo(String stage, String seq) {
        return new DeployUpdatePO(null, stage, seq, null);
    }

    /**
     * 创建实例 : createDeployUpdatePo 用于更新部署成功记录
     */
    public static DeployUpdatePO createDeployUpdatePoBuildSuccess(String jarPath, String stage, String seq) {
        return new DeployUpdatePO(jarPath, stage, seq, null);
    }

    /**
     * 创建实例 : createDeployUpdatePo 用于更新部署成功记录
     */
    public static ReviewStatusOutDTO createReviewStatusOutDTO() {
        return new ReviewStatusOutDTO();
    }

    /**
     * 创建实例 : createDeployUpdatePo 用于更新部署失败记录
     */
    public static DeployUpdatePO createDeployUpdatePoBuildLog(String buildLog, String stage, String seq) {
        return new DeployUpdatePO(null, stage, seq, buildLog);
    }

    /**
     * 创建实例 : DeployQryPO 用于更新唯一部署记录
     */
    public static DeployQryPO createFromBuildSeq(String seq) {
        return new DeployQryPO(seq, null, null);
    }

    /**
     * 创建实例 : DeployQryPO 用于查询用户部署记录
     */
    public static DeployQryPO createFromUserSeq(String userSeq, String deploySeq) {
        return new DeployQryPO(null, userSeq, deploySeq);
    }

    /**
     * 创建实例 : NextDeployBuildSeqOutDTO
     */
    public static NextDeployBuildSeqOutDTO createNextDeployBuildSeqOutDTO() {
        return new NextDeployBuildSeqOutDTO();
    }

    /**
     * 创建实例 : NextDeployBuildSeqOutDTO
     */
    public static NextDeployBuildSeqOutDTO createNextDeployBuildSeqOutDTO(DeployInsertPO deployInsertPo) {
        return new NextDeployBuildSeqOutDTO(deployInsertPo.getSeq());
    }

    /**
     * 创建实例 : CreateDeployInfoOutDTO
     */
    public static CreateDeployInfoOutDTO createCreateDeployInfoOutDTO(DeployInsertPO deployInsertPo) {
        return new CreateDeployInfoOutDTO(deployInsertPo.getDeploySeq());
    }

    /**
     * 创建实例 : BuildProjectOutDTO
     */
    public static BuildProjectOutDTO createBuildProjectOutDTO() {
        return new BuildProjectOutDTO();
    }

    /**
     * 创建实例 : BuildProjectOutDTO
     */
    public static BuildProjectOutDTO createBuildProjectOutDTO(String errorCode, String buildSeq) {
        BuildProjectOutDTO buildProjectOutDTO = new BuildProjectOutDTO(buildSeq);
        buildProjectOutDTO.setErrorCode(errorCode);

        return buildProjectOutDTO;
    }

    /*---------------- poConvertDto PO转DTO ----------------*/

    /**
     * DeployStageOutPO -> ReviewStatusOutDTO
     */
    public static ReviewStatusOutDTO poConvertDto(DeployStageOutPO deployStageOutPo) {
        return new ReviewStatusOutDTO(deployStageOutPo.getStageFlag());
    }

    /**
     * DeployQryOutPO -> DeployInfoDTO
     */
    public static DeployInfoDTO poConvertDto(DeployQryOutPO deployQryOutPo) {
        return new DeployInfoDTO(deployQryOutPo.getBuildSeq(), deployQryOutPo.getDeploySeq(), deployQryOutPo.getUserSeq(), deployQryOutPo.getProjectName(), deployQryOutPo.getAppName(),
                deployQryOutPo.getGitPath(), deployQryOutPo.getGitBrchan(), deployQryOutPo.getStageFlag(), deployQryOutPo.getSwitchJunit(),
                deployQryOutPo.getSwitchBatchDeploy(), deployQryOutPo.getJarPath(), deployQryOutPo.getJvmParam(), deployQryOutPo.getTargetHost(),
                deployQryOutPo.getRemarks(), deployQryOutPo.getCreateDateTime());
    }

    /**
     * List<DeployQryOutPO> -> List<DeployInfoDTO>
     */
    public static List<DeployInfoDTO> poConvertDto(List<DeployQryOutPO> list) {
        return list.stream()
                .map(DeployAssembler::poConvertDto)
                .collect(Collectors.toList());
    }

    /**
     * List<DeployQryOutPO> -> QryDeployInfoOutDTO
     */
    public static QryDeployInfoOutDTO createQryDeployInfoOutDto(List<DeployQryOutPO> deployQryOutPos) {
        return new QryDeployInfoOutDTO(DeployAssembler.poConvertDto(deployQryOutPos));
    }

    /*---------------- 通用方法 ----------------*/

    /**
     * 截取git 项目名称
     *
     * @param gitPath git路径
     * @return git路径项目名称
     */
    public static String subProjectNameFromGitPath(String gitPath) {
        String flag1 = "/";
        String flag2 = ".git";
        String result = gitPath.substring(gitPath.lastIndexOf(flag1) + 1, gitPath.lastIndexOf(flag2));
        CommandLog.info("subProjectNameFromGitPath gitPath:{}", gitPath);
        CommandLog.info("subProjectNameFromGitPath result:{}", result);

        return result;
    }
}
