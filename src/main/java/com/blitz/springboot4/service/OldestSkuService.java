package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.OldestSKUMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OldestSkuService {

    @Autowired
    private OldestSKUMapper oldestSkuMapper;

    public List<Map<String,Object>> getOldestSku(String type){
        if("P".equals(type)){
            return oldestSkuMapper.getOldestSKU();
        }else {
            // A B C
            return oldestSkuMapper.getOldestSKU2();
        }

    }




    public List<String> getPickingOrderNumber(String month){
        return oldestSkuMapper.getPickingOrderNumber(month);
    }

}
