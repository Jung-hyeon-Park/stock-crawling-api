package com.parkjh.stockcrawlingapi.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(name = "company_code", nullable = false, length = 10)
    private String companyCode;

    @Column(name = "transaction_time", nullable = false, length = 10)
    private String transactionTime;

    @Column(name = "transaction_price", nullable = false)
    private int transactionPrice;

    @Column(name = "previous_day_difference_price", nullable = false)
    private int previousDayDifferencePrice;

    @Column(name = "sell_count", nullable = false)
    private int sellCount;

    @Column(name = "buy_count", nullable = false)
    private int buyCount;

    @Column(name = "volume_count", nullable = false)
    private int volumeCount;

    @Column(name = "change_count", nullable = false)
    private int changeCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
