package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.BiweeklyMapper;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
