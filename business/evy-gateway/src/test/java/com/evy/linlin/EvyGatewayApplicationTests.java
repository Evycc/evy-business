package com.evy.linlin;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EvyGatewayApplicationTests {

    @Autowired
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    public ReactiveRedisTemplate<String, RouteDefinition> rt;

//	@Autowired
//	RedisTemplate<String, RouteDefinition> redisTemplate;

    @Test
    public void testRedis() throws InterruptedException {
//		System.out.println("start");
//		redisTemplate.<String,String>opsForHash()
//				.put("a","b","redistest")
//				.subscribe();
//		redisTemplate.opsForHash()
//				.get("a","b")
//				.log()
//				.switchIfEmpty(Mono.just("null"))
//				.map(obj -> {
//					System.out.println("1111");
//					System.out.println(obj);
//					return obj;
//				})
//				.subscribe(s ->{
//					System.out.println("sub");
//					System.out.println(s);
//				});
//		String modifyTime = redisTemplate.<String, String>opsForHash().get("a", "b");
//		System.out.println(StringUtils.isEmpty(modifyTime));


        reactiveRedisTemplate
                .listenTo(ChannelTopic.of("__keyevent@0__:expired"))
                .subscribe(consumer -> {
                    System.out.println(consumer);
                    System.out.println(consumer.getMessage());
                    System.out.println(consumer.getChannel());
                });

        TimeUnit.SECONDS.sleep(1000);

        reactiveRedisTemplate.opsForHash()
                .remove("a", "bb")
                .subscribe(count -> {
                    System.out.println(count);
                });
        reactiveRedisTemplate.opsForHash()
                .put("a", "b", "c")
                .subscribe();
        Mono.just("sss")
                .flatMap(s -> {
                    System.out.println(s);
                    return Mono.empty();
                }).subscribe();
        Mono.empty()
                .subscribe(s -> System.out.println("nuuuu"));

        Flux.fromIterable(
                this.rt.<String, RouteDefinition>opsForHash()
                        .values("a")
                        .switchIfEmpty(Flux.just(new RouteDefinition()))
                        .toIterable()
        );
    }

    @Test
    public void contextLoads() {
        System.out.println("2019-12-29 22:20:14" + 1);

        AtomicInteger has = new AtomicInteger();
        Flux.just("a", "b")
                .filter(c -> c.equals("c"))
//				.switchIfEmpty(Flux.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: "
//						+ "From RedisRouteDefinitionRepository"))))
                .switchIfEmpty(Mono.just("c"))
                .map(s -> {
                    System.out.println("map:" + s);
                    return "map";
                })
//                .switchIfEmpty(s -> has.set(1))
//				.defaultIfEmpty("")
                .subscribe(con -> System.out.println(StringUtils.isEmpty(con) + con + "1"));

        System.out.println(has.get());

        List<String> list = new ArrayList<>() {{
            add("/test1");
            add("/test2");
        }};
        Map<String, List<String>> map = new HashMap<>() {{
            put("pattern", list);
        }};
        String p = new Gson().toJson(
                new HashMap<>() {{
                    put("ff", "tt");
                }}
        );

        System.out.println(p);
        System.out.println(new Gson().fromJson("{\"patterns\":\"/test1,/test2\"}", Map.class));
    }

    @Test
    public void testHttpClient() throws IOException {
        //HttpUriRequest    GET POST请求实例
        //HttpHost
        //HttpRequest
        //HttpContext
        //ResponseHandler

        //GET
        HttpClient httpClient = HttpClients.createDefault();

        //GET 构造参数
//        URI uri = new URIBuilder().addParameter().build();

        HttpResponse response1 = httpClient.execute(new HttpGet("http://192.168.58.1:8080/test2"));
        System.out.println(response1);
        InputStream content = response1.getEntity().getContent();   //返回正文
        BufferedInputStream bis = new BufferedInputStream(content);
        byte[] bytes = new byte[2048];
        StringBuffer stringBuffer = new StringBuffer();
        int n = -1;

        while ((n = bis.read(bytes, 0, 2048)) != -1){
            stringBuffer.append(new String(bytes, 0, n, "UTF-8"));
        }
        System.out.println(stringBuffer.toString());

        //POST
        HttpClient httpClient1 = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost();

        //构造参数
        String jsonparam = "{\"srcSendNo\":\"123\",\"requestTime\":\"444\",\"clientIp\":\"52\",\"helloId\":\"hii\"}";
        HttpEntity httpEntity = new StringEntity(jsonparam,
                ContentType.create("application/json", "UTF-8"));
        httpPost.setEntity(httpEntity);
        httpPost.setURI(URI.create("http://192.168.58.1:8080/test1"));
        HttpResponse httpResponse = httpClient1.execute(httpPost);
        BufferedInputStream bis1 = new BufferedInputStream(httpResponse.getEntity().getContent());

        byte[] bytes1 = bis1.readAllBytes();
        System.out.println(new String(bytes1, "UTF-8"));

        //File类型
//        FileEntity fileEntity = new FileEntity(new File(""), ContentType.MULTIPART_FORM_DATA);

        //from-data表单类型
        List<NameValuePair> list = new ArrayList<>();
//        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list, "UTF-8");
    }
}
