package ru.temaslikov.searchFactsEngine;

import java.io.IOException;
import java.util.OptionalDouble;

/**
 * Created by temaslikov on 4.06.2017.
 */


public class Main {

    public static void main(String[] args) throws Exception {

        runInfoboxParseService();
    }

    static void runInfoboxParseService() throws IOException {

        InfoboxParseService infoboxParseService = new InfoboxParseService();
        //infoboxParseService.loadTitleMap(Resources.titlePath);
        infoboxParseService.run();


    }

}





