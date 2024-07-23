package com.doxita.rpcspringbootprovider;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;
import com.doxita.rpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("UserServiceImpl.getUser");
        System.out.println(user);
        return user;
    }
    
    @Override
    public short getNumber() {
        return UserService.super.getNumber();
    }
}
