package com.blitz.springboot4.controller;


import com.blitz.springboot4.entity.User;
import com.blitz.springboot4.entity.UserCreateDTO;
import com.blitz.springboot4.entity.UserDTO;
import com.blitz.springboot4.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();


        if (userService.getUserName(user.getUsername(), user.getPassword()) == 1) {
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Failed");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getDate")
    public Map<String, Object> getDate() {
        Map<String, Object> response = new HashMap<>();
        String date = userService.getDate();
        response.put("date", date);
        return response;
    }


    /**
     * 获取所有用户
     *
     * @return 包含用户列表
     */
    @GetMapping("/users")
    public List<Map<String,Object>> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 创建新用户
     *
     * @param userCreateDTO 用户创建DTO
     * @return 包含创建用户的 ResponseEntity
     */

    @PostMapping("/addUser")
    public Map<String,Object> createUser( @RequestBody Map<String,Object> userCreateDTO) {
        UserDTO createdUser = userService.createUser(userCreateDTO);
        //todo 按照下面这行优化HttpStatus.CREATED
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        return Map.of();
    }

    /**
     * 获取单个用户详情
     *
     * @param userId 用户ID
     * @return 包含用户详情的 ResponseEntity
     */

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 更新用户信息
     *
     * @param userId        用户ID
     * @return 包含更新后用户的 ResponseEntity
     */
//
    @PostMapping("/users/{userId}")
    public Map updateUser(
            @PathVariable Long userId,
             @RequestBody Map<String,Object> userData) {
         userService.updateUser(userId, userData);
        return Map.of();
    }

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param userId 用户ID
     * @return 包含状态更新后用户的 ResponseEntity
     */

    @GetMapping("/status/{userId}")
    public Map<Object, Object> toggleUserStatus(@PathVariable Long userId) {
        try{
            UserDTO user = userService.toggleUserStatus(userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Map.of();
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 空响应体的 ResponseEntity
     */
//
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
