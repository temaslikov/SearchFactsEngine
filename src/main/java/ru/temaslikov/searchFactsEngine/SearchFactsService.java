package ru.temaslikov.searchFactsEngine;

import org.jetbrains.annotations.Nullable;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.File;
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

    private final static MyStem mystemAnalyzer =
            new Factory("-igd --eng-gr --format json --weight")
                    .newMyStem("3.0", Option.<File>empty()).get();

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

    private String lex(String expression) throws MyStemApplicationException {

        if (Constants.IS_LEX) {
            final Iterable<Info> result =
                    JavaConversions.asJavaIterable(
                            mystemAnalyzer
                                    .analyze(Request.apply(expression))
                                    .info()
                                    .toIterable());

            StringBuilder modifyExpression = new StringBuilder();
            for (final Info info : result)
                modifyExpression.append(info.lex().get()).append(" ");

            System.out.println("lex: " + modifyExpression.toString().trim());
            return modifyExpression.toString().trim();
        }

        else
            return expression;
    }

    public String modify(String expression) {
        return delimiterPattern.matcher(expression)
                .replaceAll(" ")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    public String searchFact(String expression) throws MyStemApplicationException {
        String answer;
        String[] words = expression.split(" ");
        int wordsLength = words.length;

        for (int i=1; i< wordsLength; i++) {
            String expectedPage = String.join(" ",
                    Arrays.copyOfRange(words, 0, i));
            String expectedField = String.join(" ",
                    Arrays.copyOfRange(words, i, wordsLength));

            answer = checkFact(lex(expectedPage), expectedField);
            if (answer == null)
                answer = checkFact(lex(expectedField), expectedPage);

            // если страница сама по себе склоняема, например фильм "без защиты"
            if (answer == null)
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
