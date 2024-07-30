package com.parkjh.stockcrawlingapi.dto;

import com.parkjh.stockcrawlingapi.enums.MarketType;
import lombok.Builder;

@Builder
public class CompanyDto {
    private String companyCode;
    private String companyName;
    private MarketType marketType;
    private String department;
}
