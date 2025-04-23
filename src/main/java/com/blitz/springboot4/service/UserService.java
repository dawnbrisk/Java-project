package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public String getDate() {
        return userMapper.getDate();
    }


    public int getUserName(String name,String password){
        return userMapper.getUserName(name,password);
    }

}
