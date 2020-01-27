package com.evy.common;

import com.evy.common.infrastructure.common.constant.BusinessConstant;

import java.lang.reflect.Proxy;

/**
 * jdk代理
 */
public class JdkProxy2 {
    public static Object proxy(Object targetObj){
        return Proxy.newProxyInstance(targetObj.getClass().getClassLoader(), targetObj.getClass().getInterfaces(),
            (proxy, method, args) -> {
                //proxy：代理对象    method：代理对象的接口方法    args：接口方法参数
                System.out.println("开始代理start");
                System.out.println(proxy.getClass().getNestHost());
                System.out.println(method.getName());
                System.out.println(args);
                Object o = method.invoke(targetObj, args);
                System.out.println("结束代理end");
                return o;
            });
    }
}
