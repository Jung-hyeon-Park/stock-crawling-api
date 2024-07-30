package com.parkjh.stockcrawlingapi.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.parkjh.stockcrawlingapi.enums.MarketType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompanyInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyInfoId;

    @Column(name = "company_code", nullable = false, length = 10, unique = true)
    private String companyCode;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "market_type", nullable = false, length = 20)
    private MarketType marketType;

    @Column(name = "department", length = 50)
    private String department;

    private boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
