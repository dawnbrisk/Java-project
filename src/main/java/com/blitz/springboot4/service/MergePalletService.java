package com.blitz.springboot4.service;


import com.blitz.springboot4.mapper.MergeMapper;
import com.blitz.springboot4.util.CommonUtils;
import com.blitz.springboot4.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MergePalletService {

    @Autowired
    private MergeMapper mergeMapper;



    public void mergePallet() {

        //  delete
        mergeMapper.deleteSkuPallet();
        mergeMapper.deleteMergeSteps();



        List<Map<String, Object>> toDoList = mergeMapper.getLessPallet(Constants.MaxLimit);

        // 通过Stream去重
        List<Map<String, Object>> list = new ArrayList<>(
                toDoList.stream()
                        .collect(Collectors.toMap(
                                map -> map.get("Item_Code"), // 以 Item_Code 作为键
                                map -> map, // 直接存 Map
                                (existing, replacement) -> existing // 如果有重复的，保留第一个
                        ))
                        .values()
        );

        for (Map<String, Object> map : list) {
            String sku = (String) map.get("Item_Code");

            String palletDetails = map.get("pallet_details").toString();

            List<Map<String, String>> result = splitStr(palletDetails);

            if (result.size() == 2) {
                //如果只有2托则直接合并
                mergeTwoPallet(result, sku);
            } else {
                //如果大于2托
                Iterator<Map<String, String>> iterator = result.iterator();
                boolean shouldRemove = false;
                while (iterator.hasNext()) {
                    Map<String, String> startMap = iterator.next();
                    int currentQty = Integer.parseInt(startMap.get("current_qty"));

                    if (currentQty >= Constants.MaxLimit) {
                        if (shouldRemove) {
                            iterator.remove(); // 删除多余的 >=4 的项
                        } else {
                            shouldRemove = true; // 标记第一个 >=4 的项已找到
                        }
                    }
                }

                if (result.size() == 2) {
                    mergeTwoPallet(result, sku);
                } else {
                    Map<String, String> endMap = result.get(result.size() - 1);

                    int allQty = 0;

                    for (int i = 0; i < result.size() - 1; i++) {

                        Map<String, String> indexMap = result.get(i);
                        allQty += Integer.parseInt(indexMap.get("current_qty"));
                        mergeMapper.insertSteps(CommonUtils.generateUUID(), sku, Integer.parseInt(indexMap.get("current_qty")), indexMap.get("location"), endMap.get("location"), indexMap.get("pallet_code"), endMap.get("pallet_code"), "0");
                        // front pallet (current)
                        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, indexMap.get("location"), indexMap.get("pallet_code"), Integer.parseInt(indexMap.get("current_qty")), "0", "0", "0");
                        // front pallet (expected)
                        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, indexMap.get("location"), indexMap.get("pallet_code"), 0, "1", "0", "1");

                    }
                    // end pallet (current)
                    mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, result.get(result.size() - 1).get("location"), result.get(result.size() - 1).get("pallet_code"), Integer.parseInt(result.get(result.size() - 1).get("current_qty")), "0", "0", "0");
                    // end pallet (expected)
                    mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, endMap.get("location"), endMap.get("pallet_code"), allQty + Integer.parseInt(endMap.get("current_qty")), "0", "0", "1");

                }
            }
        }
    }

    public void mergeTwoPallet(List<Map<String, String>> result, String sku) {
        Map<String, String> startMap = result.get(0);
        Map<String, String> endMap = result.get(1);


        mergeMapper.insertSteps(CommonUtils.generateUUID(), sku, Integer.parseInt(startMap.get("current_qty")), startMap.get("location"), endMap.get("location"), startMap.get("pallet_code"), endMap.get("pallet_code"), "0");

        //两个托盘&巧固架，第一个
        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, startMap.get("location"), startMap.get("pallet_code"), Integer.parseInt(startMap.get("current_qty")), "0", "0", "0");
        //两个托盘&巧固架，第二个
        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, endMap.get("location"), endMap.get("pallet_code"), Integer.parseInt(endMap.get("current_qty")), "0", "0", "0");

        //合并后的两个托盘&巧固架，第一个
        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, startMap.get("location"), startMap.get("pallet_code"), 0, "1", "0", "1");
        //合并后的两个托盘&巧固架，第二个
        mergeMapper.insertSkuPallet(CommonUtils.generateUUID(), sku, endMap.get("location"), endMap.get("pallet_code"), Integer.parseInt(endMap.get("current_qty")) + Integer.parseInt(startMap.get("current_qty")), "0", "0", "1");

    }


    public List<Map<String, String>> splitStr(String details) {

        List<Map<String, String>> list = new ArrayList<>();
        // 按逗号分割
        String[] parts = details.split(",");
        for (String part : parts) {
            // 去除空格并按加号分割
            String[] subParts = part.trim().split("\\+");
            if (subParts.length != 3) {
                return null;
            }

            // 将第一个元素作为键，其余部分作为值
            Map<String, String> newMap = new HashMap<>();

            newMap.put("pallet_code", subParts[0]);
            newMap.put("current_qty", subParts[1]);
            newMap.put("location", subParts[2]);
            list.add(newMap);


        }

        list.sort((mapA, mapB) -> {
            int a = Integer.parseInt(mapA.get("current_qty"));
            int b = Integer.parseInt(mapB.get("current_qty"));
            return a - b;
        });

        return list;
    }


    //得到移动端 合并托盘 to do列表 详情页
    public  Map<String, List<Map<String, Object>>>   MergePalletList(String sku) {

        List<Map<String, Object>> palletList = mergeMapper.selectSkuPallet(sku);

        List<Map<String, Object>> steps = mergeMapper.selectMergeSteps(sku);


        // 根据 SKU 查询并替换 type 为 currentPallet 或 expectedPallet
        Map<String, List<Map<String, Object>>> groupedPallets = palletList.stream()
                .collect(Collectors.groupingBy(pallet -> "0".equals(pallet.get("type")) ? "currentPallet" : "expectedPallet"));


        groupedPallets.put("steps", steps);



        return groupedPallets;
    }


    //得到移动端 合并托盘列表
    public List<String> getAreaList(String area) {
        if (area.equals("Z")) {
            List<Map<String, Object>> maps = mergeMapper.selectSkuPalletByLocation();
            return maps.stream()
                    .map(map -> (String) map.get("item_code"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (area.equals("A&C")) {
            return mergeMapper.skuList();
        } else {

         return mergeMapper.history();
        }

    }


    public void updatePallet(Map<String, Object> map){
        //update steps
        Map<String, Object> step = null;
        Object steps = map.get("steps");
        if (steps instanceof Map) {
            step = (Map<String, Object>) steps;
            step.forEach((k, v) -> {
                mergeMapper.updateMergeSteps(k,v.toString(),map.get("username").toString());
            });
        }

        //update pallet
        Map<String,Object> pallet = null;
        Object pallets = map.get("expectedPallet");
        if (pallets instanceof Map) {
            pallet = (Map<String, Object>) pallets;
            pallet.forEach((k, v) -> {
                mergeMapper.updateSkuPallet(k,v.toString(),map.get("username").toString());
            });
        }
    }

    public String getNextPallet(){
        return mergeMapper.getNext();
    }


    public List<Map<String,Object>> mergePalletHistory(Map<String, Object> params){

        if (!params.get("name").toString().isEmpty()) {
            params.put("name", " AND user = '" + params.get("name").toString() + "'");
        } else {
            params.put("name", "");
        }
        if (params.get("dateRange")!=null &&!params.get("dateRange").toString().isEmpty()  ) {
            List<String> dateRange = (List<String>) params.getOrDefault("dateRange", Collections.emptyList());

            if (!dateRange.isEmpty()) {
                params.put("dateRange", "AND update_time BETWEEN '" + dateRange.get(0).substring(0, 19).replace("T", " ") + "'" + " AND '" + dateRange.get(1).substring(0, 19).replace("T", " ") + "'");
            } else {
                params.put("dateRange", "");
            }

        } else {
            params.put("dateRange", "");
        }

        return mergeMapper.getAllSteps(params);
    }


    public List<Map<String,Object>> getMergeStepsByUser(){
        return mergeMapper.getMergeStepsByUser();
    }

}
