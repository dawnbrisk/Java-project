package com.blitz.springboot4.controller;

import com.blitz.springboot4.dao.ItemsRepository;
import com.blitz.springboot4.dao.PickingItemRepository;
import com.blitz.springboot4.entity.Item;
import com.blitz.springboot4.entity.PickingItem;
import com.blitz.springboot4.service.*;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
public class UpLoadExcelController {

    @Autowired
    private ItemsRepository itemsRepository;


    @Autowired
    private MergePalletService mergePalletService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SinglePalletService singlePalletService;

    @Autowired
    private OldestSkuService oldestSkuService;


    @Autowired
    private UploadService uploadService;


    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadWarehouseItems(@RequestBody List<Item> items) {

        Map<String, Object> map = new HashMap<>();
        try {

            itemsRepository.deleteAllInBatch();
            itemsRepository.flush(); // 立即执行 SQL，释放表锁
            itemsRepository.saveAll(items);

            itemsRepository.flush(); // 立即执行 SQL，释放表锁

            //计算合并托盘
            mergePalletService.mergePallet();
            //计算合并库位
            locationService.clear();//清空表
            locationService.mergeLocation();

            //计算合并库位（从普通库位往过道库位移动）
            singlePalletService.insertAllEmptyLocationOnWay();

            //计算合并库位（把过道上的往同类移动）
            //这个功能可用，但先不用，因为在展示时没有与腾库位列表分开，易混淆。
            singlePalletService.InsertPalletInWay();

            //work mixing locations out
            map.put("status", "Upload successfully!");
        } catch (Exception e) {
            map.put("status", "Upload failed!");
            e.printStackTrace();
        }


        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @PostMapping("/uploadPickingList")
    public ResponseEntity<?> handleUpload(@RequestBody List<PickingItem> pickingItems) {

        try {
            uploadService.handleUpload(pickingItems);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "message", "Data received successfully",
                "count", pickingItems.size()
        )));


    }







    @PostMapping("/month_picking")
    public ResponseEntity<?> monthPicking(@RequestBody String month) {
        month = month.replace("\"", "").replace("-", "");
        List<String> result = oldestSkuService.getPickingOrderNumber(month);  // 设置断点在这里
        return ResponseEntity.ok(ApiResponse.success(result));
    }


}





