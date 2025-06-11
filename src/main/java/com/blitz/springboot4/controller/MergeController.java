package com.blitz.springboot4.controller;


import com.blitz.springboot4.entity.GeneralMergeRequest;
import com.blitz.springboot4.service.MergePalletService;
import com.blitz.springboot4.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@RestController
public class MergeController {

    @Autowired
    public MergePalletService mergePalletService;


    private static final String UPLOAD_DIR = "upload"; // 存储路径，相对于项目根目录


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
    public ResponseEntity<?> insertGeneralMerge(@RequestBody GeneralMergeRequest request,
                                                @RequestHeader("Authorization") String token) {

        // 打印接收到的数据

        List<String> fileUrls = request.getFileUrls();

        for (String url : fileUrls) {
            String mergeId = request.getPalletA_code() + "+" + request.getPalletB_code();
            mergePalletService.insertPalletPhoto(mergeId, url);
        }

        mergePalletService.insertGeneralMergePallet(request.getPalletA_code(), request.getPalletB_code(), token);


        return ResponseEntity.ok(ApiResponse.success("success"));
    }


    @PostMapping("/generalMergeHistory")
    public ResponseEntity<?> generalMergeHistory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.generalMergeHistory(token)));
    }

    @PostMapping("/checkIfExist")
    public ResponseEntity<?> checkIfExist(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(ApiResponse.success(mergePalletService.checkIfExist(params.get("fromPallet").toString(),params.get("toPallet").toString())));
    }


    @RestController
    public class FileUploadController {

        // 上传目录，支持通过配置文件或者环境变量注入
        @Value("${upload.dir}")
        private String uploadDir;

        @PostMapping("/uploadFiles")
        public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
            List<String> fileUrls = new ArrayList<>();
            String today = LocalDate.now().toString();

            // 构建当天上传目录路径
            File uploadPath = new File(uploadDir + "/" + today);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                try {
                    String originalFilename = file.getOriginalFilename();
                    String newFileName = UUID.randomUUID() + "_" + originalFilename;
                    Path filePath = Paths.get(uploadPath.getAbsolutePath(), newFileName);

                    file.transferTo(filePath);

                    // 构造访问URL（前端访问时需要配置静态资源映射）
                    String fileUrl = "/uploads/" + today + "/" + newFileName;
                    fileUrls.add(fileUrl);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(List.of("上传失败：" + file.getOriginalFilename()));
                }
            }
            return ResponseEntity.ok(ApiResponse.success(fileUrls));
        }
    }

}
