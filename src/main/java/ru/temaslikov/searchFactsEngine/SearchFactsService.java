package ru.temaslikov.searchFactsEngine;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by temaslikov on 09.06.17.
 */
public class SearchFactsService {

    private Map<String, Map<String, String>> infoboxMap;

    private Pattern delimiterPattern;

    public SearchFactsService () {
        infoboxMap = new HashMap<>();

        String delimiter = "\\s+|:+|!+|\\?|—+|\"+|»+|«+|&lt;.*?&gt;|<.*?>";
        delimiter += "|\\)+|\\(+|\\{+|\\}+|\\+|/+|№+|“+|„+|\\[.*?]";
        delimiter += "|…+";
        delimiter += "| +";// не пробел
        delimiter += "|&nbsp;?|&amp;?";
        delimiter += "|·+";
        delimiter += "<";
        delimiter += "|-+";
        delimiterPattern = Pattern.compile(delimiter);
    }

    public String modify(String expression) {
        return delimiterPattern.matcher(expression)
                .replaceAll(" ")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    public String searchFact(String expression) {
        String answer;
        String[] words = expression.split(" ");
        int wordsLength = words.length;

        for (int i=1; i< wordsLength; i++) {
            String expectedPage = String.join(" ",
                    Arrays.copyOfRange(words, 0, i));
            String expectedField = String.join(" ",
                    Arrays.copyOfRange(words, i, wordsLength));

            answer = checkFact(expectedPage, expectedField);
            if (answer == null)
                answer = checkFact(expectedField, expectedPage);

            if (answer != null)
                return answer;
        }
        return null;
    }

    @Nullable
    private String checkFact(String expectedPage, String expectedField) {
        if (infoboxMap.containsKey(expectedPage))
            if (infoboxMap.get(expectedPage).containsKey(expectedField))
                return infoboxMap.get(expectedPage).get(expectedField);

        return null;
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
