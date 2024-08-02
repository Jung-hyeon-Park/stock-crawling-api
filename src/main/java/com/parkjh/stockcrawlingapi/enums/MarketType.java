package com.parkjh.stockcrawlingapi.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MarketType {
    KOSPI("KOSPI"),
    KOSDAQ("KOSDAQ"),
    KOSDAQ_GLOBAL("KOSDAQ GLOBAL"),
    KONEX("KONEX");

    private final String displayName;

    public static MarketType fromString(String text) {
        for (MarketType type : MarketType.values()) {
            if (type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for text " + text);
    }
}
