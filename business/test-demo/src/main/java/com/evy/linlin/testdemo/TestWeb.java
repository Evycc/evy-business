package com.evy.linlin.testdemo;

import com.evy.common.log.CommandLog;
import com.evy.linlin.HelloDto;
import com.evy.linlin.gateway.EvyGatewayRequestUtils;
import com.evy.linlin.gateway.GatewayInputDTO;
import com.evy.linlin.gateway.GatewayOutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 22:03
 */
@RestController
public class TestWeb {
    @Autowired
    private EvyGatewayRequestUtils<GatewayInputDTO, GatewayOutDTO> evyGatewayRequestUtils;

    @RequestMapping("/test")
    public String test(){
        try {
            HelloDto inputDTO = new HelloDto();
            inputDTO.setServiceCode("helloService2Impl");
            inputDTO.setHelloId("htttlo");
            inputDTO.setClientIp("127.0.0.1");
            inputDTO.setRequestTime("11");
            inputDTO.setSrcSendNo("1111");
            GatewayOutDTO gatewayOutDTO = evyGatewayRequestUtils.requestJson(inputDTO, GatewayOutDTO.class);
            CommandLog.info("{}", gatewayOutDTO);
            return gatewayOutDTO.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return "HELLO";
        }
    }
}
