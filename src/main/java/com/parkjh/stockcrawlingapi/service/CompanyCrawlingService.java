package com.parkjh.stockcrawlingapi.service;

import com.parkjh.stockcrawlingapi.dto.CompanyDto;
import com.parkjh.stockcrawlingapi.enums.MarketType;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CompanyCrawlingService implements CrawlingService<CompanyDto> {

    private static final String URL = "http://data.krx.co.kr/contents/MDC/MDI/mdiLoader/index.cmd?menuId=MDC0201020101";
    private static final int MAX_RETRIES = 3;
    private static final int MAX_SIZE_THRESHOLD = 400;

    public void companyCrawling() {
        List<CompanyDto> allCompanyDtoList = crawling(Map.of());
        log.info("allStockDtoList.size : {}", allCompanyDtoList.size());
    }

    @Override
    public List<CompanyDto> crawling(Map<String, String> queryParams) {
        List<CompanyDto> companyDtoList = new ArrayList<>();
        int retries = 0;

        while (retries < MAX_RETRIES) {
            companyDtoList.clear();
            try {
                String url = buildUrlWithQueryParams(URL, queryParams);
                companyDtoList = fetchCompanyData(url);

//                if (companyDtoList.size() == MAX_SIZE_THRESHOLD) break;

                retries++;
                log.warn("companyDtoList size is below threshold : ({}), retrying {}/{}", companyDtoList.size(), retries, MAX_RETRIES);
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("crawling error - {}", e.getMessage(), e);
                retries++;
                if (retries >= MAX_RETRIES) {
                    log.error("Max retries reached, aborting.");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("companyDtoList.size : {}", companyDtoList.size());
        return companyDtoList;
    }

    private List<CompanyDto> fetchCompanyData(String url) throws IOException {
        Document doc = fetchDocumentWithRetry(url, 3);
        Elements trElements = doc.select("div");

        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

        System.out.println("trElements = " + trElements.text());

        for (Element trElement : trElements) {
            // CI-GRID-BODY-TABLE
            System.out.println(trElement.text());
        }
//        Elements trElements = doc.select("table").get(0).select("tr");

        return trElements.stream()
            .skip(1)
            .filter(element -> element.text() != null && !element.text().isEmpty())
            .map(this::parseTableRow)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private CompanyDto parseTableRow(Element row) {
        Elements tdElements = row.select("td");
        if (tdElements.size() < 7) return null;

        try {
            return CompanyDto.builder()
                .companyCode(tdElements.get(0).text())
                .companyName(tdElements.get(1).text())
                .marketType(MarketType.valueOf(tdElements.get(2).text()))
                .department(tdElements.get(3).text())
                .build();
        } catch (Exception e) {
            log.error("Error parsing table row: {}", e.getMessage(), e);
            return null;
        }
    }
}
