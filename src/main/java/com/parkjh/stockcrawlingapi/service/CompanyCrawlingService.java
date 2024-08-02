package com.parkjh.stockcrawlingapi.service;

import com.parkjh.stockcrawlingapi.dto.CompanyDto;
import com.parkjh.stockcrawlingapi.enums.MarketType;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class CompanyCrawlingService implements CrawlingService<CompanyDto> {

    private static final String URL = "http://data.krx.co.kr/contents/MDC/MDI/mdiLoader/index.cmd?menuId=MDC0201020101";
    private static final int MAX_RETRIES = 1;

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

    private List<CompanyDto> fetchCompanyData(String url) {
        WebDriver webDriver = fetchWebDriver(url);
        List<CompanyDto> companyDtoList = new ArrayList<>();

        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody.CI-GRID-BODY-TABLE-TBODY")));

            // todo 화면 스크롤 필요

            for (WebElement tr : table.findElements(By.tagName("tr"))) {
                List<WebElement> tds = tr.findElements(By.tagName("td"));
                if (!tds.isEmpty() && tds.get(0) != null && !tds.get(0).getText().isEmpty()) {
                    System.out.println("tds.get(1).getText() = " + tds.get(1).getText());

                    companyDtoList.add(
                        CompanyDto.builder()
                            .companyCode(tds.get(0).getText())
                            .companyName(tds.get(1).getText())
                            .marketType(MarketType.fromString(tds.get(2).getText()))
                            .build()
                    );
                }
            }

        } catch (Exception e) {
            log.error("fetchCompanyData - {}", url, e);
        } finally {
            webDriver.quit();
        }

        return companyDtoList;
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
