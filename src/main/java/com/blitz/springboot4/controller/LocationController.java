package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.LocationService;
import com.blitz.springboot4.service.LocationServicePlus;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationServicePlus locationServicePlus;

    @GetMapping("/longLocation")
    public List<Map<String, Object>> getLongLocationList() {
        return locationServicePlus.findAll();
    }

    @GetMapping("/mixingLocation")
    public ResponseEntity<?> getMixingLocation() {
        return ResponseEntity.ok(ApiResponse.success(locationService.findMixingLocation()));
    }

    @GetMapping("/locationList")
    public ResponseEntity<?> locationList() {
        List<String> locationList = locationService.getAllLocations();

        return ResponseEntity.ok(ApiResponse.success(locationList));
    }

    @GetMapping("/getNext")
    public ResponseEntity<?> getNext() {
        String nextSku = locationService.getAllLocations().get(0);

        Map<String,String> map = new HashMap<>();
        map.put("sku",nextSku);
        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @PostMapping("/history")
    public ResponseEntity<?> getHistory(@RequestBody Map<String,Object> params) {
        List<String> locationList = locationService.getHistory(params.get("username").toString());

        return ResponseEntity.ok(ApiResponse.success(locationList));
    }




    @GetMapping("/skuDetail/{sku}")  // 使用 @PathVariable 获取 sku
    public ResponseEntity<?> getLocationListBySku(@PathVariable String sku) {

        return ResponseEntity.ok(ApiResponse.success(locationService.getLocationListBySku(sku)));  // 根据 sku 查询数据
    }


    @PostMapping("/updateFinish")
    public ResponseEntity<?> updateFinish(@RequestBody Map<String, Map<String, String>> params) {
        locationService.updateFinish(params);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "Success!");

        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @PostMapping("/getAllSteps")
    public ResponseEntity<?> getAllSteps(@RequestBody Map<String,Object> params) {
       try {
           return ResponseEntity.ok(ApiResponse.success(locationService.getAllSteps(params)));
       }catch (Exception e){
           e.printStackTrace();
           return ResponseEntity.ok(ApiResponse.success(List.of()));
       }

    }


    @GetMapping("/emptyLocationList")
    public ResponseEntity<?> emptyLocationList() {
        return ResponseEntity.ok(ApiResponse.success(locationService.getEmptyLocation()));
    }


    @PostMapping("/skip")
    public ResponseEntity<?> skip(@RequestBody Map<String,String> params) {

        locationService.skip(params.get("reasonType"),params.get("sku"));
        return ResponseEntity.ok(ApiResponse.success(Map.of("result","Success!")));

    }

    @GetMapping("/movePalletHistory")
    public ResponseEntity<?> movePalletHistory() {
        return ResponseEntity.ok(ApiResponse.success(locationService.getMovingHistory()));
    }


}
