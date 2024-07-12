package com.doxita.provider;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名:"+ user.getName());
        return user;
    }
}
