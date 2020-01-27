package com.evy.common.infrastructure.common.log;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import org.springframework.util.StringUtils;

/**
 * 自定义%logger打印格式<br/>
 * 1:打印上一个调用类，com.**开头
 * @Author: EvyLiuu
 * @Date: 2019/10/27 14:54
 */
public class CommandLogPattern extends NamedConverter {
    private final static String EXCLUSIONLOG = "com.evy.common.infrastructure.common.log.CommandLog";
    private final static String EVY_PACKAGE = "com.evy";

    @Override
    protected String getFullyQualifiedName(ILoggingEvent iLoggingEvent) {
        String rs = BusinessConstant.EMPTY_STR;
        //格式化%logger
        String loggerName = patternLogger(iLoggingEvent.getLoggerName());
        //格式化%call
        //打印上一个调用类
        String callerName = patternCaller(iLoggingEvent, EXCLUSIONLOG);
        String lastCallerName = patternCaller(iLoggingEvent, callerName);

        rs += StringUtils.isEmpty(callerName) ? loggerName
                : StringUtils.isEmpty(lastCallerName) ? callerName
                : lastCallerName + "=>" + callerName;
        return rs;
    }

    /**
     * %caller 获取上一个调用类
     * @param iLoggingEvent event
     * @return  caller
     */
    private String patternCaller(ILoggingEvent iLoggingEvent, String curClass){
        String pre = BusinessConstant.EMPTY_STR;
        StackTraceElement[] ste = iLoggingEvent.getCallerData();
        //从下标1开始遍历，忽略CommandLog类
        for (int i = 1; i < ste.length -1; i++) {
            String eleClsName = ste[i].getClassName();
            //过滤指定类
            if (curClass.equals(eleClsName) || EXCLUSIONLOG.equals(eleClsName)) {
                continue;
            }
            if (eleClsName.startsWith(EVY_PACKAGE) && !eleClsName.contains(curClass)){
                pre += patternLogger(eleClsName);
                break;
            }
        }

        return pre;
    }

    /**
     * %logger，只保留最后两个包名
     * @param loggerName event
     * @return  logger
     */
    private String patternLogger(String loggerName){
        String rs = loggerName;
        if (loggerName.split(BusinessConstant.SPLIT_POINT).length > BusinessConstant.UNKNOW){
            int lastOne = loggerName.lastIndexOf(".");
            int lastTwo = lastOne;
            char[] chars = loggerName.substring(0, lastOne).toCharArray();
            int clen = chars.length;
            for (int i = clen -1; i >= 0; i--){
                if ('.' == (chars[i])){
                    lastTwo = i;
                    break;
                }
            }

            rs = loggerName.substring(lastTwo +1);
        }

        return rs;
    }
}
