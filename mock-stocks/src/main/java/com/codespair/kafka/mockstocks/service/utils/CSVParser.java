package com.codespair.kafka.mockstocks.service.utils;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;

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
}
