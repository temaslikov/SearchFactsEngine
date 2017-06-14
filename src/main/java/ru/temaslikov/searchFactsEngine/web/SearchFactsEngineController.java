package ru.temaslikov.searchFactsEngine.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.temaslikov.searchFactsEngine.Constants;
import ru.temaslikov.searchFactsEngine.SearchFactsService;
import scala.Option;

import java.io.File;

import static java.lang.System.currentTimeMillis;

/**
 * Created by temaslikov on 09.06.2017.
 */

@Controller
public class SearchFactsEngineController {

    private SearchFactsService searchFactsService;
    private long start, duration;

    public SearchFactsEngineController() {
        start = currentTimeMillis();

        searchFactsService = new SearchFactsService();
        searchFactsService.loadInfoboxMap(Constants.infoboxPath);

        duration = currentTimeMillis() - start;
        System.out.println("INFO: duration of get indexes to SearchFactsService: " + duration / 1000 + " seconds");

        // searchFactsService.printInfoboxMap();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView start(@RequestParam(value="expression", required = false) String expression) throws MyStemApplicationException {
        ModelAndView modelAndView = new ModelAndView();
        String answer = null;

        if (expression != null) {

            System.out.println(searchFactsService.modify(expression));
            answer = searchFactsService.searchFact(searchFactsService.modify(expression));
        }
        modelAndView.addObject("expressionViewJSP", expression);
        modelAndView.addObject("answerViewJSP", answer);
        modelAndView.setViewName("mainSearchFacts");
        return modelAndView;
    }
}
