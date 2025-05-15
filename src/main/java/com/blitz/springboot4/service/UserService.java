package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.UserDTO;
import com.blitz.springboot4.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public List<Map<String,Object>> getAllUsers() {

        return userMapper.getAllUsers();
    }

    public void deleteUser(Long userId) {

    }

    public UserDTO toggleUserStatus(Long userId) {
        userMapper.abandon(userId);
        return null;
    }

    public UserDTO createUser(Map<String,Object> user) {
        user.put("status", "0");
        userMapper.addUser(user);
        return null;
    }

    public UserDTO getUserById(Long userId) {
        return null;
    }

    public void updateUser(Long userId, Map<String, Object> user) {
        try {
            userMapper.updateUser(userId,user);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
