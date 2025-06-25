package com.blitz.springboot4;

import com.blitz.springboot4.spider.ProductDetailService;
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

        ProductDetailService service = ctx.getBean(ProductDetailService.class);
        try {
            service.getDataFromDatabase();
        } catch (Exception e) {
            System.out.println("出现异常：-----------------------------------------------");
            e.printStackTrace();
        }
    }

}
