package com.blitz.springboot4.controller;

import com.blitz.springboot4.service.BiweeklyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;


@RestController
public class BiweeklyCheckController {


    @Autowired
    private BiweeklyService service;

    @GetMapping("/double_weeks_check")
    public Map<String, Object> getCheckList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getPage(page, size);
    }
}
