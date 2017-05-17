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
import java.util.List;

@Slf4j
@Component
public class CSVParser {
    public void readCSV() throws Exception {
        File resource = new ClassPathResource("/static/NASDAQ.csv").getFile();
        CSVReader reader = new CSVReader(new FileReader(resource));
        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            log.info("Symbol: " + nextLine[0] + " Name: " + nextLine[1]);
        }
    }

    public void readCSVAsBeans() throws Exception {
        File resource = new ClassPathResource("/static/NASDAQ.csv").getFile();
        CSVReader reader = new CSVReader(new FileReader(resource));
        ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
        strat.setType(StockDetail.class);
        String[] columns = new String[] {"symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"}; // the fields to bind do in your JavaBean
        strat.setColumnMapping(columns);

        CsvToBean csv = new CsvToBean();
        List list = csv.parse(strat, reader);

        list.forEach((stockDetail) -> {
            log.info(stockDetail.toString());
        });

    }
}
