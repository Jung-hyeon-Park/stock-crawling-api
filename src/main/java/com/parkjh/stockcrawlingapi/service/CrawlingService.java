package com.parkjh.stockcrawlingapi.service;

import com.parkjh.stockcrawlingapi.dto.StockDto;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
public class CrawlingService {
    private static final String URL = "https://finance.naver.com/item/sise_time.naver";
    private static final String[] CODES = {"005930", "035720"};

    public void stockCrawling() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nowStr = now.format(formatter) + "235959";

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<List<StockDto>>> futures = Arrays.stream(CODES)
                .map(code -> executor.submit(() -> crawling(code, nowStr)))
                .collect(Collectors.toList());

            List<StockDto> allStockDtoList = futures.stream()
                .map(this::getFutureResult)
                .flatMap(List::stream)
                .collect(Collectors.toList());

            System.out.println("allStockDtoList.size = " + allStockDtoList.size());
        } catch (Exception e) {
            log.error("stockCrawling error - {}", e.getMessage(), e);
        }
    }

    private List<StockDto> crawling(String code, String thisTime) {
        List<StockDto> stockDtoList = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<List<StockDto>>> pageFutures = IntStream.rangeClosed(1, 40)
                .mapToObj(page -> {
                    String url = URL + "?code=" + code + "&page=" + page + "&thistime=" + thisTime;
                    return executor.submit(() -> fetchStockData(url));
                })
                .collect(Collectors.toList());

            stockDtoList = pageFutures.stream()
                .map(this::getFutureResult)
                .flatMap(List::stream)
                .collect(Collectors.toList());

            System.out.println("stockDtoList.size = " + stockDtoList.size());
        } catch (Exception e) {
            log.error("crawling error - {}", e.getMessage(), e);
        }

        return stockDtoList;
    }

    private List<StockDto> fetchStockData(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements trElements = doc.select("table").get(0).select("tr");

        return trElements.stream()
            .skip(1)
            .filter(element -> element.text() != null && !element.text().isEmpty())
            .map(this::parseTableRow)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private StockDto parseTableRow(Element row) {
        Elements tdElements = row.select("td");
        if (tdElements.size() < 7) return null;

        try {
            return StockDto.builder()
                .transactionTime(tdElements.get(0).text())
                .transactionPrice(parseNumber(tdElements.get(1).text()))
                .previousDayDifferencePrice(parsePreviousDayDifference(tdElements.get(2).text()))
                .sellCount(parseNumber(tdElements.get(3).text()))
                .buyCount(parseNumber(tdElements.get(4).text()))
                .volumeCount(parseNumber(tdElements.get(5).text()))
                .changeCount(parseNumber(tdElements.get(6).text()))
                .build();
        } catch (Exception e) {
            log.error("Error parsing table row: {}", e.getMessage(), e);
            return null;
        }
    }

    private int parseNumber(String text) {
        return Integer.parseInt(text.replaceAll(",", ""));
    }

    private int parsePreviousDayDifference(String text) {
        String[] splitStr = text.split(" ");
        if (splitStr.length != 2) return 0;

        int value = parseNumber(splitStr[1]);
        return splitStr[0].contains("하락") ? -value : value;
    }

    private List<StockDto> getFutureResult(Future<List<StockDto>> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Failed to retrieve data from future", e);
            return new ArrayList<>();
        }
    }
}
