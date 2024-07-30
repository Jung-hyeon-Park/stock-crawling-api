package com.parkjh.stockcrawlingapi.controller;

import com.parkjh.stockcrawlingapi.service.CompanyCrawlingService;
import com.parkjh.stockcrawlingapi.service.StockCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CompanyCrawlingService companyCrawlingService;
    private final StockCrawlingService stockCrawlingService;

    @GetMapping("/")
    public void test() {
        this.companyCrawlingService.companyCrawling();
    }

    @GetMapping("/2")
    public void test2() {
        this.stockCrawlingService.stockCrawling();
    }
}
