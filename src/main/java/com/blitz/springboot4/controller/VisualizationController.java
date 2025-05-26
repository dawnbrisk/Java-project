package com.blitz.springboot4.controller;

import com.blitz.springboot4.service.VisualizationService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisualizationController {

    @Autowired
    private VisualizationService visualizationService;

    @GetMapping("/allLocation")
    public ResponseEntity<?> getAllLocation(){
      return   ResponseEntity.ok(ApiResponse.success(visualizationService.getVisualizationList()));
    }

}
