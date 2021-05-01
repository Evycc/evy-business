package com.evy.linlin.testdemo;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.HelloDto;
import com.evy.linlin.HelloOutDto;
import com.evy.linlin.gateway.EvyGatewayRequestUtils;
import com.evy.linlin.gateway.GatewayInputDTO;
import com.evy.linlin.gateway.GatewayOutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/12/5 16:10
 */
@RestController("helloService2Impl")
@TraceLog
public class HelloService2Impl extends BaseCommandTemplate<HelloDto, HelloOutDto> {
    @Autowired
    private EvyGatewayRequestUtils<GatewayInputDTO, GatewayOutDTO> evyGatewayRequestUtils;
    @Override
    public HelloOutDto execute(HelloDto helloDto) throws BasicException {
        HelloOutDto outDto = new HelloOutDto();
        outDto.setErrorCode("0");
        outDto.setErrorMsg("成功");

        //试验服务化调用trace
        HelloDto inputDTO = new HelloDto();
        inputDTO.setServiceCode("helloServiceImpl");
        inputDTO.setHelloId("htttlo");
        inputDTO.setClientIp("127.0.0.1");
        inputDTO.setRequestTime("11");
        inputDTO.setSrcSendNo("1111");
        GatewayOutDTO gatewayOutDTO = evyGatewayRequestUtils.requestJson(inputDTO, GatewayOutDTO.class);
        CommandLog.info("{}", gatewayOutDTO);

        //试验db调用trace
        List<Object> list =  DBUtils.selectList("com.evy.linlin.testdemo.TestMapper.selectCount");
        CommandLog.info("{}", list);

        return outDto;
    }

    @PostMapping("/hello2")
    public HelloOutDto hello2(@RequestBody HelloDto helloDto) {
        OutDTO outDTO = start(helloDto);
        CommandLog.info(helloDto.getHelloId());
        HelloOutDto helloOutDto = new HelloOutDto();
        return helloOutDto;
    }
}
