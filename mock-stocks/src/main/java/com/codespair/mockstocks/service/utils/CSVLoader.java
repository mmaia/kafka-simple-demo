package com.codespair.mockstocks.service.utils;

import com.codespair.mockstocks.model.StockDetail;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("csvParser")
public class CSVLoader {

    @SuppressWarnings("squid:S1166") // supress false positive check from sonarqube
    /**
     * @param filePath the filepath relative to the classpath where the csv is to be found i.e - /static/AMEX.csv
     * @returns a map where each key is a stock exchange symbol and each element is a StockDetail object filled with
     * data loaded from the CSVs
     */
    public Map loadExchangeCSV(String filePath) {
        Map<String, StockDetail> result;
        ColumnPositionMappingStrategy<StockDetail> loadStrategy = new ColumnPositionMappingStrategy<>();
        loadStrategy.setType(StockDetail.class);
        String[] columns = new String[]{"symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"}; // the fields to bind do in your JavaBean
        loadStrategy.setColumnMapping(columns);
        CsvToBean<StockDetail> csv = new CsvToBean<>();
        try {
            File resource = new ClassPathResource(filePath).getFile();
            FileReader fileReader = new FileReader(resource);
            CSVReader reader = new CSVReader(fileReader);
            result = stockDetailsBySymbol(csv.parse(loadStrategy, reader));
            fileReader.close();
        } catch (Exception e) {
            log.error("Failed to load csv file with exchange information because: {}", e.getMessage());
            throw new InvalidCSVPathException(e.getMessage());
        }
        return result;
    }

    private Map<String, StockDetail> stockDetailsBySymbol(List<StockDetail> stockDetailList) {
        Map<String, StockDetail> stockDetailMap = new HashMap<>();
        stockDetailList.forEach(stockDetail -> stockDetailMap.put(stockDetail.getSymbol(), stockDetail));
        return stockDetailMap;
    }
}
