package com.evy.linlin.testdemo;

import com.evy.common.infrastructure.common.command.BaseCommandTemplate;
import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.common.log.anno.TraceLog;
import com.evy.common.infrastructure.tunnel.OutDTO;
import com.evy.linlin.HelloDto;
import com.evy.linlin.HelloOutDto;
import com.evy.linlin.HelloService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 17:59
 */
@RestController
@TraceLog
public class HelloServiceImpl extends BaseCommandTemplate<HelloDto,HelloOutDto> implements HelloService<HelloDto,HelloOutDto>{

    @Override
    public HelloOutDto execute(HelloDto inputDTO) throws BasicException {
        throw new BasicException("HELLO", "Wrold");
//        HelloOutDto outDto = new HelloOutDto();
//        outDto.setErrorCode("0");
//        outDto.setErrorMsg("成功");
//        return outDto;
    }

    @Override
    public HelloOutDto hello(HelloDto helloDto) {
        OutDTO outDTO = start(helloDto);
        System.out.println(helloDto.getHelloId());
        HelloOutDto helloOutDto = new HelloOutDto();
        HelloOutDto helloOutDto1 = convertDto(helloOutDto, outDTO);
        return  helloOutDto1;
    }
}
