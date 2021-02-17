package com.evy.linlin.login.domain.repository;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.SequenceUtils;
import com.evy.linlin.login.domain.tunnel.LoginAssembler;
import com.evy.linlin.login.domain.tunnel.constant.LoginErrorConstant;
import com.evy.linlin.login.domain.tunnel.model.LoginInfoDO;
import com.evy.linlin.login.domain.tunnel.model.LoginInfoOutDO;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * 操作登录信息仓储层
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:13
 */
@Repository
public class LoginDataRepository {
    private ReactiveRedisTemplate<String, String> template;
    private static final String USER_PASS_KEY = "login:user:pass:";
    private static final String USER_SEQ_KEY = "login:user:seq:";

    @PostConstruct
    private void init() {
        ReactiveRedisConnectionFactory factory = AppContextUtils.getBean(ReactiveRedisConnectionFactory.class);
        template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
    }

    /**
     * 检验或保存登录信息
     * @param loginInfoDo com.evy.linlin.login.domain.tunnel.model.LoginInfoDO
     * @return com.evy.linlin.login.domain.tunnel.model.LoginInfoOutDO
     */
    public LoginInfoOutDO validLogin(LoginInfoDO loginInfoDo) throws BasicException {
        String userName = loginInfoDo.getUserName();
        String password = loginInfoDo.getPassword();
        //[0] 密码  [1] userSeq
        String[] userSeqAndEncPassword = new String[2];

        template.opsForValue()
                .get(USER_PASS_KEY + userName)
                .blockOptional()
                .ifPresent(pass -> userSeqAndEncPassword[0] = pass);

        if (StringUtils.isEmpty(userSeqAndEncPassword[0])) {
            //不存在用户信息
            template.opsForValue()
                    .append(USER_PASS_KEY + userName, password)
                    .subscribe();
            userSeqAndEncPassword[1] = createUserSeq();
            template.opsForValue()
                    .append(USER_SEQ_KEY + userName, userSeqAndEncPassword[1])
                    .subscribe();
        } else if (validPass(password, userSeqAndEncPassword[0])) {
            //存在用户信息并密码校验通过
            template.opsForValue()
                    .get(USER_SEQ_KEY + userName)
                    .blockOptional()
                    .ifPresent(seq -> userSeqAndEncPassword[1] = seq);
        } else {
            //存在用户信息并密码校验不通过
            throw new BasicException(LoginErrorConstant.LOGIN_ERR_1);
        }

        if (StringUtils.isEmpty(userSeqAndEncPassword[1])) {
            throw new BasicException(LoginErrorConstant.LOGIN_ERR_2);
        }

        return LoginAssembler.create(userSeqAndEncPassword[1]);
    }

    /**
     * 返回唯一用户标识
     * @return  userSeq
     */
    private String createUserSeq() {
        return SequenceUtils.getNextSeq();
    }

    /**
     * 对比密码与加密串
     * @param fromPassword  加密密码串,前端上送
     * @param encPassword   解密密码串,redis获取
     * @return true校验一致
     */
    private boolean validPass(String fromPassword, String encPassword) {
        return fromPassword.equals(encPassword);
    }
}
