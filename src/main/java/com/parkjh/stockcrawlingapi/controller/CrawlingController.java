package com.parkjh.stockcrawlingapi.controller;

import com.parkjh.stockcrawlingapi.service.CompanyService;
import com.parkjh.stockcrawlingapi.service.StockCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CompanyService companyService;
    private final StockCrawlingService stockCrawlingService;

    @GetMapping("/")
    public void test() {
        this.companyService.save();
    }

    @GetMapping("/2")
    public void test2() {
        this.stockCrawlingService.stockCrawling();
    }
}
