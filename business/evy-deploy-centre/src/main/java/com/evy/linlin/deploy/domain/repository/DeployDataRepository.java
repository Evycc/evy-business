package com.evy.linlin.deploy.domain.repository;

import com.evy.common.db.DBUtils;
import com.evy.linlin.deploy.domain.tunnel.po.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作数据表public_deploy_info
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:18
 */
@Repository
public class DeployDataRepository {
    private static final String QRY_DEPLOY_INFO_FOR_SEQ = "DeployMapper.qryDeployInfoForSeq";
    private static final String QRY_DEPLOY_INFOS_FOR_USERSEQ = "DeployMapper.qryDeployInfoListForUserSeq";
    private static final String UPDTAE_DEPLOY_STAGE = "DeployMapper.updateBuildStatus";
    private static final String INSERT_NEW_DEPLOY_INFO = "DeployMapper.insertBuildInfo";
    private static final String QRY_FRO_SEQ = "DeployMapper.qryDeployInfoForSeq";
    private static final String QRY_STAGE_FOR_SEQ = "DeployMapper.qryStageForSeq";

    /**
     * 根据buildSeq查询唯一部署状态
     */
    public DeployStageOutPO qryStageForSeq(DeployQryPO qryPo) {
        return DBUtils.selectOne(QRY_STAGE_FOR_SEQ, qryPo);
    }

    /**
     * 根据buildSeq查询唯一部署记录
     */
    public DeployQryOutPO qryForSeq(DeployQryPO qryPo) {
        return DBUtils.selectOne(QRY_DEPLOY_INFO_FOR_SEQ, qryPo);
    }

    /**
     * 根据userSeq查询客户所有部署记录
     */
    public List<DeployQryOutPO> qryForUserSeq(DeployQryPO qryPo) {
        return DBUtils.selectList(QRY_DEPLOY_INFOS_FOR_USERSEQ, qryPo);
    }

    /**
     * 更新部署阶段状态
     */
    public Integer updateStage(DeployUpdatePO deployUpdatePO) {
        return DBUtils.update(UPDTAE_DEPLOY_STAGE, deployUpdatePO);
    }

    /**
     * 添加一条新部署记录
     */
    public Integer insertDeployInfo(DeployInsertPO deployInsertPo) {
        return DBUtils.insert(INSERT_NEW_DEPLOY_INFO, deployInsertPo);
    }
}
