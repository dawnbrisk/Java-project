package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.VisualizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisualizationService {

    @Autowired
    private VisualizationMapper visualizationMapper;

    public List<Map<String,Object>> getVisualizationList(){
        List<Map<String,Object>> list = visualizationMapper.getAllLocations();
        // 处理数据
        List<Map<String, Object>> fullList = list.stream()
                .peek(map -> {
                    if (!map.containsKey("num")) {
                        map.put("num", "0");
                    }
                })
                .toList();


        List<Map<String, Object>> result = fullList.stream()
                .map(map -> {
                    String location = (String) map.get("location");
                    if (location != null && location.matches("B-\\d+-\\d+-\\d+")) {
                        String[] parts = location.split("-");
                        if (parts.length == 4) {
                            map.put("Xlocation", parts[1]); // xx
                            map.put("Ylocation", parts[2]); // yy
                        }
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return result;
    }
}
