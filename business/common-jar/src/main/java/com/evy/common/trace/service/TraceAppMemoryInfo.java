package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceMemoryInfoPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceMemoryQueryPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.web.utils.UdpUtils;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 记录应用内存信息<br/>
 * 记录应用服务器系统内存、应用堆内存、应用内存、CPU占比<br/>
 * 配置 : evy.trace.memory.flag={0开启|1关闭} | evy.trace.memory.limit 内存最多记录数,默认1分钟记录一次,10表示记录10分钟内的记录数<br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/7/5 14:59
 */
public class TraceAppMemoryInfo {
    private static boolean MEMORY_PRPO = false;
    private static final MemoryMXBean MEMORY_MX_BEAN = ManagementFactory.getMemoryMXBean();
    private static final OperatingSystemMXBean SYSTEM_MX_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final String MEMORY_INFO_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.insertAppMermoryInfo";
    private static final String MEMORY_INFO_QUERY_COUNT = "com.evy.common.trace.repository.mapper.TraceMapper.queryCountMermoryInfoByIp";
    private static final String MEMORY_INFO_DELETE_LAST_ONE = "com.evy.common.trace.repository.mapper.TraceMapper.deleteMermoryInfoByLastOne";
    private static final String MEMORY_INFO_QUERY_LAST_ONE = "com.evy.common.trace.repository.mapper.TraceMapper.queryLastId";

    static {
        AppContextUtils.getAsyncProp(businessProperties -> {
            if (Objects.nonNull(businessProperties)) {
                MEMORY_PRPO = businessProperties.getTrace().getMemory().isFlag();
            }
        });
    }

    /**
     * evy.trace.memory.flag 为0，则采集内存信息
     */
    public static void executeMemoryInfo() {
        if (MEMORY_PRPO) {
            try {
                execute();
            } catch (Exception exception) {
                CommandLog.errorThrow("executeThreadInfo执行异常", exception);
            }
        }
    }

    /**
     * 通过ManagementFactory.getMemoryMXBean()获取应用堆内存、CPU信息
     */
    public static void execute() {
        //堆内存信息  运行时数据区域，从中分配了所有类实例和数组的内存
        MemoryUsage heapMemory = MEMORY_MX_BEAN.getHeapMemoryUsage();
        //非堆内存 类的结构，例如运行时常量池，字段和方法数据，以及方法和构造函数
        MemoryUsage nonHeapMemory = MEMORY_MX_BEAN.getNonHeapMemoryUsage();
        //系统最大物理内存
        long sysMaxMemory = SYSTEM_MX_BEAN.getTotalPhysicalMemorySize();
        //系统空闲物理内存
        long sysFreeMemory = SYSTEM_MX_BEAN.getFreePhysicalMemorySize();
        //系统可用物理内存
        long sysAvailableMemory = sysMaxMemory - sysFreeMemory;
        //应用占用内存
        long appUseMemory = Runtime.getRuntime().totalMemory();
        //cpu 负载 单位%
        BigDecimal sysCpuLoad = new BigDecimal(String.valueOf(SYSTEM_MX_BEAN.getSystemCpuLoad()))
                .setScale(2, RoundingMode.HALF_UP);

        String tamiCpuLoad = String.valueOf(sysCpuLoad);
        String tamiSysMemory = String.valueOf(sysMaxMemory);
        String tamiSysAvailMemory = String.valueOf(sysAvailableMemory);
        String tamiAppUseMemory = String.valueOf(appUseMemory);
        String tamiAppHeapMaxMemory = String.valueOf(heapMemory.getMax());
        String tamiAppHeapMinMemory = String.valueOf(heapMemory.getInit());
        String tamiAppHeapUseMemory = String.valueOf(heapMemory.getUsed());
        String tamiAppNonHeapMaxMemory = String.valueOf(nonHeapMemory.getMax());
        String tamiAppNonHeapMinMemory = String.valueOf(nonHeapMemory.getInit());
        String tamiAppNonHeapUseMemory = String.valueOf(nonHeapMemory.getUsed());

        TraceMemoryInfoPO traceMemoryInfoPo = TraceMemoryInfoPO.create(tamiCpuLoad, tamiSysMemory, tamiSysAvailMemory, tamiAppUseMemory, tamiAppHeapMaxMemory,
                tamiAppHeapMinMemory, tamiAppHeapUseMemory, tamiAppNonHeapMaxMemory, tamiAppNonHeapMinMemory, tamiAppNonHeapUseMemory);

        if (HealthyInfoService.isIsHealthyService()) {
            UdpUtils.send(HealthyInfoService.getHostName(), HealthyInfoService.getPort(), HealthyInfoModel.create(traceMemoryInfoPo));
        } else {
            addMemoryInfo(traceMemoryInfoPo);
        }
    }

    /**
     * 更新服务器内存信息
     * @param traceMemoryInfoPo com.evy.common.trace.infrastructure.tunnel.po.TraceMemoryInfoPO
     */
    public static void addMemoryInfo(TraceMemoryInfoPO traceMemoryInfoPo) {
        int limit = AppContextUtils.getProp().getTrace().getMemory().getLimit();
        int count = DBUtils.selectOne(MEMORY_INFO_QUERY_COUNT, TraceMemoryQueryPO.create(limit));
        if (count == BusinessConstant.ONE_NUM) {
            DBUtils.delete(MEMORY_INFO_DELETE_LAST_ONE, TraceMemoryQueryPO.create(DBUtils.selectOne(MEMORY_INFO_QUERY_LAST_ONE)));
        }

        DBUtils.insert(MEMORY_INFO_INSERT, traceMemoryInfoPo);
    }
}
