package com.blitz.springboot4.controller;

import com.blitz.springboot4.service.BiweeklyService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BiweeklyCheckController {


    @Autowired
    private BiweeklyService service;

    @GetMapping("/double_weeks_check")
    public ResponseEntity<?> getCheckList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getPage(page, size)));
    }
}
