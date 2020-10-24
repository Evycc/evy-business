package com.evy.common.command.infrastructure.constant;

import com.evy.common.log.CommandLog;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 常量字段及静态方法
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public class BusinessConstant {
    private BusinessConstant() {
    }

    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final int SUCESS = 0;
    public static final int FAILED = 1;
    public static final int UNKNOW = 2;
    public static final String EMPTY_STR = "";
    public static final String WHITE_EMPTY_STR = " ";
    public static final String SPLIT_LINE = "\\|";
    public static final String LINE = "|";
    public static final String DOUBLE_LINE = "||";
    public static final String SPLIT_DOUBLE_LINE = "\\|\\|";
    public static final String SPLIT_POINT = "\\.";
    public static final String POINT = ".";
    public static final String COMMA = ",";
    public static final String ENC_PRE_STR = "ENC(";
    public static final String STRIKE_THROUGH_STR = "-";
    public static final String COLON_STR = ":";
    public static final String LINE_FEED_STR = "\n";
    /**
     * 临时表通常为<subquery> 尖括号包裹
     */
    public static final String TMP_TABLE_NAME = "<";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String LEFT_PARENTHESES = "(";
    public static final String RIGHT_PARENTHESES = ")";
    public static final String AT_STR = "@";
    public static final String FORWARD_SLASH_STR = "/";
    public static final String SHARE_STR = "#";

    /**
     * 可用cpu数量
     */
    public static final int CORE_CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 机器host
     */
    public static String VM_HOST;
    /**
     * jvm管理Bean
     */
    private static final RuntimeMXBean RUNTIME_MX_BEAN = ManagementFactory.getRuntimeMXBean();
    /**
     * MAC地址，为空则取随机数
     */
    public static long MAC_ID;

    /**
     * jvm进程PID
     */
    public static final long WORK_PID = RUNTIME_MX_BEAN.getPid();

    /**
     * jvm运行时间
     *
     * @return jvm运行时间 long
     */
    public static long getVmUpTime() {
        return RUNTIME_MX_BEAN.getUptime();
    }

    static {
        Long val = getVmUpTime();
        try {
            String defaultIpKey = "AppLocalhost";
            String defaultIp = "127.0.0.1";
            InetAddress inetAddress = InetAddress.getLocalHost();
            VM_HOST = inetAddress.getHostAddress();

            if (defaultIp.equals(VM_HOST)) {
                String vmAppIp = System.getProperty(defaultIpKey);
                if (!StringUtils.isEmpty(vmAppIp)) {
                    VM_HOST = vmAppIp;
                }
            }

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

            byte[] bytes = networkInterface.getHardwareAddress();
            if (bytes != null && bytes.length > 1) {
                for (byte b : bytes) {
                    val += (long) b;
                }
            }
        } catch (Exception e) {
            CommandLog.errorThrow("当前机器不存在网卡，获取MAC地址失败", e);
        }
        MAC_ID = val;
    }
}
