package com.blitz.springboot4.spider;

import com.blitz.springboot4.mapper.SpiderMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        headers.set("x-csrf-token", "VT3ZUER0tbuB3w8IWdaemHTqK6QPVQku4CYNRuMo");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0");
        headers.set("cookie", "_ga=GA1.1.831586317.1747408221; _gcl_au=1.1.1088004216.1747408221; hubspotutk=d62e7b2b17e76df36ee4dc36c0d3f49c; __hs_opt_out=no; _fbp=fb.1.1747408226446.717849119989622286; gmd_device_id=a8f4ba04-2ec8-414e-85f0-9ee162902c4a; currency=USD; country=USA; tfstk=geoIpJv6R6fQNmZRFyvNfTJWT5qWQd-2RTw-nYIFekEdw_HY_XkEYe47WbNZYWkUv3gSij1zyuore9msGgSeteDRVurJgI-20vX3qud2GxU8c623EwKzw7ETx8zeXytM0vDhBgQpU2tVFl-0ZkF-wyUTX8NTpwh8JNp_e85dyMhJCdwzn6QdvJUOW8w427h82dMTs8E8pbE-NkXbFeNndpJNzqzfGJHL1gI-vU4QG77PVgg0dyi-p5MIdcwQRSa8YENEvfk-YD4MWNZoQqGSyjdN8W3bhfaqRIsIGX2-F8hBmtNtAYi_-VWVDAa7OVnL5TI-puFLr4GBOwVK8fa0BPBWmRl46vmK5LftpjPQvRUwDKMTyVo3oDROAo3mLku-MC7U9qwR4uj4GN-lPOacVRN2Cd_lrN1dGvHiqilQJR2Q7d9194fc2g7pCd1Kryegd5J6C63l.; acw_tc=ac11000117502321030721468e006db96077b787e1db75deae4cd1d3f60390; _ayo=670a7685d938a9eda27c77a965a3460b; OCSESSID=e3f093d0c39b91f882eb05de45; __hstc=114550104.d62e7b2b17e76df36ee4dc36c0d3f49c.1747408222805.1750172697649.1750232104035.23; __hssrc=1; acw_sc__v2=68526c2f05e7edc78f4587d549ca5b53f2950939; login_flag=1; is_partner=0; __hssc=114550104.3.1750232104035; _ga_39N4BF4XSG=GS2.1.s1750232103$o23$g1$t1750232135$j28$l0$h0");

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
                    "order": "asc",
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
                            "order": "asc",
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

