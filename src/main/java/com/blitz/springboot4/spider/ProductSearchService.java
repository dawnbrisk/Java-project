package com.blitz.springboot4.spider;

import com.blitz.springboot4.mapper.SpiderMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
public class ProductSearchService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 定义在类中
    Random random = new Random();

    @Autowired
    private SpiderMapper spiderMapper;

    public ProductSearchService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    //获取每个子目录下的列表
    public void categoryList() {
        List<String> categoryList = spiderMapper.getCategoryIds();
        //List<String> categoryList =  List.of("10050");

        categoryList.forEach(categoryId -> {
            try {
                searchProducts(categoryId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }


    private HttpHeaders buildHeaders() {
        // 构造 Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json, text/plain, */*");
        headers.set("origin", "https://www.gigab2b.com");
        headers.set("referer", "https://www.gigab2b.com/index.php?route=product/category&product_category_id=10015");
        headers.set("x-csrf-token", "naYR9Auz9fDfNMvDrtmWvhSQ1S06uxPBekQ8NyBs");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0");
        headers.set("cookie", "_ga=GA1.1.831586317.1747408221; _gcl_au=1.1.1088004216.1747408221; hubspotutk=d62e7b2b17e76df36ee4dc36c0d3f49c; __hs_opt_out=no; _fbp=fb.1.1747408226446.717849119989622286; gmd_device_id=a8f4ba04-2ec8-414e-85f0-9ee162902c4a; country=USA; _ayo=8a4ec2c94bd8c10db7bf2420560360ff; OCSESSID=8df2ac81bbf6a742d78b295e66; __hstc=114550104.d62e7b2b17e76df36ee4dc36c0d3f49c.1747408222805.1751291892368.1751356283770.53; __hssrc=1; login_flag=1; is_partner=0; currency=EUR; tfstk=g8YqplD14q34QWWAmU_aUeUCDR_A5NkIgF61sCAGGtX0lrwM4pv2hjnOjG-yILIfnnO1_PRAFO915F-87Bd9ljQsMZdAWNDId2TwMIQO6xN5OEPlq6RuSPbiVamjiak-d2gBiBCI5IkB5xBs6tfGSsjGm8cP167GirvGEgfhO-bMIdjuEs13oRjcizAle1bGIdbiaQXR_NXiNFRDsz5CmfhuYc7ILTsVKIXz8LLPiErvgTygIUJC094NUP4MzsRNWTo_8c6HvtsCKL07uNRl_LSB0vz2QCApjg8anj9HEhJGw3Hab9-MGHL525qDaFWVxEjzTzAlP1vGb3HURs7X4M82Dfekip62xZd_TxOPY3SdZgVamZtBOFIHoqydFMCwUMtqLrJh4qwOZ3OvWnygbifRa9GrafO4k3FRcE47XlIlJ_WIGsZgjJUNa9GfGlEOq6CPdj1f.; __hssc=114550104.24.1751356283770; acw_tc=ac11000117513599708897974e00e4801e44a86f6e13b7a5cd225a01f95abf; acw_sc__v2=6863a1ebcc0b2e161a4d74cbb59257d9ea33a00d; _ga_39N4BF4XSG=GS2.1.s1751356282$o52$g1$t1751360002$j38$l0$h0");

        return headers;
    }

    public void searchProducts(String categoryId) throws JsonProcessingException {
        int totalPages = 1;
        int limit = 100;


        String url = "https://www.gigab2b.com/index.php?route=/product/list/search";

        HttpHeaders headers = buildHeaders();

        String jsonBody = String.format("""
                {
                    "page": 1,
                    "limit": %d,
                    "dimension_type": 1,
                    "scene": 2,
                    "sort": "first_available",
                    "order": "desc",
                    "product_category_id": [%s]
                }
                """, limit, categoryId);


        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 计算总页数
            JsonNode paginationNode = rootNode.path("data").path("pagination");
            int total = paginationNode.path("total").asInt();
            totalPages = (int) Math.ceil(total / (double) limit);

            // 处理第1页数据
            JsonNode productList = rootNode.path("data").path("product_list");
            if (productList.isArray()) {
                for (JsonNode productNode : productList) {
                    spiderMapper.saveToDatabase(categoryId, productNode.asText(), totalPages);
                }
            }
        }


        // 从第2页开始抓取
        for (int page = 2; page <= totalPages; page++) {
            System.out.println("正在爬取分类 " + categoryId + " 的第 " + page + " 页");

            try {
                String jsonBody1 = String.format("""
                        {
                            "page": %d,
                            "limit": %d,
                            "dimension_type": 1,
                            "scene": 2,
                            "sort": "first_available",
                            "order": "desc",
                            "product_category_id": [%s]
                        }
                        """, page, limit, categoryId);

                HttpEntity<String> requestEntity1 = new HttpEntity<>(jsonBody1, headers);
                ResponseEntity<String> response1 = restTemplate.postForEntity(url, requestEntity1, String.class);

                if (response1.getStatusCode().is2xxSuccessful()) {
                    String responseBody = response1.getBody();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode productList = rootNode.path("data").path("product_list");

                    if (productList.isArray()) {
                        for (JsonNode productNode : productList) {
                            spiderMapper.saveToDatabase(categoryId, productNode.asText(), totalPages);
                        }
                    }
                } else {
                    System.out.println("请求失败，分类 " + categoryId + " 第 " + page + " 页");
                }

                Thread.sleep(random.nextInt(1000));

            } catch (Exception e) {

                System.out.println("分类 " + categoryId + " 第 " + page + " 页，尝试失败：" + e.getMessage());

            }
        }


    }
}

