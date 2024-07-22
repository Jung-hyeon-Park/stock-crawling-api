package com.parkjh.stockcrawlingapi.controller;

import com.parkjh.stockcrawlingapi.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/")
    public void test() {
        this.crawlingService.stockCrawling();
    }
}
