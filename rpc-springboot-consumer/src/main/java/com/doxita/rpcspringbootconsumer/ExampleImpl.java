package com.doxita.rpcspringbootconsumer;

import com.doxita.common.model.User;
import com.doxita.common.service.UserService;
import com.doxita.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleImpl {
    
    @RpcReference
    UserService userService;
    
    public void test(){
        User user = new User();
        user.setName("doxita hello world");
        userService.getUser(user);
        
    }
}
