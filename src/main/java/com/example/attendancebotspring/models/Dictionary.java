package com.example.attendancebotspring.models;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


@Slf4j
public class Dictionary {
    private Map<String, String> translations;

    public Dictionary(){
        translations = new HashMap<>();
        loadTranslationsFromFile();
    }

    private void loadTranslationsFromFile() {
        try {
            File file = new File("src/main/resources/dictionary.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");

                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    translations.put(key, value);
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            log.error("File not found " + e.getMessage());
        }
    }

    public String translate(String word) {
        return translations.getOrDefault(word, "Translation not found");
    }

}
