package com.blitz.springboot4;

import com.blitz.springboot4.spider.Match;
import com.blitz.springboot4.spider.ProductDetailService;
import com.blitz.springboot4.spider.ProductSearchService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.blitz.springboot4.spider", "com.blitz.springboot4.mapper"})
public class SpiderMain {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpiderMain.class);
        app.setWebApplicationType(WebApplicationType.NONE);  // 关闭内置web服务器，变成命令行应用
        ApplicationContext ctx = app.run(args);

        //爬取单个商品
        ProductDetailService service = ctx.getBean(ProductDetailService.class);
        //爬取商品列表
        ProductSearchService listService = ctx.getBean(ProductSearchService.class);
        //统计结果
        Match match = ctx.getBean(Match.class);
        try {
            //爬取单个商品
            //service.getDataFromDatabase();
            //爬取商品列表
            //listService.categoryList();
            //配对计算
            match.matchResult();
        } catch (Exception e) {
            System.out.println("出现异常：-----------------------------------------------");
            e.printStackTrace();
        }
    }

}
