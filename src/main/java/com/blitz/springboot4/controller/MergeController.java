package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.MergePalletService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MergeController {

    @Autowired
    public MergePalletService mergePalletService;


    @GetMapping("/MergePalletDetail/{sku}")
    public ResponseEntity<?> MergePalletDetail(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.MergePalletList(sku)));
    }


    @PostMapping("/areaList")
    public ResponseEntity<?> areaList(@RequestBody Map<String, Object> area) {

        return ResponseEntity.ok(ApiResponse.success(mergePalletService.getAreaList(area.get("selectedTab").toString())));
    }


    @PostMapping("/updatePalletFinish")
    public ResponseEntity<?> updatePalletFinish(@RequestBody Map<String, Object> updateData) {
        Map<String, Object> map = new HashMap<>();
        mergePalletService.updatePallet(updateData);
        map.put("result", "success");
        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @GetMapping("/getNextPallet")
    public ResponseEntity<?> getNextPallet() {
        Map<String, Object> map = new HashMap<>();
        String str = mergePalletService.getNextPallet();
        map.put("sku", str);

        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @PostMapping("/getMergeSteps")
    public ResponseEntity<?> getMergeSteps(@RequestBody Map<String, Object> params) {

        return ResponseEntity.ok(ApiResponse.success(mergePalletService.mergePalletHistory(params)));
    }


    @GetMapping("/getMergePalletHistory")
    public ResponseEntity<?> getMergePalletHistory() {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.getMergeStepsByUser()));
    }


}
