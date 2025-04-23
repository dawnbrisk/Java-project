package com.blitz.springboot4.service;

import com.blitz.springboot4.mapper.PickingMapper;
import com.blitz.springboot4.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PickingService {

    @Autowired
    private PickingMapper pickingMapper;

    public List<Map<String,Object>> getAllPickingHistory(){
        return pickingMapper.findAllPickings();
    }


    public List<Map<String,Object>> getPickingHistoryByDay(String account,String month){
        return pickingMapper.findPickingsByDay(account,month);
    }


    public Map<String, Map<String, Double>> getPickingHistoryByAccount(){
        List<Map<String, Object>> pickingsByAccount = pickingMapper.findPickingsByAccount();

        Map<String, Map<String, List<Map<String, Object>>>> result =
                pickingsByAccount.stream()
                        .collect(Collectors.groupingBy(
                                record -> (String) record.get("operator_account"), // 一级分组：操作员
                                Collectors.groupingBy(
                                        record -> (String) record.get("picking_order_number"), // 二级分组：拣货单号
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                list -> list.stream()
                                                        .sorted(Comparator.comparingInt(r -> (Integer) r.get("item_no")))
                                                        .collect(Collectors.toList())
                                        )
                                )
                        ));


        Map<String, Map<String, Double>> averageIntervalsByDay = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Map<String, Object>>>> opEntry : result.entrySet()) {
            String operator = opEntry.getKey();// 操作员账号
            Map<String, List<Map<String, Object>>> orders = opEntry.getValue(); // 此操作员的所有拣货单

            // 日期 -> [该日期下所有平均间隔值]
            Map<String, List<Double>> dayToAvgList = new HashMap<>();


            for (Map.Entry<String, List<Map<String, Object>>> orderEntry : orders.entrySet()) {
                String orderNumber = orderEntry.getKey();// 拣货单号
                List<Map<String, Object>> items = orderEntry.getValue();

                // 提取拣货日期
                String dateStr = orderNumber.substring(0, 8);

                // 计算拣货单的总拣货数量
                int totalGoodsQuantity = items.stream()
                        .mapToInt(item -> (Integer) item.get("goods_quantity"))
                        .sum();

                // 如果拣货总量小于 20，则跳过该拣货单
                if (totalGoodsQuantity < Constants.MaxNumber) {
                    continue;
                }

                // 拆分连续 item_no 的段
                List<List<Map<String, Object>>> segments = new ArrayList<>();
                List<Map<String, Object>> currentSegment = new ArrayList<>();

                int lastItemNo = -2; // 初始为不连续

                for (Map<String, Object> item : items) {
                    int itemNo = (Integer) item.get("item_no");
                    if (itemNo == lastItemNo + 1) {
                        currentSegment.add(item);
                    } else {
                        // 分段
                        if (!currentSegment.isEmpty()) segments.add(new ArrayList<>(currentSegment));
                        currentSegment.clear();
                        currentSegment.add(item);
                    }
                    lastItemNo = itemNo;
                }

                // 添加最后一段
                if (!currentSegment.isEmpty()) segments.add(currentSegment);

                // 每段计算平均拣货间隔
                double totalAvg = 0;
                int segmentCount = 0;

                for (List<Map<String, Object>> segment : segments) {
                    if (segment.size() < 2) continue;

                    // 初始化变量来存放当前段的最小和最大 scan_time
                    LocalDateTime minTime = LocalDateTime.MAX; // 初始为最大值
                    LocalDateTime maxTime = LocalDateTime.MIN; // 初始为最小值


                    int totalGoods = 0;

                    // 遍历该段，找到最早和最晚的 scan_time
                    for (Map<String, Object> item : segment) {
                        LocalDateTime scanTime =(LocalDateTime)item.get("scan_time");

                        if (scanTime.isBefore(minTime)) minTime = scanTime; // 更新最小时间
                        if (scanTime.isAfter(maxTime)) maxTime = scanTime; // 更新最大时间

                        totalGoods += (Integer) item.get("goods_quantity"); // 累加 goods_quantity
                    }

                    // 计算该段时间间隔（最大时间 - 最小时间）
                    long totalSeconds = Duration.between(minTime, maxTime).getSeconds();

                    if (totalGoods > 0) {
                        double avgThisSegment = (double) totalSeconds / totalGoods;
                        System.out.println("operator: " + operator + " order: " + orderNumber + " totalSeconds: " + totalSeconds + " totalGoods: " + totalGoods);
                        totalAvg += avgThisSegment;
                        segmentCount++;
                    }
                }

                // 每个拣货单的平均值
                if (segmentCount > 0) {
                    double finalAvg = totalAvg / segmentCount;

                    // 添加到日期聚合中
                    dayToAvgList.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(finalAvg);
                }


            }

            // 👉 计算每个日期的总平均（多个单的平均值再取平均）
            Map<String, Double> dateAvgMap = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : dayToAvgList.entrySet()) {
                String date = entry.getKey();
                List<Double> avgList = entry.getValue();
                double avgOfDay = avgList.stream().mapToDouble(d -> d).average().orElse(0);
                dateAvgMap.put(date, avgOfDay);
            }

            averageIntervalsByDay.put(operator, dateAvgMap);
        }


        //转换结果
        Map<String, Map<String, Double>> resultByDate = new HashMap<>();

        for (Map.Entry<String, Map<String, Double>> operatorEntry : averageIntervalsByDay.entrySet()) {
            String operator = operatorEntry.getKey();
            Map<String, Double> dateMap = operatorEntry.getValue();

            for (Map.Entry<String, Double> dateEntry : dateMap.entrySet()) {
                String date = dateEntry.getKey();
                Double avg = dateEntry.getValue();

                // 按日期分组，把账号和平均值放进去
                resultByDate
                        .computeIfAbsent(date, k -> new HashMap<>())
                        .put(operator, avg);
            }
        }



        return resultByDate;
    }
}
