package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.BiweeklyMapper;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BiweeklyService {

    @Autowired
    private BiweeklyMapper mapper;

    public Map<String, Object> getPage(int page, int size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> records = mapper.getPageData(offset, size);
        int total = mapper.getTotalCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }


    public List<Map<String,Object>> getDoubleCheckList(List<String> skus){
        String batchTime = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String itemCodes = skus.stream()
                .map(code -> "'" + code.trim() + "'")
                .collect(Collectors.joining(","));
        mapper.truncateDoubleWeeksCheck();
        mapper.insertIntoDoubleWeeksCheck(itemCodes,batchTime);
        return mapper.getDoubleCheckList(itemCodes);
    }

    public List<Map<String,Object>> getToCheckList(){
        return mapper.getToCheckList();
    }


    public void updateQtyByItemAndPallet(String itemCode, String pallet, String qty){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 获取当前用户名

        mapper.updateQtyByItemAndPallet(itemCode,pallet,qty,username);
    }
}
