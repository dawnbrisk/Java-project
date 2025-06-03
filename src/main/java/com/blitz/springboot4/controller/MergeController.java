package com.blitz.springboot4.controller;


import com.blitz.springboot4.service.MergePalletService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MergeController {

    @Autowired
    public MergePalletService mergePalletService;


    @GetMapping("/MergePalletDetail/{sku}")
    public ResponseEntity<?> MergePalletDetail(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.MergePalletList(sku)));
    }


    @PostMapping("/areaList")
    public ResponseEntity<?> areaList(@RequestBody Map<String, Object> area) {

        return ResponseEntity.ok(ApiResponse.success(mergePalletService.getAreaList(area.get("selectedTab").toString())));
    }


    @PostMapping("/updatePalletFinish")
    public ResponseEntity<?> updatePalletFinish(@RequestBody Map<String, Object> updateData) {
        Map<String, Object> map = new HashMap<>();
        mergePalletService.updatePallet(updateData);
        map.put("result", "success");
        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @GetMapping("/getNextPallet")
    public ResponseEntity<?> getNextPallet() {
        Map<String, Object> map = new HashMap<>();
        String str = mergePalletService.getNextPallet();
        map.put("sku", str);

        return ResponseEntity.ok(ApiResponse.success(map));
    }


    @PostMapping("/getMergeSteps")
    public ResponseEntity<?> getMergeSteps(@RequestBody Map<String, Object> params) {

        return ResponseEntity.ok(ApiResponse.success(mergePalletService.mergePalletHistory(params)));
    }


    @GetMapping("/getMergePalletHistory")
    public ResponseEntity<?> getMergePalletHistory() {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.getMergeStepsByUser()));
    }


    @PostMapping("/generalMergePallets")
    public ResponseEntity<?> insertGeneralMerge(@RequestParam String palletA_code,
                                                @RequestParam String palletB_code,
                                                @RequestParam("files") List<MultipartFile> files, @RequestHeader("Authorization") String token) throws IOException {

        // 2. 保存图片到文件夹和数据库
        String mergeId = palletA_code+"+"+palletB_code;
        String saveDirPath = "src/main/resources/static/images/merge-pallets/" + mergeId;
        File saveDir = new File(saveDirPath);
        if (!saveDir.exists())
            saveDir.mkdirs();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
                File dest = new File(saveDir, filename);
                file.transferTo(dest);

                String relativePath = "/images/merge-pallets/" + mergeId + "/" + filename;

                mergePalletService.insertPalletPhoto(mergeId,relativePath);
            }
        }

        return ResponseEntity.ok(ApiResponse.success(mergePalletService.insertGeneralMergePallet(palletA_code,palletB_code,token)));
    }


    @PostMapping("/generalMergeHistory")
    public ResponseEntity<?> generalMergeHistory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.generalMergeHistory(token)));
    }

}
