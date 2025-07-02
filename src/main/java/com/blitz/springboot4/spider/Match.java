package com.blitz.springboot4.spider;

import com.blitz.springboot4.mapper.SpiderMapper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Match {

    @Autowired
    private SpiderMapper spiderMapper;

    public void matchResult() {

        spiderMapper.deleteCompareResult();

        List<Map<String, Object>> retourList = spiderMapper.getRetour();

        List<Map<String, Object>> subList = spiderMapper.getSubSku();

        int result = countMatchedMainSkus(retourList, subList);

        System.out.println("result1 " + result);
    }


    public int countMatchedMainSkus(List<Map<String, Object>> retourList, List<Map<String, Object>> dataList) {
        // Step 1: 将 retourList 转为 Map<sub_sku, qty>，方便快速查库存
        Map<String, Integer> stockMap = retourList.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("SKU"),
                        row -> ((Number) row.get("qty")).intValue(),
                        Integer::sum // 如果重复，数量相加
                ));


        // Step 2: 将 dataList 按 main_sku 分组，每个 main_sku 需要的 sub_sku -> qty
        Map<String, List<Map<String, Object>>> mainSkuGroups = dataList.stream()
                .collect(Collectors.groupingBy(row -> (String) row.get("main_sku")));

        int matchCount = 0;

        // Step 3: 遍历每个 main_sku，计算最多能组装多少个
        for (Map.Entry<String, List<Map<String, Object>>> entry : mainSkuGroups.entrySet()) {
            String mainSku = entry.getKey();
            List<Map<String, Object>> components = entry.getValue();

            // 每个 main_sku 最多能组装多少个？
            int possibleCount = Integer.MAX_VALUE;

            for (Map<String, Object> comp : components) {
                String subSku = (String) comp.get("sub_sku");
                int requiredQty = ((Number) comp.get("qty")).intValue();
                int availableQty = stockMap.getOrDefault(subSku, 0);

                int canMake = availableQty / requiredQty;


                possibleCount = Math.min(possibleCount, canMake);
            }

            if (possibleCount > 0 && possibleCount < Integer.MAX_VALUE) {
                matchCount += possibleCount;

                // 可选：从 stockMap 中减去使用的库存
                for (Map<String, Object> comp : components) {
                    String subSku = (String) comp.get("sub_sku");
                    int requiredQty = ((Number) comp.get("qty")).intValue();
                    stockMap.put(subSku, stockMap.get(subSku) - requiredQty * possibleCount);

                    spiderMapper.updateIsUsed(subSku,requiredQty * possibleCount);

                    spiderMapper.insertCompareResult(mainSku, possibleCount, subSku, requiredQty);
                }



                System.out.println("Matched " + possibleCount + " x main_sku = " + mainSku  );
            }
        }

        int originQty = retourList.stream()
                .map(row -> (Number) row.get("qty"))
                .mapToInt(Number::intValue)
                .sum();


        int totalQty = stockMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("the origin qty is : "+originQty+"  the remain sku qty is "+totalQty);

        return matchCount;
    }


}
