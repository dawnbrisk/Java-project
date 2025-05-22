package com.blitz.springboot4.controller;

import com.blitz.springboot4.service.VisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class VisualizationController {

    @Autowired
    private VisualizationService visualizationService;

    @GetMapping("/allLocation")
    @ResponseBody
    public List<Map<String,Object>> getAllLocation(){
      return   visualizationService.getVisualizationList();
    }

}
