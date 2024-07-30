package com.parkjh.stockcrawlingapi.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface CrawlingService<T> {

    default Document fetchDocumentWithRetry(String url, int maxRetries) throws IOException {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return Jsoup.connect(url).timeout(10000).get();
            } catch (IOException e) {
                attempt++;
                if (attempt >= maxRetries) throw e;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IOException("Max retries exceeded for URL: " + url);
    }

    default List<T> getFutureResult(Future<List<T>> future) {
        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<T> crawling(Map<String, String> queryParam);

    default String buildUrlWithQueryParams(String baseUrl, Map<String, String> queryParams) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (!queryParams.isEmpty() && queryParams.values().size() > 0) {
            urlBuilder.append("?");
            queryParams.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
        }
        return urlBuilder.toString();
    }
}
