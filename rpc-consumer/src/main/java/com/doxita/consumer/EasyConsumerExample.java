package com.doxita.consumer;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;
import com.doxita.rpc.proxy.ServiceProxyFactory;

import java.lang.reflect.InvocationTargetException;

public class EasyConsumerExample {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        Class<?> aClass = LocalRegistry.get(UserService.class.getName());
//        UserService userService = (UserService) aClass.getDeclaredConstructor().newInstance();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("doxita");
        User user1 = userService.getUser(user);
        if (user1 == null) {
            System.out.println("user1 == null");
        }else{
            System.out.println(user1.getName());
        }
        
        System.out.println(userService.getNumber());
    }
}