package com.doxita.consumer;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;
import com.doxita.rpc.bootstrap.ConsumerBootstrap;
import com.doxita.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
//        Class<?> aClass = LocalRegistry.get(UserService.class.getName());
//        UserService userService = (UserService) aClass.getDeclaredConstructor().newInstance();
        ConsumerBootstrap.init();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("doxita");
        User user1 = userService.getUser(user);
        System.out.println("user1.getName() = " + user1.getName());
        User user2 = userService.getUser(user);
        System.out.println("user2.getName() = " + user2.getName());
        User user3 = userService.getUser(user);
        System.out.println("user3.getName() = " + user3.getName());
        if (user1 == null) {
            System.out.println("user1 == null");
        }else{
            System.out.println(user1.getName());
            System.out.println(user2.getName());
            System.out.println(user3.getName());
        }
        
//        System.out.println(userService.getNumber());
    }
}
