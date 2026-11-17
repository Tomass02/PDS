package sk.uniza.veterina.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sk.uniza.veterina.model.VeterinarnyLiek;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class VeterinarnyLiekService {
    private static final Logger log = LoggerFactory.getLogger(VeterinarnyLiekService.class);
    private final HttpClient httpClient;
    private final String endpoint;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VeterinarnyLiekService(
            @Value("${veterinary.api-url:https://medicines.health.europa.eu/veterinary/en/search-medicines/{query}}")
            String endpoint) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.endpoint = endpoint;
    }

    public List<VeterinarnyLiek> search(String query, int limit) {
        if (endpoint.isBlank()) {
            throw new IllegalStateException("EU databaza veterinarnych liekov nie je nakonfigurovana.");
        }

        List<VeterinarnyLiek> result = loadForTerm(query, limit);
        if (!result.isEmpty()) {
            return result;
        }
        for (String suggestion : autocomplete(query)) {
            result = loadForTerm(suggestion, limit);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return List.of();
    }

    private List<VeterinarnyLiek> loadForTerm(String term, int limit) {
        String encodedQuery = URLEncoder.encode(term, StandardCharsets.UTF_8);
        String url = endpoint.replace("{query}", encodedQuery) + "?keys="
                + encodedQuery + "&conjunction=OR&page&_format=xlsx";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("User-Agent", "Mozilla/5.0 (Veterina)")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("EU databaza vratila HTTP " + response.statusCode() + ".");
            }
            return parseWorkbook(response.body(), limit);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.error("Vyhladavanie veterinarnych liekov zlyhalo.", exception);
            throw new IllegalStateException("EU databazu veterinarnych liekov sa nepodarilo zavolat.", exception);
        } catch (IOException exception) {
            log.error("Spracovanie vysledku veterinarnych liekov zlyhalo.", exception);
            throw new IllegalStateException("Vysledok EU databazy sa nepodarilo spracovat.", exception);
        }
    }

    private List<String> autocomplete(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String autocompleteEndpoint = endpoint
                .replace("/search-medicines/{query}", "/search-suggestions/autocomplete")
                .replace("/search-medicines", "/search-suggestions/autocomplete");
        String url = autocompleteEndpoint
                + "?display=page&filter=keys&q=" + encodedQuery;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Veterina)")
                    .GET()
                    .build();
            String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JsonNode root = objectMapper.readTree(body);
            List<String> suggestions = new ArrayList<>();
            for (JsonNode node : root) {
                String value = node.path("value").asText();
                if (!value.isBlank()) {
                    suggestions.add(value);
                }
            }
            return suggestions;
        } catch (IOException exception) {
            log.warn("Autocomplete veterinarnych liekov pre '{}' zlyhalo.", query, exception);
            return List.of();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    private List<VeterinarnyLiek> parseWorkbook(byte[] content, int limit) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(content))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(sheet.getFirstRowNum());
            if (header == null) {
                return List.of();
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : header) {
                headers.add(normalize(cellText(cell)));
            }

            List<VeterinarnyLiek> result = new ArrayList<>();
            for (int rowIndex = sheet.getFirstRowNum() + 1;
                 rowIndex <= sheet.getLastRowNum() && result.size() < limit;
                 rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                String name = value(row, headers, "name", "medicinal product", "product name", "názov");
                if (name.isBlank()) {
                    continue;
                }
                result.add(new VeterinarnyLiek(
                        value(row, headers, "id", "product id", "upc"),
                        name,
                        value(row, headers, "pharmaceutical form", "form"),
                        value(row, headers, "strength"),
                        value(row, headers, "active substance", "active ingredient"),
                        value(row, headers, "status", "authorisation status")
                ));
            }
            return result;
        }
    }

    private String value(Row row, List<String> headers, String... names) {
        for (String name : names) {
            String wanted = normalize(name);
            for (int index = 0; index < headers.size(); index++) {
                if (headers.get(index).contains(wanted)) {
                    return cellText(row.getCell(index));
                }
            }
        }
        return "";
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    private String cellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        return new DataFormatter().formatCellValue(cell).trim();
    }
}
