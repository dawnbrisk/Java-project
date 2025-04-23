package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.PickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping
public class PickingController {


    @Autowired
    private PickingService pickingService;

    @GetMapping("/picking_detail")
    @ResponseBody
    public List<Map<String,Object>> pickingDetail() {
        return pickingService.getAllPickingHistory();
    }


    @GetMapping("/pickingByAccount")
    @ResponseBody
    public List<Map<String,Object>> pickingByAccount(@RequestParam("month") String month,
                                                     @RequestParam("account") String account) {
        return pickingService.getPickingHistoryByDay(account,month);
    }

    @GetMapping("/averageInterval")
    @ResponseBody
    public Map<String, Map<String, Double>> average(){
       return pickingService.getPickingHistoryByAccount();

    }
}
