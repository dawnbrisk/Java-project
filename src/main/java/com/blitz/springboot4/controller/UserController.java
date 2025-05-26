package com.blitz.springboot4.controller;

import com.blitz.springboot4.entity.UserDTO;
import com.blitz.springboot4.service.UserService;
import com.blitz.springboot4.util.ApiResponse;
import com.blitz.springboot4.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // 尝试认证（会调用 UserDetailsService）
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(ApiResponse.success(token));
    }


    @GetMapping("/getDate")
    public ResponseEntity<?> getDate() {
        Map<String, Object> response = new HashMap<>();
        String date = userService.getDate();
        response.put("date", date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * 获取所有用户
     *
     * @return 包含用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    /**
     * 创建新用户
     *
     * @param userCreateDTO 用户创建DTO
     * @return 包含创建用户的 ResponseEntity
     */

    @PostMapping("/addUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userCreateDTO) {
        UserDTO createdUser = userService.createUser(userCreateDTO);
        //todo 按照下面这行优化HttpStatus.CREATED
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }

    /**
     * 获取单个用户详情
     *
     * @param userId 用户ID
     * @return 包含用户详情的 ResponseEntity
     */

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @return 包含更新后用户的 ResponseEntity
     */
//
    @PostMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> userData) {
        userService.updateUser(userId, userData);
        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param userId 用户ID
     * @return 包含状态更新后用户的 ResponseEntity
     */

    @GetMapping("/status/{userId}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        userService.toggleUserStatus(userId);

        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 空响应体的 ResponseEntity
     */
//
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
