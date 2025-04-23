package com.blitz.springboot4.controller;


import com.blitz.springboot4.entity.User;
import com.blitz.springboot4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();


        if(userService.getUserName(user.getUsername().trim(),user.getPassword()) == 1){
            response.put("message", "Success");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Failed");
        return ResponseEntity.ok(response);
    }



    @GetMapping("/getDate")
    @ResponseBody
    public Map<String, Object> getDate() {
        Map<String, Object> response = new HashMap<>();
        String date=  userService.getDate();
        response.put("date", date);
        return response;
    }
}
