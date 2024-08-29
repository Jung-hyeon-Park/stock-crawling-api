package com.parkjh.stockcrawlingapi.controller;

import com.parkjh.stockcrawlingapi.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CompanyService companyService;
//    private final StockCrawlingService stockCrawlingService;

    @PostMapping("/stock")
    public void stockCrawling() {
        this.companyService.save();
    }
}
