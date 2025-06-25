package com.blitz.springboot4.spider;


import com.blitz.springboot4.mapper.SpiderMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
public class ProductDetailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private SpiderMapper spiderMapper;


    public void getDataFromDatabase() throws Exception {

        int i = 1;
        while (true){
            List<String> productList = spiderMapper.getItemCodes();
            if (productList.isEmpty()) break;
            String cookie = CookieManager.getValidCookie();

            for (String productCode : productList) {

                System.out.print("\r开始爬取第" + i++ + "个: " + productCode);
                String product = fetchProductInfo(productCode, cookie);

                Thread.sleep(new Random().nextInt(100)); // 防止触发反爬机制，适当休眠

                if (!extractData(product, productCode)) {
                    System.out.println("重新获取cookie");
                    CookieManager.refreshCookie();  // ✅ 强制更新 Cookie
                    cookie = CookieManager.getValidCookie();  // 重新拿到新 cookie
                    // 用 selenium 打开该 productCode 的详情页（或任意一个会触发滑块的网址）
                    SeleniumLoginService.openPageWithSlider(productCode); // 🔧 你需要写这个方法
                    SeleniumLoginService.handleSliderCaptcha();  // 👈 在页面上拖动滑块

                    // 滑块通过后获取新的 cookie
                    cookie = SeleniumLoginService.getCookie();
                }

            }
        }
    }

    public boolean extractData(String product, String productCode) throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(product);

        // 提取主 SKU
        String mainSku = rootNode.path("data").path("product_info").path("sku").asText();
        String productName = rootNode.path("data").path("product_info").path("product_name").asText();


        // 提取 combo SKU 列表
        JsonNode comboArray = rootNode
                .path("data")
                .path("product_info")
                .path("specification")
                .path("package_size")
                .path("combo");

        if (comboArray != null && comboArray.isArray()) {
            spiderMapper.updateToDatabase(mainSku, productCode, productName, "Y");

            for (JsonNode comboItem : comboArray) {
                String subSku = comboItem.path("sku").asText();
                String qty = comboItem.path("qty").asText();
                spiderMapper.insertSubSku(mainSku, subSku, qty);

            }
            if (comboArray.isEmpty()) {
                spiderMapper.updateToDatabase(mainSku, productCode, productName, "N");
            }

            return true;
        } else {

            return false;

        }

    }


    public String fetchProductInfo(String productId, String cookie) throws Exception {

        String url = "https://www.gigab2b.com/index.php?route=/product/info/info/baseInfos&product_id=" + productId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json, text/plain, */*");
        headers.set("cookie", cookie);
        headers.set("referer", "https://www.gigab2b.com/index.php?route=product/product&product_id=" + productId);
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0");
        headers.set("x-requested-with", "XMLHttpRequest");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );


        if (response.getStatusCode().is2xxSuccessful()) {

            return response.getBody();
        } else {
            throw new RuntimeException("请求失败: " + response.getStatusCode());
        }
    }

}
