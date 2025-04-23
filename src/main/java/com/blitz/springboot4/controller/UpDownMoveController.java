package com.blitz.springboot4.controller;

import com.blitz.springboot4.entity.ApiResponse;
import com.blitz.springboot4.entity.UpDownMove;
import com.blitz.springboot4.service.UpDownMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
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
    public ResponseEntity<Map<String, Object>> selectHistory(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", upDownMoveService.selectUpDownMove(params.get("username"), params.get("status")));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deleteAction")
    public ResponseEntity<ApiResponse> deleteAction(@RequestBody Map<String, String> params) {


        int result = upDownMoveService.deleteUpDownMove(params.get("id"));

        return ResponseEntity.ok(new ApiResponse(true, "delete successful", result));
    }


    @PostMapping("/ActionList")
    public ResponseEntity<Map<String, Object>> ActionList(@RequestBody Map<String, Object> params) {

        return ResponseEntity.ok(upDownMoveService.actionList(params));

    }


    @GetMapping("/updownDetail")
    @ResponseBody
    public List<Map<String, Object>> updownDetail() {
       return upDownMoveService.updownDetail();
    }


    @GetMapping("/AverageMovePerHour")
    @ResponseBody
    public List<Map<String, Object>> AverageMovePerHour() {
        return upDownMoveService.averageMovePerHour();
    }


}
