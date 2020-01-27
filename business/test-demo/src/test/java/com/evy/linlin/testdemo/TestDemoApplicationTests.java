package com.evy.linlin.testdemo;

import com.evy.common.app.command.CommandExecute;
import com.evy.common.domain.repository.db.DBUtils;
import com.evy.common.domain.repository.mq.MqConsumer;
import com.evy.common.domain.repository.mq.MqSender;
import com.evy.common.infrastructure.common.batch.BatchUtils;
import com.evy.common.infrastructure.common.batch.FtpUtils;
import com.evy.common.infrastructure.common.command.CommandUtils;
import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.tunnel.OutDTO;
import com.evy.common.infrastructure.tunnel.test.TestInput;
import com.evy.linlin.HelloDto;
import com.evy.linlin.HelloOutDto;
import com.evy.linlin.HelloService;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public void testDBUtils() throws BasicException {
        Map<String, String> insertMap = new HashMap<>();
        insertMap.put("name", "EVYliuu1");
        insertMap.put("age", "18");
        //insert
        int ir1 = DBUtils.insert("com.evy.linlin.testdemo.TestMapper.insert", insertMap);

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
        bl1.add(insertMap);
        bl1.add(insertMap);
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
    }

    @Test
    public void test() throws SQLException {
        ResultSet set = DBUtils.getDataSource().getConnection().prepareStatement(
                "SELECT * FROM test_table"
        ).executeQuery();
        if (set.next()) {
            System.out.println(set.getString(2));
        }

        Map<String, String> map = DBUtils.selectOne("com.evy.linlin.testdemo.TestMapper.selectOne");
        System.out.println(map);

        List<Map> lists = DBUtils.selectList("com.evy.linlin.testdemo.TestMapper.selectAll");
        System.out.println(lists);
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
                "Hello World", TimeUnit.SECONDS, 10L, "20200126 23:33", "yyyyMMdd HH:mm"));

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
