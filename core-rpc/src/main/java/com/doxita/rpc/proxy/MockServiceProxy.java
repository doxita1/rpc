package com.doxita.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler {
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("MockServiceProxy invoke: {}", method.getName());
        return getDefaultValue(returnType);
    }
    
    public Object getDefaultValue(Class<?> returnType) {
        // 基本类型
        if (returnType.isPrimitive()) {
            if (returnType == int.class) {
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            } else if (returnType == float.class) {
                return 0.0f;
            } else if (returnType == double.class) {
                return 0.0d;
            }else if (returnType == boolean.class) {
                return false;
            }else if(returnType == short.class){
                return (short)0;
            }else if (returnType == char.class){
                return '\u0000';
            }
        }
        // 引用类型
        return null;
    }
}
