package com.doxita.common.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
