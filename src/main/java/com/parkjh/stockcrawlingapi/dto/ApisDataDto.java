package com.parkjh.stockcrawlingapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApisDataDto {
    private ApisResponseDto response;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApisResponseDto {
        private ApisHeader header;
        private ApisBody body;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApisHeader {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApisBody {
        private int numOfRows;
        private int pageNo;
        private long totalCount;
        private ApisItems items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApisItems {
        private List<ApisItem> item;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApisItem {
        private String basDt;
        private String srtnCd;
        private String isinCd;
        private String mrktCtg;
        private String itmsNm;
        private String crno;
        private String corpNm;
    }
}
