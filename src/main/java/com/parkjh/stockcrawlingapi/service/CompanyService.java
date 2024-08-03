package com.parkjh.stockcrawlingapi.service;

import com.parkjh.stockcrawlingapi.dto.ApisDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class CompanyService {

    private static final String URL = "https://apis.data.go.kr/1160100/service/GetKrxListedInfoService/getItemInfo";
    private static final String SERVICE_KEY = "jWt83EDDTe/lTW+Avnwoi1u20sf20LtukAyEU0f95SUVKSGh0N4uCFMSGg3AWbYO2GYVujE5roWbs7ykqe3sOw==";
    private static final int NUM_OF_ROWS = 10000;

    private final RestTemplate restTemplate;

    public void save() {
        try {
            long totalCount = fetchTotalCount();
            int totalPages = calculateTotalPages(totalCount);

            log.info("Company total count: {}", totalCount);
            log.info("Company total pages: {}", totalPages);

            List<ApisDataDto> apisDataDtoList = fetchAllPages(totalPages);
            processResults(apisDataDtoList);

        } catch (Exception e) {
            log.error("Error occurred while fetching data.", e);
        }
    }

    private long fetchTotalCount() throws Exception {
        ApisDataDto totalCountDataDto = fetchData(1);
        return totalCountDataDto.getResponse().getBody().getTotalCount();
    }

    private int calculateTotalPages(long totalCount) {
        return (int) Math.ceil((double) totalCount / NUM_OF_ROWS);
    }

    private List<ApisDataDto> fetchAllPages(int totalPages) {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<ApisDataDto>> futures = IntStream.rangeClosed(1, totalPages)
            .mapToObj(pageNo -> executor.submit(() -> fetchData(pageNo)))
            .collect(Collectors.toList());

        List<ApisDataDto> apisDataDtoList = new ArrayList<>();
        for (Future<ApisDataDto> future : futures) {
            try {
                ApisDataDto apisDataDto = future.get();
                apisDataDtoList.add(apisDataDto);
                log.info("Fetched data for page: {}", apisDataDto.getResponse().getBody().getPageNo());
            } catch (Exception e) {
                log.error("Error occurred while fetching data", e);
            }
        }

        executor.shutdown();
        return apisDataDtoList;
    }

    private ApisDataDto fetchData(int pageNo) throws Exception {
        String encodedServiceKey = URLEncoder.encode(SERVICE_KEY, StandardCharsets.UTF_8.toString());

        URI uri = UriComponentsBuilder.fromHttpUrl(URL)
            .queryParam("serviceKey", encodedServiceKey)
            .queryParam("numOfRows", NUM_OF_ROWS)
            .queryParam("pageNo", pageNo)
            .queryParam("resultType", "json")
            .build(true)
            .toUri();

        return this.restTemplate.getForObject(uri, ApisDataDto.class);
    }

    private void processResults(List<ApisDataDto> apisDataDtoList) {
        log.info("Total pages fetched: {}", apisDataDtoList.size());
        int totalSize = 0;

        for (ApisDataDto apisDataDto : apisDataDtoList) {
            totalSize += apisDataDto.getResponse().getBody().getItems().getItem().size();
        }

        System.out.println("totalSize = " + totalSize);
    }
}
