package com.blitz.springboot4.controller;


import com.blitz.springboot4.entity.UpDownMove;
import com.blitz.springboot4.service.UpDownMoveService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UpDownMoveController {

    @Autowired
    private UpDownMoveService upDownMoveService;

    @PostMapping("/upDownMoveInsert")
    public ResponseEntity<Map<String, Object>> upDownMoveInsert(@RequestBody UpDownMove onceMove) {
        Map<String, Object> response = new HashMap<>();
        upDownMoveService.insertUpDownMove(onceMove);
        response.put("result", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/selectHistory")
    public ResponseEntity<?> selectHistory(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", upDownMoveService.selectUpDownMove(params.get("username"), params.get("status")));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/deleteAction")
    public ResponseEntity<ApiResponse> deleteAction(@RequestBody Map<String, String> params) {

        int result = upDownMoveService.deleteUpDownMove(params.get("id"));

        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @PostMapping("/ActionList")
    public ResponseEntity<?> ActionList(@RequestBody Map<String, Object> params) {

        return ResponseEntity.ok(ApiResponse.success(upDownMoveService.actionList(params)));

    }



    @GetMapping("/AverageMovePerHour")
    public ResponseEntity<?> AverageMovePerHour() {
        return ResponseEntity.ok(ApiResponse.success(upDownMoveService.averageMovePerHour()));
    }


}
