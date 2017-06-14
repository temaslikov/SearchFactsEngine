package ru.temaslikov.searchFactsEngine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by temaslikov on 04.06.17.
 */

public class InfoboxParseService {

    private Map<Integer, String> titleMap;
    private Map<Integer, Map<String, String>> infoboxMap;

    private String firstWord, secondWord;
    private Integer trTag;

    Pattern trPattern;
    Pattern checkPattern;
    Pattern spacePattern;
    Pattern delimiterPattern;

    InfoboxParseService () {
        titleMap = new HashMap<>();
        infoboxMap = new HashMap<>();

        trPattern = Pattern.compile("^(\\s|\t)*<tr>(\\s|\t)*$");
        checkPattern = Pattern.compile("(<td|<th)");
        spacePattern = Pattern.compile("\\s+");

        String delimiter = "\\s+|:+|!+|\\?|—+|\"+|»+|«+|&lt;.*?&gt;|<.*?>";
        delimiter += "|\\)+|\\(+|\\{+|\\}+|\\+|/+|№+|“+|„+|\\[.*?]";
        delimiter += "|…+";
        delimiter += "| +";// не пробел
        delimiter += "|&nbsp;?|&amp;?";
        delimiter += "|·+";
        delimiter += "<";
        delimiterPattern = Pattern.compile(delimiter);
    }

    public void loadTitleMap(String path) {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
                for (Path entry: stream) {
                    if (!entry.toFile().isDirectory()) {

                        Files.lines(entry, StandardCharsets.UTF_8).forEach( (line) -> {
                            String[] lines = line.split(" ", 2);
                            titleMap.put(Integer.parseInt(lines[0]), lines[1]);
                        });
                    }
                }
                //System.out.println(allTokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {

        for (Integer id : titleMap.keySet()) {
            System.out.println("parse id = " + id);
            String infoHtml = getHtmlInfobox(id);

            if (infoHtml != null) {
                parseHtmlInfobox(infoHtml, id);
            }
            System.out.println("--------------------------------");
        }
        writeInfobox();
    }

    private void writeInfobox() throws IOException {

        FileWriter writer = new FileWriter(Constants.infoboxPath + "infoboxes.txt", false);

        for (Map.Entry<Integer, Map<String, String>> entryKey : infoboxMap.entrySet()) {
            for (Map.Entry<String, String> entryValue : entryKey.getValue().entrySet()) {
                writer.write(titleMap.get(entryKey.getKey()).toLowerCase() + ":" + entryValue.getKey() + ":"
                + entryValue.getValue() + "\n");
            }
        }

        writer.close();
        titleMap.clear();
        infoboxMap.clear();
    }

    private String modify (String token, boolean isToLowerCase) {
        token = token.trim().replace(" ,", ",");
        token = spacePattern.matcher(token).replaceAll(" ");
        if (isToLowerCase)
            return token.toLowerCase();
        return token;
    }

    private void parseHtmlInfobox (String infoHtml, Integer id) {

        trTag = 4;
        String[] linesHtml = infoHtml.split("\n");

        for (String lineHtml:linesHtml) {
            parseLine(lineHtml, id);
        }
    }

    private void parseLine(String lineHtml, Integer id) {

        Matcher trMatcher = trPattern.matcher(lineHtml);
        Matcher checkMatcher = checkPattern.matcher(lineHtml);

        if (trMatcher.matches()) {
            trTag = 1;
            firstWord = null;
            secondWord = null;
        }
        else {
            if (!checkMatcher.find()) {
                if (trTag == 1 || trTag == 3)
                    firstWord = null;
                    secondWord = null;
                    trTag = 4;
                return;
            }
            else {
                trTag++;
                if (trTag == 2 || trTag == 3) {
                    // parse
                    String parseLine = delimiterPattern.matcher(lineHtml).replaceAll(" ");
                    if (trTag == 2)
                        firstWord = parseLine;
                    else
                        secondWord = parseLine;
                }
            }
        }

        if (firstWord != null && secondWord != null
                && !spacePattern.matcher(firstWord).matches()
                && !spacePattern.matcher(secondWord).matches()) {

            firstWord = modify(firstWord, true);
            secondWord = modify(secondWord, false);
            // иногда парсер пишет два раза одно слово (из-за image или просто разметки)
            String[] firstWords = firstWord.split(" ");
            String[] secondWords = secondWord.split(" ");
            if (firstWords.length == 2) {
                if (firstWords[0].equals(firstWords[1]))
                    firstWord = firstWords[0];
            }
            if (secondWords.length == 2) {
                if (secondWords[0].equals(secondWords[1]))
                    secondWord = secondWords[0];
            }

            System.out.println("firstWord = " + firstWord);
            System.out.println("secondWord = " + secondWord);
            if (infoboxMap.get(id) == null)
                infoboxMap.put(id, new HashMap<>());

            infoboxMap.get(id).put(firstWord, secondWord);
        }
    }

    private String getHtmlInfobox (int id) throws IOException {

        Connection.Response res = Jsoup.connect("http://ru.wikipedia.org/wiki?curid=" + id)
                .timeout(300*1000)
                .ignoreHttpErrors(true)
                .execute();
        String html = res.body();
        Document doc2 = Jsoup.parseBodyFragment(html);
        Element body = doc2.body();
        Elements tables = body.getElementsByTag("table");

        for (Element table : tables) {
            if (table.className().contains("infobox")) {
                return table.outerHtml();
            }
        }

        return null;
    }

}
