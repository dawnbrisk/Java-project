package com.blitz.springboot4.service;

import com.blitz.springboot4.entity.UpDownMove;
import com.blitz.springboot4.mapper.UpDownMoveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpDownMoveService {

    @Autowired
    private UpDownMoveMapper upDownMoveMapper;

    public void insertUpDownMove(UpDownMove upDownMove) {
        upDownMoveMapper.insertInventory(upDownMove);
    }

    public List<UpDownMove> selectUpDownMove(String username, String status) {
        return upDownMoveMapper.selectUpDownMove(username, status);
    }

    public int deleteUpDownMove(String id) {
        return upDownMoveMapper.deleteUpDownMove(id);
    }

    public Map<String, Object> actionList(Map<String, Object> params) {


        int pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
        int pageNo = Integer.parseInt(String.valueOf(params.get("page")));
        params.put("offset", (pageNo - 1) * pageSize); // 计算偏移量
        params.put("pageSize", pageSize);

        if (!params.get("name").toString().isEmpty()) {
            params.put("name", " AND username = '" + params.get("name").toString() + "'");
        } else {
            params.put("name", "");
        }
        if (params.get("dateRange")!=null &&!params.get("dateRange").toString().isEmpty()  ) {
            List<String> dateRange = (List<String>) params.getOrDefault("dateRange", Collections.emptyList());

            if (!dateRange.isEmpty()) {
                params.put("dateRange", "AND insert_time BETWEEN '" + dateRange.get(0) + "'" + " AND '" + dateRange.get(1) + "'");
            } else {
                params.put("dateRange", "");
            }

        } else {
            params.put("dateRange", "");
        }

        Map<String, Object> response = new HashMap<>();

        List<UpDownMove> result = upDownMoveMapper.selectActionList(params);
        List<UpDownMove> total =  upDownMoveMapper.selectActionListTotal(params);
        response.put("result", result);
        response.put("total", total.size());



        return response;
    }



    public List<Map<String,Object>> averageMovePerHour() {

        return upDownMoveMapper.averageMovePerHour();
    }
}
