package com.evy.common;

import com.evy.common.app.command.CommandExecute;
import com.evy.common.app.command.TestLogCommand;
import com.evy.common.domain.repository.mq.basic.BasicMqConsumer;
import com.evy.common.domain.repository.mq.model.MqConsumerModel;
import com.evy.common.domain.repository.mq.impl.RabbitBaseMqConsumer;
import com.evy.common.infrastructure.common.command.CommandTemplate;
import com.evy.common.infrastructure.common.command.utils.CommandUtils;
import com.evy.common.infrastructure.common.command.utils.SequenceUtils;
import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.common.inceptor.BaseCommandInceptor;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.test.TestInput;
import com.evy.common.infrastructure.tunnel.test.TestOutDTO;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void testPasswordEncodeAndDecode(){
        String pass1 = CommandUtils.encodeEnc("1q2w#E$R");
        String enc = "ENC(" + pass1 + ")";
        System.out.println("加密:" + enc);
        System.out.println("解密:" + CommandUtils.decodeEnc(enc));
    }

    @Test
    public void testSeq() throws BrokenBarrierException, InterruptedException {
        //统计耗时
//        long cur = System.currentTimeMillis();
//        int i =0;
//        while (i++ < 10000) {
//            System.out.println(SequenceUtils.nextId());
//        }
//        System.out.println("耗时: " + (System.currentTimeMillis() - cur));
//
//        System.out.println(SequenceUtils.getPrefix("tradeId"));
//        System.out.println(SequenceUtils.getPrefix("IBTC", 10));

        //测试10000并发是否存在重复
//        List<Long> longs = Collections.synchronizedList(new ArrayList<>(100));
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(10000);
//        int i =0;
//        while (i++ < 10000) {
//            new Thread(() -> {
//                try {
//                    cyclicBarrier.await();
//                    longs.add(SequenceUtils.nextId());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//        TimeUnit.SECONDS.sleep(10);
//        System.out.println(longs.size());
//        System.out.println(longs.stream()
//                .distinct()
//                .count());


        //统计每秒生成数量
//        List<Long> longs = Collections.synchronizedList(new ArrayList<>(100));
//        long cur = System.currentTimeMillis();
//        int i =0;
//        while ((System.currentTimeMillis() - cur) < 1000) {
//            longs.add(SequenceUtils.nextId());
//        }
//        System.out.println("耗时: " + (System.currentTimeMillis() - cur) + " 数量: " + longs.size() + " 是否存在重复: " +
//                longs.stream().distinct().count());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        assertTrue(true);
        List<String> list = Stream.of("a").collect(Collectors.toList());
        ArrayList<String> alist = new ArrayList<>();
        System.out.println((ArrayList)list);

        List<BasicMqConsumer> list1 = Collections.synchronizedList(new ArrayList<>());
        Object bean= new Object();
        List<MqConsumerModel> models = new ArrayList<>(){{
           add(new MqConsumerModel());
        }};
        Class cls = RabbitBaseMqConsumer.class;

        RabbitBaseMqConsumer rabbitBasicMqConsumer = new RabbitBaseMqConsumer();
        rabbitBasicMqConsumer.addConsumer(new TestLogCommand(null), models);
        list1.add(rabbitBasicMqConsumer);
        BasicMqConsumer b =list1.stream()
                .filter(basicMqConsumer -> basicMqConsumer.getClass() == cls)
                .findFirst()
                .orElse((BasicMqConsumer) cls.getDeclaredConstructor().newInstance());
        b.addConsumer(new TestLogCommand(null), models);
        if (!list1.contains(b))
            list1.add(b);

        System.out.println(list1);
    }

    /**
     * 排序CommandExecute拦截器
     */
    @Test
    public void test() {
        BaseCommandInceptor inceptor1 = new BaseCommandInceptor() {
            @Override
            public int beforeCommand(InputDTO inputDTO) {
                return 0;
            }

            @Override
            public int afterCommand(InputDTO inputDTO) {
                return 0;
            }
        };
        BaseCommandInceptor inceptor2 = new BaseCommandInceptor() {
            @Override
            public int beforeCommand(InputDTO inputDTO) {
                return 0;
            }

            @Override
            public int afterCommand(InputDTO inputDTO) {
                return 0;
            }
        };

        inceptor1.setOrder(1);
        inceptor2.setOrder(2);

        List<BaseCommandInceptor> list = new ArrayList<>();
        list.add(inceptor2);
        list.add(inceptor1);

        list = orderCommandInceptor(list);

        System.out.println(list);
        System.out.println();
    }

    /**
     * 排序CommandExecute拦截器
     *
     * @param list
     * @return
     */
    private List<BaseCommandInceptor> orderCommandInceptor(List<BaseCommandInceptor> list) {
        //test BusinessConstant临时变量
        list.stream()
                .forEach(c -> {
                    CommandUtils.setLambdaTemp(c, c.getOrder());
                });
        System.out.println(CommandUtils.getTempObject());

        return list.stream()
                .sorted(Comparator.comparingInt(BaseCommandInceptor::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * CommandExecute test
     */
    @Test
    public void testCommandExecute() throws BasicException, InterruptedException {
        TestInput input = new TestInput();
        input.setTest("1");
        input.setClientIp("1");
        input.setRequestTime("12");
        input.setSrcSendNo("3");
        new CommandExecute().start(input);
    }

    @Test
    public void testlog() {
        int i = 0;
        AtomicInteger ai = new AtomicInteger(1);
        for (i = 0; i < 100; i++) {
            new Thread(() -> {
                Thread.currentThread().setName("AI+" + ai.getAndIncrement());
                CommandLog.info("cur");
            }).start();
        }
    }

    /**
     * @throws BasicException
     */
    @Test
    public void testWhenException() throws BasicException {
        TestInput input = new TestInput();
        input.setTest("esss");
        input.setClientIp("111");
        input.setSrcSendNo("1111");
        input.setRequestTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
//        System.out.println(new CommandExecute().start(input));

        int i = 0;
        AtomicInteger ai = new AtomicInteger(1);
        for (i = 0; i < 100; i++) {
            new Thread(() -> {
                Thread.currentThread().setName("AI+" + ai.getAndIncrement());
                new CommandExecute().start(input);
            }).start();
        }
    }

    /**
     * aop test
     *
     * @throws Throwable
     */
    @Test
    public void testAOP() throws Throwable {
        JdkProxy1 jdkProxy = new JdkProxy1();
        CglibProxy1 cglibProxy = new CglibProxy1();
        CommandTemplate template = null;

        //jdk proxy
//        template = (BasicCommandTemplate) jdkProxy.getJDKProxy(new CommandExecute());
//        template = (CommandTemplate) JdkProxy2.proxy(new TestCommandIntefaced());

        //cglib
//        template = (BasicCommandTemplate) cglibProxy.getCglibProxy(new CommandExecute());
        template = (CommandTemplate) CglibProxy2.proxy(new CommandExecute());
        System.out.println(template.start(null));
    }

    @Test
    public void repl() {
        CommandLog.info("hello world");
        System.out.println(LocalDateTime.parse("20190901 12:00", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")));
    }

    @Test
    public void testBasicException() {
        System.out.println(new BasicException(new RuntimeException("xx")).toString());
    }

    @Test
    public void testCommandUtils() {
        int r1 = CommandUtils.execute(new Object(), object -> {
            try {
                TimeUnit.SECONDS.sleep(31L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }, commandExecute -> {
            CommandLog.errorThrow("", commandExecute);
        }, 0);
        System.out.println("r1: " + r1);
    }

    @Test
    public void printOutDto(){
        System.out.println(new TestOutDTO().toString());
    }

    @Test
    public void testssh() throws Exception {
        JSch jSch = new JSch();
        String pryKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAwbGxUy4o4iPy+JkQauSaWvRUQzxepJXsPaNFnLQO2EFwo+gM\n" +
                "VljE3SXz1nnxm8fbZxBEi1GQbQ2plPdcNh+roYMoIu833W6GgxeRzrByXzLMqXUU\n" +
                "OY9HNCDw2/9xTlJRVEcJHKnhFnSpG3pApL+ZDm4qyYSzncXuo2bvCcnSW1YcUJ9/\n" +
                "hnK32IPkDgBzUgHtDZgPjILKn7wCMs0efTVy1cxAmYGiCXMp4f5k8U7ZC4cIepGG\n" +
                "b6d8OddDBqgbql4dZ3o6oH9n4J3vKbwyKeKpikTGYbF1SjqIK5G/vmERCkqBvhGz\n" +
                "TgUPPNtY3K6LDghlfPcq2BRPPh04Cij+SaFMRwIDAQABAoIBAC5gqaDNEKT1DpxP\n" +
                "qgquKfdVQUfIELE/5IztpyFaS6OkJ6BXU3zpTFnXWIcmM5+lPvdygbcKFJbICq7M\n" +
                "e5vKAYJusRGMTcgLStHgttzSitAYjcCVc/5EPDxTFtjWVP733tlbGOdwFaEW1dbq\n" +
                "csf/yFoYLs6Jo6hHWFhvsRSa6m9LyCl4LCOGRbssfpgJn5AmhBYtAZfmEaj3NGSX\n" +
                "NHw2zqWkS1vnD7ZRSo8BoXr7Kpar7eGHv4ItPezhQrj6WKdrcItiqjb3M7h5sTXy\n" +
                "UjiTSKcZ9+Tlac8Bfn4iXVLyaLB2xTqF2RYZTDJY31PMAx3kKnqEiGuKDz33WgaD\n" +
                "8mR2/6ECgYEA7VI6TU5B0YOS1sp0xeI8hUuQX3APqnTKlSloGeVLtwpXYc/patay\n" +
                "RA4Z8Wu6NtwHReIvPN6ea18aTbLdtpBxN+KCOIT8Zkq6v22W7CqLoCfKjpOjw3k6\n" +
                "3hknM9NQdp6lomn/nx0C9j/N2lOL2oOt0BK0tZUZuoeVckLkFaqZ7LMCgYEA0PBs\n" +
                "zg5jQcwg2xK6/qbQO+T9UOM5vXNtt39Rrz9v6zqKMVVVdv8N4sF8r5zd00sgaW52\n" +
                "f/Vz0sLeHWxf73sXHi5SS/Nr3JcrZcXgXDPYvvQm1kPzOMJkhqTWSxjxvjMCruMg\n" +
                "knaISsjlROdPmoSffPcp8GYciLSYenE6tsI+lB0CgYAVVvCBJQlMEp2wjLHlXUt9\n" +
                "3OaVflkaRbg1o2yF75VYVyi2VeCyR63t6qqwh7RN3IkW4pjGy2nJU9rko7HGCcT0\n" +
                "dnypImQjupKCRhNtFwJJG2reWS4/lHYTpIVe5qB0mh1gtlFS+GPcyq3y9NI9itul\n" +
                "OxlevpJ6sFhi1tr1i8qdewKBgFY4IG4XedYZCiXc4NVcuOk+YVoTqiwHiesC2w5a\n" +
                "OzDygzxAXngwX+aGuZEuRkt+4O0g7D15aEY8q7OwmRYV0UBfFQ7KcGQi7YGd2iuy\n" +
                "KWsDDVO1zpRJg3ocY2Gx9W/rVNweVb6asztJE3MPkMXOnJHQ3LmbKYMwIwvD1dq8\n" +
                "5V3xAoGBALnpT+JwUGDLdER6VSYxir+lxIe9GS+AnbuyrC/2gLTCPcwhluD4NMjQ\n" +
                "WChrM54gRu7ZVpLthJJECu1UX9KU4Wap2xivN1bCXfgFBcIJD5nHNGVATFVEWkYd\n" +
                "pVju793nMycQdg+P88BPt2E2nwvhQHVRQt5t3pdJCTyfEnOt+Trp\n" +
                "-----END RSA PRIVATE KEY-----\n";
//        jSch.addIdentity("F:\\demo\\business\\test-demo\\src\\main\\resources\\id_rsa");
        jSch.addIdentity("pryKey", pryKey.getBytes(), null, null);
        Session session = jSch.getSession("root", "192.168.152.128", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(3000);
        if (session.isConnected()){
            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(3000);
            if (sftp.isConnected()) {
                //do sftp
                System.out.println("SFTP已连接");

                //测试获取ls文件名列表
                Vector<ChannelSftp.LsEntry> vector = sftp.ls("/root/ssh-dir/test-demo");
                sftp.lcd("");
                vector.forEach(lsEntry -> {
                    if (!lsEntry.getFilename().equals(".") && !lsEntry.getFilename().equals("..")) {
                        String path = "/root/ssh-dir/test-demo" + "/" + lsEntry.getFilename();
                        String local = "C:\\Users\\50466\\Desktop" + File.separator + lsEntry.getFilename();
                        System.out.println("获取文件: " + path);
                        try {
                            sftp.get(path, new FileOutputStream(lsEntry.getFilename()));
                        } catch (SftpException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
    }

    @Test
    public void isNo() {
        boolean autoCommit = true;
        System.out.println((!autoCommit && false));

        int i = 0;
        System.out.println(++i % 3000 == 0);
    }
}
