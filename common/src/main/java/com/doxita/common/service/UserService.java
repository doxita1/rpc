package com.doxita.common.service;

import com.doxita.common.model.User;

public interface UserService {
    User getUser(User user);
    
    default short getNumber(){
        return 1;
    }
}
