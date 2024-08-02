package com.parkjh.stockcrawlingapi.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;

public interface CrawlingService<T> {

    default WebDriver fetchWebDriver(String url) {
        WebDriverManager.chromedriver().driverVersion("127.0.6533.73").setup();
        WebDriver driver = new ChromeDriver();
        driver.get(url);

        return driver;
    }

    List<T> crawling(Map<String, String> queryParam);

    default String buildUrlWithQueryParams(String baseUrl, Map<String, String> queryParams) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (!queryParams.isEmpty() && queryParams.values().size() > 0) {
            urlBuilder.append("?");
            queryParams.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
        }
        return urlBuilder.toString();
    }
}
