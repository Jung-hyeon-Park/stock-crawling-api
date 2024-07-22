package com.parkjh.stockcrawlingapi.dto;

import lombok.Builder;

@Builder
public class StockDto {
    private String transactionTime;
    private int transactionPrice;
    private int previousDayDifferencePrice;
    private int sellCount;
    private int buyCount;
    private int volumeCount;
    private int changeCount;
}
