package ru.temaslikov.searchFactsEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by temaslikov on 09.06.17.
 */
public class SearchFactsService {

    private Map<String, Map<String, String>> infoboxMap;


    public SearchFactsService () {
        infoboxMap = new HashMap<>();

    }

    public String searchFact(String expression) {
        String answer = null;
        // todo: написать модуль
        return answer;
    }

    public void loadInfoboxMap(String path) {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
                for (Path entry: stream) {
                    if (!entry.toFile().isDirectory()) {

                        Files.lines(entry, StandardCharsets.UTF_8).forEach( (line) -> {
                            String[] lines = line.split(":", 3);
                            if (!infoboxMap.containsKey(lines[0])) {
                                infoboxMap.put(lines[0], new HashMap<>());
                            }
                            infoboxMap.get(lines[0]).put(lines[1], lines[2]);
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printInfoboxMap() {
        System.out.println("Print infoboxMap:");
        for (Map.Entry<String, Map<String, String>> entryKey : infoboxMap.entrySet()) {
            for (Map.Entry<String, String> entryValue : entryKey.getValue().entrySet()) {
                System.out.println(entryKey.getKey() + ":" + entryValue.getKey() + ":"
                        + entryValue.getValue() + "\n");
            }
        }
    }
}
