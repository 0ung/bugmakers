package com.bugmaker.apt.common.util.menuInit;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuCsvLoader {

    public List<String[]> load() throws Exception {

        ClassPathResource resource = new ClassPathResource("menu.csv");

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            return reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split(","))
                    .filter(arr -> arr.length >= 4)
                    .toList();
        }
    }
}
