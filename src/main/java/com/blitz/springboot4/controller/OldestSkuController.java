package com.blitz.springboot4.controller;

import com.blitz.springboot4.util.ApiResponse;
import org.apache.ibatis.annotations.Param;
import com.blitz.springboot4.service.OldestSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OldestSkuController {

    @Autowired
    private OldestSkuService oldestSkuService;

    @GetMapping("/getOldestSku")
    public ResponseEntity<?> getOldestSku(@Param("type") String type){
        return ResponseEntity.ok(ApiResponse.success(oldestSkuService.getOldestSku(type)));
    }
}
