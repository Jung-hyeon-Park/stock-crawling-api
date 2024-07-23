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

    // 추후에 api로 조회하게 수정
    private static final String[] CODES = {
        "45226K",
        "45014K",
        "38380K",
        "37550L",
        "37550K",
        "36328K",
        "35320K",
        "33637L",
        "33637K",
        "33626L",
        "33626K",
        "28513K",
        "26490K",
        "18064K",
        "03473K",
        "02826K",
        "00806K",
        "00781K",
        "00680K",
        "00499K",
        "00279K",
        "00104K",
        "00088K",
        "950210",
        "900140",
        "481850",
        "475150",
        "465770",
        "462870",
        "462520",
        "460860",
        "460850",
        "457190",
        "456040",
        "454910",
        "453340",
        "452260",
        "451800",
        "450140",
        "450080",
        "448730",
        "446070",
        "443060",
        "432320",
        "417310",
        "404990",
        "403550",
        "402340",
        "400760",
        "396690",
        "395400",
        "383800",
        "383220",
        "381970",
        "378850",
        "377740",
        "377300",
        "377190",
        "375500",
        "373220",
        "372910",
        "365550",
        "363280",
        "361610",
        "357430",
        "357250",
        "357120",
        "353200",
        "352820",
        "350520",
        "348950",
        "344820",
        "339770",
        "338100",
        "336370",
        "336260",
        "334890",
        "330590",
        "329180",
        "326030",
        "323410",
        "322000",
        "317400",
        "316140",
        "308170",
        "307950",
        "306200",
        "302440",
        "300720",
        "298690",
        "298050",
        "298040",
        "298020",
        "298000",
        "294870",
        "293940",
        "293480",
        "286940",
        "285130",
        "284740",
        "282690",
        "282330",
        "281820",
        "280360",
        "278470",
        "272550",
        "272450",
        "272210",
        "271980",
        "271940",
        "271560",
        "268280",
        "267850",
        "267290",
        "267270",
        "267260",
        "267250",
        "264900",
        "259960",
        "251270",
        "249420",
        "248170",
        "248070",
        "244920",
        "241590",
        "241560",
        "234080",
        "229640",
        "227840",
        "226320",
        "214420",
        "214390",
        "214330",
        "214320",
        "213500",
        "210980",
        "210540",
        "207940",
        "204320",
        "204210",
        "200880",
        "195870",
        "194370",
        "192820",
        "192650",
        "192400",
        "192080",
        "185750",
        "183190",
        "181710",
        "180640",
        "178920",
        "175330",
        "170900",
        "168490",
        "163560",
        "161890",
        "161390",
        "161000",
        "155660",
        "152550",
        "145995",
        "145990",
        "145720",
        "145270",
        "145210",
        "143210",
        "140910",
        "139990",
        "139480",
        "139130",
        "138930",
        "138490",
        "138040",
        "137310",
        "136490",
        "134790",
        "134380",
        "133820",
        "130660",
        "129260",
        "128940",
        "128820",
        "126720",
        "126560",
        "123890",
        "123700",
        "123690",
        "122900",
        "120115",
        "120110",
        "120030",
        "119650",
        "118000",
        "117580",
        "115390",
        "114090",
        "112610",
        "111770",
        "111380",
        "111110",
        "109070",
        "108675",
        "108670",
        "108320",
        "107590",
        "105840",
        "105630",
        "105560",
        "104700",
        "103590",
        "103140",
        "102460",
        "102280",
        "102260",
        "101530",
        "101140",
        "100840",
        "100250",
        "100220",
        "100090"
    };

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
