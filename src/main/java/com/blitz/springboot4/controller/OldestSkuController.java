package com.blitz.springboot4.controller;

import org.apache.ibatis.annotations.Param;
import com.blitz.springboot4.service.OldestSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
public class OldestSkuController {

    @Autowired
    private OldestSkuService oldestSkuService;

    @GetMapping("/getOldestSku")
    @ResponseBody
    public List<Map<String,Object>> getOldestSku(@Param("type") String type){
        return oldestSkuService.getOldestSku(type);
    }
}
