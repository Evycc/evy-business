package com.evy.common.log.infrastructure.config;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义%logger打印格式<br/>
 * 1:打印上一个调用类，com.evy.**开头
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/27 14:54
 */
public class CommandLogPattern extends NamedConverter {

    /**
     * 过滤类名集合
     */
    private final static List<String> FILTER_LIST = Stream.of(
            CommandLog.class.getName(),
            BaseCommandTemplate.class.getName()
    ).collect(Collectors.toList());
    /**
     * 只输出com.evy开头类名
     */
    private final static String EVY_PACKAGE = "com.evy";
    private final static String STACK_JOIN_STR = "=>";

    @Override
    protected String getFullyQualifiedName(ILoggingEvent iLoggingEvent) {
//        String rs = BusinessConstant.EMPTY_STR;
//        //格式化%logger
//        String loggerName = patternLogger(iLoggingEvent.getLoggerName());
//        //格式化%call
//        //打印上一个调用类
//        String callerName = patternCaller(iLoggingEvent, EXCLUSIONLOG);
//        String lastCallerName = patternCaller(iLoggingEvent, callerName);
//
//        rs += StringUtils.isEmpty(callerName) ? loggerName
//                : StringUtils.isEmpty(lastCallerName) ? callerName
//                : lastCallerName + "=>" + callerName;
//
//        getStackClsInfo(iLoggingEvent);
//        return rs;

        return BusinessConstant.VM_HOST + BusinessConstant.DOUBLE_LINE + getStackClsInfo(iLoggingEvent);
    }

    /**
     * 获取调用栈信息，包名.类名=>调用包名.类名||方法名<br/>
     * 只打印com.evy开头，其余一律输出原包名
     * @param iLoggingEvent ch.qos.logback.classic.spi.ILoggingEvent
     * @return  拼接好的调用栈字符串
     */
    private String getStackClsInfo(ILoggingEvent iLoggingEvent) {
        try {
            StackTraceElement[] stackTraceElements = iLoggingEvent.getCallerData();
            int start = 2;
            int end = stackTraceElements.length;
            //使用CommandLog打印日志，调用栈前两个下标一定是CommandLog的方法，跳过不打印
            for (int i = start; i < stackTraceElements.length; i++) {
                //查找com.evy开头的调用栈
                String cls = stackTraceElements[i].getClassName();
                if (!cls.startsWith(EVY_PACKAGE)) {
                    end = i;
                    break;
                }
            }
            StackTraceElement[] newStes = new StackTraceElement[end - start];
            System.arraycopy(stackTraceElements, start, newStes, 0, end - start);

            return joinStackCls(newStes);
        } catch (Exception e) {
            return patternLogger(iLoggingEvent.getLoggerName());
        }
    }

    /**
     * 根据日志调用栈打印类方法调用信息<br/>
     * 过滤相同类名、CommandLog类等信息，只打印com.evy开头包名，其余一律输出原类名
     * @param stackTraceElements    日志打印栈类方法信息，
     * @return  拼接好的调用栈字符串
     */
    private String joinStackCls(StackTraceElement... stackTraceElements) {
        StackTraceElement[] elements = Stream.of(stackTraceElements)
                .filter(stackTraceElement -> !FILTER_LIST.contains(stackTraceElement.getClassName()))
                .distinct()
                .collect(Collectors.toList())
                .toArray(new StackTraceElement[]{});

        int len = elements.length;
        StringBuilder stringBuilder = new StringBuilder();
        //记录前一个类名
        String preStr = BusinessConstant.EMPTY_STR;
        String cls;
        for (int i = len - 1; i >= 0; ) {
            cls = patternLogger(elements[i].getClassName());
            if (!preStr.equals(cls)) {
                //相同类只打印一次
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(STACK_JOIN_STR);
                }
                stringBuilder.append(cls);
                preStr = cls;
            }
            if (--i < 0){
                //遍历到栈的最后一个类，打印其方法
                stringBuilder.append(BusinessConstant.DOUBLE_LINE)
                        .append(elements[i +1].getMethodName());
            }
        }

        return stringBuilder.toString();
    }

    /**
     * %caller 获取上一个调用类
     *
     * @param iLoggingEvent event
     * @return caller
     */
    private String patternCaller(ILoggingEvent iLoggingEvent, String curClass) {
        String pre = BusinessConstant.EMPTY_STR;
        String log = "com.evy.common.log.CommandLog";
        StackTraceElement[] ste = iLoggingEvent.getCallerData();
        //从下标1开始遍历，忽略CommandLog类
        for (int i = 1; i < ste.length - 1; i++) {
            String eleClsName = ste[i].getClassName();
            //过滤指定类
            if (curClass.equals(eleClsName) || log.equals(eleClsName)) {
                continue;
            }
            if (eleClsName.startsWith(EVY_PACKAGE) && !eleClsName.contains(curClass)) {
                pre += patternLogger(eleClsName);
                break;
            }
        }

        return pre;
    }

    /**
     * 判断当前类是否在过滤类的列表里面
     *
     * @param clsName 当前类名
     * @return true 存在过滤类列表里面
     */
    private boolean isFilterClass(String clsName) {
        return FILTER_LIST.contains(clsName);
    }

    /**
     * %logger，只保留最后两个包名
     *
     * @param loggerName event
     * @return logger
     */
    private String patternLogger(String loggerName) {
        String rs = loggerName;
        if (loggerName.split(BusinessConstant.SPLIT_POINT).length > 2) {
            int lastOne = loggerName.lastIndexOf(BusinessConstant.POINT);
            int lastTwo = lastOne;
            char[] chars = loggerName.substring(0, lastOne).toCharArray();
            int clen = chars.length;
            for (int i = clen - 1; i >= 0; i--) {
                if ('.' == (chars[i])) {
                    lastTwo = i;
                    break;
                }
            }

            rs = loggerName.substring(lastTwo + 1);
        }

        return rs;
    }
}
