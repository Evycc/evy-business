package com.evy.linlin.testdemo;

import com.evy.common.app.test.command.CommandExecute;
import com.evy.common.batch.BatchUtils;
import com.evy.common.batch.FtpUtils;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.db.DBUtils;
import com.evy.common.http.HttpUtils;
import com.evy.common.http.tunnel.dto.HttpRequestDTO;
import com.evy.common.infrastructure.tunnel.test.TestInput;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.MqConsumer;
import com.evy.common.mq.common.app.basic.MqSender;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.trace.service.TraceAppMemoryInfo;
import com.evy.common.trace.service.TraceService;
import com.evy.common.trace.service.TraceThreadInfo;
import com.evy.common.utils.CommandUtils;
import com.evy.linlin.HelloDto;
import com.evy.linlin.HelloOutDto;
import com.jcraft.jsch.SftpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDemoApplicationTests {
    @Autowired
    CommandExecute commandExecute;
    @Autowired
    TestBean1 testBean1;
    @Autowired
    MqConsumer rabbitMqConsumer;
    @Autowired
    MqSender mqSender;

    @Test
    public void testServerCommand() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
//        RedisConnectionFactory redisConnection = AppContextUtils.getBean(RedisConnectionFactory.class);
//        redisConnection.getConnection().serverCommands().getConfig("*");

//        new LettuceConnectionFactory("127.0.0.1",6380),
//                RedisSerializationContext.fromSerializer(new StringRedisSerializer())

//        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
//                .clientResources(AppContextUtils.getBean(ClientResources.class)).build();
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("127.0.0.1", 6380);
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfiguration);
//
//        factory.afterPropertiesSet();
//        factory.initConnection();
//
//        ReactiveRedisTemplate<String, String> template = new ReactiveRedisTemplate<String, String>(
//                factory,
//                RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
//        template
//                .listenTo(ChannelTopic.of("__sentinel__:hello"))
//                .timeout(Duration.ofSeconds(3))
//                .subscribe(stringStringMessage -> {
//                    CommandLog.info("{}", stringStringMessage);
//        }, Throwable::printStackTrace, () -> {CommandLog.info("??");});

//        int i = 0;
//        while (i++ < 100){
//            new Thread(() -> {
//                System.out.println("DeadLockDemo 子线程： " + Thread.currentThread().getId());
//                try {
//                    LockDemo.ttlock();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//
//        TimeUnit.SECONDS.sleep(2);
//        TraceThreadInfo.executeThreadInfo();
//        TraceRedisInfo.executeRedisInfo();

        com.evy.common.trace.service.TraceService.executeService();

        TimeUnit.SECONDS.sleep(120);
    }

    static class LockDemo {
        private static Object obj = new Object();
        public synchronized static void ttlock() throws InterruptedException {
            System.out.println("ttlock");
            Thread.sleep(1000);
        }
        public static void tslock() throws InterruptedException {
            synchronized (obj){
                System.out.println("tslock");
                //此段会抛出java.lang.IllegalMonitorStateException异常
                //调用wait()或者notify()之前，必须使用synchronized语义绑定住被wait/notify的对象
//            Thread.currentThread().wait(1000 * 30);
                obj.wait(1000);
                obj.notify();
            }
        }
    }

    public static List<String> getFilterFile(File file, Predicate<String> filter) {
        List<String> result = new ArrayList<>();
        if (file.isFile() && filter.test(file.getName())) {
            result.add(file.getPath());
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isFile() && filter.test(file1.getName())) {
                        result.add(file1.getPath());
                    } else {
                        List<String> var1 = getFilterFile(file1, filter);
                        if (!var1.isEmpty()) {
                            result.addAll(var1);
                        }
                    }
                }
            }
        }

        return result;
    }

    @Test
    public void testReturnDtoParam(){
        String s = CommandUtils.returnDtoParam(new TestInput());
        System.out.println(s);
    }

    @Test
    public void codeTest() throws ClassNotFoundException {
        OutDTO outDTO = new OutDTO();
        outDTO.setErrorCode("1");
        outDTO.setErrorMsg("2");
        HelloOutDto helloOutDto = new HelloOutDto();

        HelloServiceImpl helloService = new HelloServiceImpl();
        HelloDto helloDto = new HelloDto();
        helloDto.setRequestTime("1");
        helloDto.setClientIp("2");
        helloDto.setSrcSendNo("22");
//        System.out.println(helloService.convertDto(helloOutDto, outDTO));
        System.out.println(helloService.hello(helloDto));

        Class.forName("com.evy.linlin.HelloService");
    }

    @Test
    public void testHttp() throws IOException, URISyntaxException, InterruptedException {
        String path = "https://passport.csdn.net/v1/register/pc/officialAccount/checkLogin";
        String context = "application/json;charset=utf-8";
        HttpRequestDTO<HttpPost> httpPostHttpRequestDTO = HttpRequestDTO.create(
                path, new HttpPost(), null,
                Stream.of(new BasicNameValuePair("name","value")).collect(Collectors.toList()),
                Stream.of(new BasicHeader("content-type",context)).collect(Collectors.toList())
        );
        HttpUtils.httpRequest(httpPostHttpRequestDTO, response -> "Hello World");
        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void testDBUtils() throws BasicException, InterruptedException {
        Map<String, String> insertMap = new HashMap<>();
        insertMap.put("name", "EVYliuu1");
        insertMap.put("age", "18");
        //insert
//        int ir1 = DBUtils.insert("com.evy.linlin.testdemo.TestMapper.insert", insertMap);

        long startTime;
//        startTime = System.currentTimeMillis();
//        for (int i=0; i < 3000; i++){
//            DBUtils.insert("com.evy.linlin.testdemo.TestMapper.insert", insertMap);
//        }
//        System.out.printf("for批量insert耗时:%s(ms)\n", (System.currentTimeMillis() - startTime));
//
//        insertBatch
        startTime = System.currentTimeMillis();
        List<Map<String, String>> bl1 = new ArrayList<>();
        bl1.add(new HashMap<>() {{
            put("name", "EvyLinlin");
            put("age", "18");
        }});

        //100001
        for (int i=0; i <10; i++){
            bl1.add(insertMap);
        }
//        bl1.add(insertMap);
//        bl1.add(insertMap);
        System.out.println(DBUtils.insertBatch("com.evy.linlin.testdemo.TestMapper.insert", bl1, 0));
        System.out.printf("for批量insert耗时:%s(ms)\n", (System.currentTimeMillis() - startTime));
//
////        update
//        int ui1 = DBUtils.batchAny(new ArrayList<>() {{
//            add(DBUtils.BatchModel
//                    .builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.insert")
//                    .map(new HashMap<>() {{
//                        put("name", "del");
//                        put("age", "0");
//                    }})
//                    .type(DBUtils.BatchType.INSERT)
//                    .build());
//            add(DBUtils.BatchModel.builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.update")
//                    .type(DBUtils.BatchType.UPDATE)
//                    .map(new HashMap<>() {{
//                        put("name", "del");
//                        put("age", "998");
//                    }})
//                    .build());
//            add(DBUtils.BatchModel
//                    .builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.delete")
//                    .map(new HashMap<>(){{
//                        put("name","del");
//                    }})
//                    .type(DBUtils.BatchType.DELETE)
//                    .build());
//        }});
//        System.out.println("update: " + ui1);

        //batch update
//        List<DBUtils.BatchModel> modelList = new ArrayList<>();
//        for (int i =0; i < 3000; i++){
//            modelList.add(DBUtils.BatchModel
//                    .builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.deleteAll")
//                    .type(DBUtils.BatchType.DELETE)
//                    .build());
//            modelList.add(DBUtils.BatchModel
//                    .builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.insert")
//                    .map(new HashMap<>() {{
//                        put("name", "del");
//                        put("age", "0");
//                    }})
//                    .type(DBUtils.BatchType.INSERT)
//                    .build());
//            modelList.add(DBUtils.BatchModel.builder()
//                    .mapper("com.evy.linlin.testdemo.TestMapper.insert")
//                    .type(DBUtils.BatchType.INSERT)
//                    .map(new HashMap<>() {{
//                        put("name", "del");
//                        put("age", "998");
//                    }})
//                    .build());
//        }
//        int r1 = DBUtils.batchAny(modelList);
//        System.out.println("update: " + r1);

        //selectList

        //select

        //delete

        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void test() throws SQLException, InterruptedException {
        ResultSet set = DBUtils.getDataSource().getConnection().prepareStatement(
                "select * from trace_http_flow join test_table where thf_req_ip in (select tmf_req_ip from trace_mq_flow);"
        ).executeQuery();
        if (set.next()) {
            System.out.println(set.getString(2));
        }

        Map<String, String> map = DBUtils.selectOne("com.evy.linlin.testdemo.TestMapper.selectOne");
//        System.out.println(map);

        List<Map> lists = DBUtils.selectList("com.evy.linlin.testdemo.TestMapper.selectAll");
//        System.out.println(lists);

        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void testEvent() throws InterruptedException {
        MqSendMessage mqSendMessage = MqSendMessage.builder()
                .topic("topic-command-test")
                .tag("rk-command-test")
                .consumerTag("queue-command-test")
                .messageId(String.valueOf(UUID.randomUUID()))
                .build();
        mqSender.sendAndConfirm("topic-command-test", "rk-command-test",
                "queue-command-test", "fuck");
        mqSender.sendAndConfirm("topic-command-test", "rk-command-test",
                "queue-command-test", "fuck2");
        TimeUnit.SECONDS.sleep(1000);
    }

    @Test
    public void contextLoads() throws InterruptedException {
        TestInput testInput = new TestInput();
        testInput.setSrcSendNo("1");
        testInput.setRequestTime("111");
        testInput.setClientIp("1");
        testInput.setTest("111");
        System.out.println(commandExecute.start(testInput));
        System.out.println(
                testBean1.start(new TestInput())
        );
//        rabbitMqConsumer.init();
        TimeUnit.SECONDS.sleep(600);
    }

    @Test
    public void testMqSend() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        System.out.println(mqSender.sendAndConfirm("topic-command-test1", "rk-command-test", "", "Hello World"));

//        System.out.println(mqSender.sendAndConfirm(
//                "node2-exchange",
//                "node2-rk1", "", "Hello World"));

//        for (int i = 0; i < 10000; i++)
//            System.out.println(mqSender.sendAndConfirm("topic-command-test", "rk-command-test", "", "Hello World"));


        System.out.printf("耗时: %s(ms)\n", System.currentTimeMillis() - startTime);
        TimeUnit.SECONDS.sleep(1000);
    }

    @Test
    public void testDelaySend() throws InterruptedException {
        long startTime = System.currentTimeMillis();

//        System.out.println(mqSender.sendDelay("topic-command-test1", "rk-command-test", "",
//                "Hello World", TimeUnit.SECONDS, 10L));

        System.out.println(mqSender.sendTiming("topic-command-test1", "rk-command-test", "",
                "Hello World", TimeUnit.SECONDS, 10L, "20200614 13:57:40", "yyyyMMdd HH:mm:ss"));

        System.out.printf("耗时: %s(ms)\n", System.currentTimeMillis() - startTime);
        TimeUnit.SECONDS.sleep(1000);
    }


    @Test
    public void tt() {
        System.out.println(commandExecute.start(new TestInput()));
        System.out.println(BatchUtils.providerBatch("auto_test_batch", "test_cid", "Hello World"));
    }

    @Test
    public void testNoTopicMQ() {
        mqSender.sendAndConfirm("notopic", "notag", "ctag", "err");
    }

    @Test
    public void testCommandUtils() throws InterruptedException {
        int r1 = CommandUtils.execute(commandExecute, object -> {
            commandExecute.start(new TestInput());
            return 0;
        }, commandExecute -> {
            CommandLog.errorThrow("", commandExecute);
        }, 0);
        System.out.println("r1: " + r1);
        TimeUnit.SECONDS.sleep(10L);
    }

    @Test
    public void testSFTP() throws Exception {
        //本地已存在文件，下载
        //本地不存在文件目录，下载
        //文服已存在文件，上传
        //文服已存在目录，上传
        //文服不存在目录，上传
        FtpUtils.doSftp(sftp -> {
            try {
                sftp.ls("/").forEach(consumer -> {
                    System.out.println(consumer);
                });
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
    }
}
