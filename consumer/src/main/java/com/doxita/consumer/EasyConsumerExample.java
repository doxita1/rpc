package com.doxita.consumer;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = null;
        User user = new User();
        user.setName("doxita");
        User user1 = userService.getUser(user);
        if (user1 == null) {
            System.out.println("user1 == null");
        }else{
            System.out.println(user1);
        }
    }
}
