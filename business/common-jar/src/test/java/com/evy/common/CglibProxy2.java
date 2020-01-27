package com.evy.common;

import com.evy.common.infrastructure.common.constant.BusinessConstant;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class CglibProxy2 {
    public static Object proxy(Object targetObj){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetObj.getClass());
        enhancer.setCallback((MethodInterceptor) (obj, method, args, methodProxy) -> {
            //obj：代理对象  method：代理对象方法   args：方法参数   methodProxy：
            System.out.println("cglib代理start");
            System.out.println(obj.getClass().getNestHost());
            System.out.println(method.getName());
            System.out.println(methodProxy);
            Object o = method.invoke(targetObj, args);
            System.out.println("cglib代理end");
            return o;
        });
        return enhancer.create();
    }
}
