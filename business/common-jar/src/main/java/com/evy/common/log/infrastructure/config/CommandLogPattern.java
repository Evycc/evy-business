package com.evy.common.log.infrastructure.config;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;

import java.lang.reflect.Field;
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
     * 只输出com.evy开头类名
     */
    private final static String EVY_PACKAGE = "com.evy";
    private final static String STACK_JOIN_STR = "=>";
    private final static String FILTER_MSG_LENGTH_KEY = "evy.log.msg.length";
    private static int FILTER_MSG_LENGTH = -1;
    private final static String LOG_VAR = "...";
    /**
     * 过滤类名集合
     */
    private final static List<String> FILTER_LIST = Stream.of(
            CommandLog.class.getName(),
            BaseCommandTemplate.class.getName()
    ).collect(Collectors.toList());

    static {
        try {
            FILTER_MSG_LENGTH = Integer.parseInt(AppContextUtils.getForEnv(FILTER_MSG_LENGTH_KEY));
        } catch (NumberFormatException ignore) {
        }
        CommandLog.info("log消息内容最大长度为: {}", FILTER_MSG_LENGTH);
    }

    @Override
    protected String getFullyQualifiedName(ILoggingEvent iLoggingEvent) {
        subLogMsgLength(iLoggingEvent);
        return BusinessConstant.VM_HOST + BusinessConstant.DOUBLE_LINE + getStackClsInfo(iLoggingEvent);
    }

    /**
     * 获取调用栈信息，包名.类名=>调用包名.类名||方法名<br/>
     * 只打印com.evy开头，其余一律输出原包名
     *
     * @param iLoggingEvent ch.qos.logback.classic.spi.ILoggingEvent
     * @return 拼接好的调用栈字符串
     */
    private String getStackClsInfo(ILoggingEvent iLoggingEvent) {
        try {
            StackTraceElement[] stackTraceElements = iLoggingEvent.getCallerData();
            if (stackTraceElements[0].getClassName().contains(CommandLog.class.getName())) {
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

                return joinStackCls(true, newStes);
            }

            return joinStackCls(false, stackTraceElements);
        } catch (Exception e) {
            return patternLogger(iLoggingEvent.getLoggerName());
        }
    }

    /**
     * 根据日志调用栈打印类方法调用信息<br/>
     * 过滤相同类名、CommandLog类等信息，只打印com.evy开头包名，其余一律输出原类名
     *
     * @param isCommandLog       true : 调用CommandLog进行打印日志 false : 非调用CommandLog打印日志
     * @param stackTraceElements 日志打印栈类方法信息，
     * @return 拼接好的调用栈字符串
     */
    private String joinStackCls(boolean isCommandLog, StackTraceElement... stackTraceElements) {
        StackTraceElement[] elements;
        if (isCommandLog) {
            elements = Stream.of(stackTraceElements)
                    .filter(stackTraceElement -> !FILTER_LIST.contains(stackTraceElement.getClassName()))
                    .distinct()
                    .collect(Collectors.toList())
                    .toArray(new StackTraceElement[]{});
        } else {
            elements = Stream.of(stackTraceElements)
                    .distinct()
                    .limit(2)
                    .collect(Collectors.toList())
                    .toArray(new StackTraceElement[]{});
        }


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
            if (--i < 0) {
                //遍历到栈的最后一个类，打印其方法
                stringBuilder.append(BusinessConstant.DOUBLE_LINE)
                        .append(elements[i + 1].getMethodName());
            }
        }

        return stringBuilder.toString();
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

    /**
     * 限制Log输出日志内容长度
     * @param iLoggingEvent ch.qos.logback.classic.spi.ILoggingEvent
     */
    private void subLogMsgLength(ILoggingEvent iLoggingEvent) {
        String content = iLoggingEvent.getMessage();
        if (FILTER_MSG_LENGTH > 0 && content.length() > FILTER_MSG_LENGTH) {
            content = content.substring(0, FILTER_MSG_LENGTH).concat(LOG_VAR);
            try {
                Field field1 = CommandUtils.getFieldByObject(iLoggingEvent, "message");
                CommandUtils.fieldAccessSet(field1, iLoggingEvent, content);
                Field field2 = CommandUtils.getFieldByObject(iLoggingEvent, "formattedMessage");
                CommandUtils.fieldAccessSet(field2, iLoggingEvent, content);
            } catch (Exception ignore) {
                //忽略异常
            }
        }
    }
}
