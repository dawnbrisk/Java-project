package com.blitz.springboot4.controller;

import com.blitz.springboot4.dao.ItemsRepository;
import com.blitz.springboot4.dao.PickingItemRepository;
import com.blitz.springboot4.entity.Item;
import com.blitz.springboot4.entity.PickingItem;
import com.blitz.springboot4.service.LocationService;
import com.blitz.springboot4.service.MergePalletService;
import com.blitz.springboot4.service.OldestSkuService;
import com.blitz.springboot4.service.SinglePalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UpLoadExcelController {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private PickingItemRepository pickingItemRepository;

    @Autowired
    private MergePalletService mergePalletService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SinglePalletService singlePalletService;

    @Autowired
    private OldestSkuService    oldestSkuService;

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<Map<String, Object>> uploadWarehouseItems(@RequestBody List<Item> items) {

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

            //计算合并库位（把下面的往过道上移动）
            singlePalletService.insertAllEmptyLocationOnWay();

            //计算合并库位（把过道上的往同类移动）
            //这个功能可用，但先不用，因为在展示时没有与腾库位列表分开，易混淆。
            //singlePalletService.InsertPalletInWay();

            //work mixing locations out




            map.put("status", "Upload successfully!");
        } catch (Exception e) {
            map.put("status", "Upload failed!");
            e.printStackTrace();
        }


        return ResponseEntity.ok(map);
    }

    @PostMapping("/uploadPickingList")
    @Transactional
    public ResponseEntity<Map<String, Object>> handleUpload(@RequestBody List<PickingItem> excelData) {

        try {
            pickingItemRepository.saveAll(excelData);

            return ResponseEntity.ok(Map.of(
                    "message", "Data received successfully",
                    "count", excelData.size()
            ));

        }
        catch (DataIntegrityViolationException  e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "duplicate upload"));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/doubleWeekCheck")
    @ResponseBody
    public List<Map<String, Object>> doubleWeekCheck(@RequestBody List<String> skus) {

        return oldestSkuService.getDoubleCheckList(skus);
    }
}





