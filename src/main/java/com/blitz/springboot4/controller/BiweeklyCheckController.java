package com.blitz.springboot4.controller;

import com.blitz.springboot4.service.BiweeklyService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class BiweeklyCheckController {


    @Autowired
    private BiweeklyService service;

    @Autowired
    private BiweeklyService biweeklyService;

    @GetMapping("/double_weeks_check")
    public ResponseEntity<?> getCheckList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(service.getPage(page, size)));
    }

    @PostMapping("/doubleWeekCheck")
    public ResponseEntity<?> doubleWeekCheck(@RequestBody List<String> skus) {

        return ResponseEntity.ok(ApiResponse.success(biweeklyService.getDoubleCheckList(skus)));
    }


    @PostMapping("/biweeklyList")
    public ResponseEntity<?> biweeklyList() {
        return ResponseEntity.ok(ApiResponse.success(biweeklyService.getToCheckList()));
    }

    @PostMapping("/update-pallets")
    public ResponseEntity<?> updatePallets(@RequestBody List<Map<String, Object>> updateList) {
        for (Map<String, Object> map : updateList) {
            String itemCode = (String) map.get("item_code");
            String pallet = (String) map.get("pallet");
            String qty = map.get("input_value").toString();

            biweeklyService.updateQtyByItemAndPallet(itemCode, pallet, qty);
        }
        return ResponseEntity.ok(ApiResponse.success("Update successful"));
    }


}
