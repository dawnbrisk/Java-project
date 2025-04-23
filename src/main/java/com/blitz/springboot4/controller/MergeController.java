package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.MergePalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
public class MergeController {

    @Autowired
    public MergePalletService mergePalletService;


    @GetMapping("/MergePalletDetail/{sku}")
    @ResponseBody
    public  Map<String, List<Map<String, Object>>> MergePalletDetail(@PathVariable String sku) {
        return mergePalletService.MergePalletList(sku);
    }


    @PostMapping("/areaList")
    @ResponseBody
    public List<String> areaList(@RequestBody Map<String, Object> area) {

        return mergePalletService.getAreaList(area.get("selectedTab").toString());
    }


    @PostMapping("/updatePalletFinish")
    @ResponseBody
    public Map<String,Object> updatePalletFinish(@RequestBody Map<String, Object> updateData) {
        Map<String,Object> map = new HashMap<>();
        mergePalletService.updatePallet(updateData);
        map.put("result","success");
        return map;
    }


    @GetMapping("/getNextPallet")
    @ResponseBody
    public  Map<String,Object> getNextPallet() {
        Map<String,Object> map = new HashMap<>();
        String str =  mergePalletService.getNextPallet();
        map.put("sku",str);
        return map;

    }


    @PostMapping("/mergePalletHistory")
    @ResponseBody
    public  Map<String,Object> mergePalletHistory(@RequestBody Map<String, Object> params) {

        Map<String,Object> map = new HashMap<>();


        return map;
    }


    @PostMapping("/getMergeSteps")
    @ResponseBody
    public  List<Map<String,Object>> getMergeSteps(@RequestBody Map<String, Object> params) {


        return mergePalletService.mergePalletHistory(params);
    }


}
