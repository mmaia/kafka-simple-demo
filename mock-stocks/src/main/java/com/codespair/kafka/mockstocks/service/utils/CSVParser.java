package com.codespair.kafka.mockstocks.service.utils;

import com.codespair.kafka.mockstocks.model.StockDetail;
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
@Component
public class CSVParser {

    public Map loadExchangeCSV(String filePath) throws Exception {
        File resource = new ClassPathResource(filePath).getFile();
        CSVReader reader = new CSVReader(new FileReader(resource));
        ColumnPositionMappingStrategy<StockDetail> strat = new ColumnPositionMappingStrategy<>();
        strat.setType(StockDetail.class);
        String[] columns = new String[] {"symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"}; // the fields to bind do in your JavaBean
        strat.setColumnMapping(columns);
        CsvToBean<StockDetail> csv = new CsvToBean<>();
        return stockDetailsBySymbol(csv.parse(strat, reader));
    }

    private Map stockDetailsBySymbol(List<StockDetail> stockDetailList) {
        Map<String, StockDetail> stockDetailMap = new HashMap<>();
        stockDetailList.forEach((stockDetail) -> {
            stockDetailMap.put(stockDetail.getSymbol(), stockDetail);
        });
        return stockDetailMap;
    }

}
