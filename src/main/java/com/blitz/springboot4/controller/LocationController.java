package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.LocationService;
import com.blitz.springboot4.service.LocationServicePlus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@CrossOrigin(origins = "*")
@Controller
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationServicePlus locationServicePlus;




    @GetMapping("/longLocation")
    @ResponseBody
    public List<Map<String, Object>> getLongLocationList() {
        return locationServicePlus.findAll();
    }

    @GetMapping("/mixingLocation")
    @ResponseBody
    public List<Map<String, Object>> getMixingLocation() {
        return locationService.findMixingLocation();
    }

    @GetMapping("/locationList")
    @ResponseBody
    public List<String> locationList() {
        List<Map<String, Object>> locationList = locationService.getAllLocations();

        return locationList.stream()
                .map(map -> map.get("sku"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
    }

    @GetMapping("/getNext")
    @ResponseBody
    public Map<String,String> getNext() {
        List<Map<String, Object>> locationList = locationService.getAllLocations();

        String nextSku =  locationList.stream()
                .map(map -> map.get("sku"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList().get(0);

        Map<String,String> map = new HashMap<>();
        map.put("sku",nextSku);
        return map;
    }


    @PostMapping("/history")
    @ResponseBody
    public List<String> getHistory(@RequestBody Map<String,Object> params) {
        List<Map<String, Object>> locationList = locationService.getHistory(params.get("username").toString());

        return locationList.stream()
                .map(map -> map.get("sku"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
    }




    @ResponseBody
    @GetMapping("/skuDetail/{sku}")  // 使用 @PathVariable 获取 sku
    public Map<String, Object> getLocationListBySku(@PathVariable String sku) {

        return locationService.getLocationListBySku(sku);  // 根据 sku 查询数据
    }


    @PostMapping("/updateFinish")
    @ResponseBody
    public Map<String, Object> updateFinish(@RequestBody Map<String, Map<String, String>> params) {
        locationService.updateFinish(params);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "Success!");

        return map;
    }


    @PostMapping("/getAllSteps")
    @ResponseBody
    public List<Map<String, Object>> getAllSteps(@RequestBody Map<String,Object> params) {
       try {
           return locationService.getAllSteps(params);
       }catch (Exception e){
           e.printStackTrace();
           return List.of();
       }

    }


    @GetMapping("/emptyLocationList")
    @ResponseBody
    public List<String> emptyLocationList() {
        return locationService.getEmptyLocation();
    }


    @PostMapping("/skip")
    @ResponseBody
    public Map<String,String> skip(@RequestBody Map<String,String> params) {

        locationService.skip(params.get("reasonType"),params.get("sku"));
        return Map.of("result","Success!");

    }
}
