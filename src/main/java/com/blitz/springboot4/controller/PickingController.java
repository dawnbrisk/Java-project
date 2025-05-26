package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.PickingService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class PickingController {


    @Autowired
    private PickingService pickingService;

    @GetMapping("/picking_detail")
    public ResponseEntity<?> pickingDetail() {
        return ResponseEntity.ok(ApiResponse.success(pickingService.getAllPickingHistory()));
    }


    @GetMapping("/pickingByAccount")
    public ResponseEntity<?> pickingByAccount(@RequestParam("month") String month,
                                                     @RequestParam("account") String account) {
        return ResponseEntity.ok(ApiResponse.success(pickingService.getPickingHistoryByDay(account,month)));
    }

    @GetMapping("/averageInterval")
    public ResponseEntity<?> average(){
       return ResponseEntity.ok(ApiResponse.success(pickingService.getPickingHistoryByAccount()));

    }
}
