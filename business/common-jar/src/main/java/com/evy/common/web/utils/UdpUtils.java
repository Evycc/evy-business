package com.evy.common.web.utils;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.JsonUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * UDP工具类
 *
 * @Author: EvyLiuu
 * @Date: 2021/4/24 16:17
 */
public class UdpUtils {
    /**
     * UDP 连接超时时间,单位ms
     */
    private static int CONN_TIME_OUT = 1000;
    /**
     * UDP 报文发送及接收的长度
     */
    private static final int MESSAGE_LENGTH = 65507;
    /**
     * UDP SERVER端线程池,接收消息用
     */
    private static final ExecutorService EXECUTOR_SERVICE_PRODUCER = CreateFactory.returnExecutorService("UDPUtils-Producer");
    /**
     * UDP SERVER端线程池,处理消息用
     */
    private static final ExecutorService EXECUTOR_SERVICE_CONSUMER = CreateFactory.returnExecutorService("UDPUtils-Consumer");
    /**
     * UDP SERVER端线程池,检测UDP发送端半个小时内活跃的DatagramSocket对象
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = CreateFactory.returnScheduledExecutorService("UDPUtils-Scheduled", 1);

    /**
     * UDP SERVER端缓存map,用于统计当前存活的server及端口
     */
    private static final Map<Integer, DatagramSocket> SOCKET_SERVER_MAP = new HashMap<>(8);
    /**
     * K : hostName + ip V : DatagramSocketSender
     * UDP发送端缓存map,保留半个小时内活跃的DatagramSocket对象
     */
    private static final Map<String, DatagramSocketSender> SOCKET_SEND_MAP = new HashMap<>(8);

    static {
        AppContextUtils.getAsyncProp(businessProperties -> {
            if (Objects.nonNull(businessProperties)) {
                CONN_TIME_OUT = businessProperties.getUdp().getConnTimeOut();
            }
        });
        //1分钟后执行
        long initialDelay = 60000L;
        //间隔1分钟轮询
        long delay = 60000L;
        //30分钟
        long timeOut = 1800000;
        //间隔1分钟轮询
        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            if (SOCKET_SEND_MAP.size() > 0) {
                SOCKET_SEND_MAP.forEach((k, v) -> {
                    if (System.currentTimeMillis() - v.usedTime >= timeOut) {
                        v.destroy();
                        SOCKET_SEND_MAP.remove(k);
                    }
                });
            }
        }, initialDelay, delay, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                SOCKET_SERVER_MAP
                        .values()
                        .forEach(datagramSocket -> {
                            if (Objects.nonNull(datagramSocket)) {
                                datagramSocket.disconnect();
                            }
                        })));
    }

    /**
     * 开启一个UDP端口,单线程接收消息
     *
     * @param port     端口
     * @param consumer 接收到消息后执行操作
     * @param async    异步处理消息
     * @return true成功
     */
    private static boolean sever(int port, Consumer<byte[]> consumer, boolean async) {
        boolean result = false;
        if (!SOCKET_SERVER_MAP.containsKey(port)) {
            DatagramSocket datagramSocket;
            try {
                datagramSocket = new DatagramSocket(port);
                SOCKET_SERVER_MAP.put(port, datagramSocket);
                CommandLog.info("创建UDP SERVER PORT:{}", port);

                EXECUTOR_SERVICE_PRODUCER.execute(() -> {
                    DatagramSocket socket = SOCKET_SERVER_MAP.get(port);
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[MESSAGE_LENGTH], MESSAGE_LENGTH);
                    while (!socket.isClosed()) {
                        try {
                            socket.receive(datagramPacket);
                            InetAddress ip = datagramPacket.getAddress();
                            byte[] data = datagramPacket.getData();
                            datagramPacket.setData(new byte[MESSAGE_LENGTH]);
                            CommandLog.info("<=== UDP接收到信息,发送端IP: {}", ip.getHostAddress());
                            if (async) {
                                EXECUTOR_SERVICE_CONSUMER.execute(() -> consumer.accept(data));
                            }
                        } catch (IOException e) {
                            CommandLog.errorThrow("<=== UDP接收信息异常", e);
                        }
                    }
                    datagramPacket = null;
                });
                result = true;
            } catch (IOException e) {
                CommandLog.errorThrow("创建UDP SERVER异常", e);
            }
        }

        return result;
    }

    /**
     * 开启一个UDP端口,接收消息后同步处理消息
     *
     * @param port     端口
     * @param consumer 接收到消息后执行操作
     * @return true成功
     */
    public static boolean server(int port, Consumer<byte[]> consumer) {
        return sever(port, consumer, false);
    }

    /**
     * 开启一个UDP端口,接收消息后异步处理消息
     *
     * @param port     端口
     * @param consumer 接收到消息后执行操作
     * @return true成功
     */
    public static boolean serverAsync(int port, Consumer<byte[]> consumer) {
        return sever(port, consumer, true);
    }

    /**
     * UDP传输,最大字节65507<br/>
     * udp报文头有2个byte用于记录包体长度. 2个byte可表示最大值为: 2^16-1=64K-1=65535<br/>
     * udp报文头占8字节, ip报文头占20字节, 65535-28 = 65507
     * @param hostName 目标ip
     * @param port 目标端口
     * @param body 报文
     * @param <T> 报文类型
     * @return true成功
     */
    public static <T> boolean send(String hostName, int port, T body) {
        boolean result;
        String key = hostName + port;
        DatagramSocketSender datagramSocketSender;

        if (!SOCKET_SEND_MAP.containsKey(key)) {
            datagramSocketSender = new DatagramSocketSender(hostName, port);
            SOCKET_SEND_MAP.put(key, datagramSocketSender);
        } else {
            datagramSocketSender = SOCKET_SEND_MAP.get(key);
        }
        result = datagramSocketSender.send(body);

        return result;
    }

    /**
     * DatagramSocket发送者包装类
     */
    static class DatagramSocketSender {
        String hostName;
        int port;
        DatagramSocket datagramSocket;
        DatagramPacket datagramPacket;
        /**
         * 最近使用时间戳
         */
        long usedTime = System.currentTimeMillis();
        Charset charset;

        DatagramSocketSender(String hostName, int port, Charset charset) {
            this.hostName = hostName;
            this.port = port;
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.setSoTimeout(CONN_TIME_OUT);
                this.charset = charset;
                byte[] message = new byte[MESSAGE_LENGTH];
                datagramPacket = new DatagramPacket(message, message.length);
                datagramPacket.setAddress(InetAddress.getByName(hostName));
                datagramPacket.setPort(port);
            } catch (IOException e) {
                CommandLog.errorThrow("创建DatagramSocketSender异常", e);
            }
        }

        DatagramSocketSender(String hostName, int port) {
            this(hostName, port, StandardCharsets.UTF_8);
        }

        public <T> boolean send(T body) {
            boolean result = false;
            if (Objects.nonNull(datagramSocket)) {
                try {
                    byte[] bytes = JsonUtils.convertToJson(body).getBytes(charset);
                    CommandLog.info("Send UDP ==> Target[HostName:{} Port:{} BodyLength:{}byte]", hostName, port, bytes.length);
                    datagramPacket.setData(bytes);
                    datagramSocket.send(datagramPacket);
                    //重置对象
                    datagramPacket.setData(new byte[MESSAGE_LENGTH]);
                    result = true;
                    usedTime = System.currentTimeMillis();
                } catch (IOException e) {
                    CommandLog.errorThrow("UDP发送异常", e);
                }
            }

            return result;
        }

        public void destroy() {
            if (Objects.nonNull(datagramSocket)) {
                datagramSocket.disconnect();
            }
            if (Objects.nonNull(datagramPacket)) {
                datagramPacket = null;
            }
        }
    }
}
