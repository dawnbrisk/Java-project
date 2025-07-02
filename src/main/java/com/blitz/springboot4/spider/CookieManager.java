package com.blitz.springboot4.spider;

public class CookieManager {
    private static String currentCookie = "";

    // 正常获取，如果为空才去登录
    public static synchronized String getValidCookie() throws Exception {
        if (currentCookie.isEmpty()) {
            refreshCookie(); // 主动刷新
        }
        return currentCookie;
    }

    // 外部显式调用：更新 Cookie（例如滑块验证后）
    public static synchronized void refreshCookie() throws Exception {
        System.out.println(" 正在重新获取 Cookie...");
        currentCookie = SeleniumLoginService.getCookie();
    }
}

