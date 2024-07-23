package com.doxita.common.service;

import com.doxita.common.model.User;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名:"+ user.getName());
        return user;
    }
}
