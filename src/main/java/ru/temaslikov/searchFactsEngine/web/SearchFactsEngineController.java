package ru.temaslikov.searchFactsEngine.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.temaslikov.searchFactsEngine.Constants;
import ru.temaslikov.searchFactsEngine.SearchFactsService;

import java.util.Set;
import java.util.TreeSet;

import static java.lang.System.currentTimeMillis;

/**
 * Created by temaslikov on 09.06.2017.
 */

@Controller
public class SearchFactsEngineController {

    private SearchFactsService searchFactsService;
    private long start, duration;
    private Set<Integer> result;

    public SearchFactsEngineController() {
        start = currentTimeMillis();

        searchFactsService = new SearchFactsService();
        searchFactsService.loadInfoboxMap(Constants.infoboxPath);

        duration = currentTimeMillis() - start;
        System.out.println("INFO: duration of get indexes to SearchFactsService: " + duration / 1000 + " seconds");

        // searchFactsService.printInfoboxMap();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView start(@RequestParam(value="expression", required = false) String expression) {
        ModelAndView modelAndView = new ModelAndView();
        String answer = null;
        if (expression != null) {
            // todo: обработать и вывести результат в jsp
            answer = "answer example";
        }
        modelAndView.addObject("expressionViewJSP", expression);
        modelAndView.addObject("answerViewJSP", answer);
        modelAndView.setViewName("mainSearchFacts");
        return modelAndView;
    }

    /*
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam(value="expression") String expression) {
        ModelAndView modelAndView = new ModelAndView();
        result = searchFactsService.findExpression(expression);

        modelAndView.addObject("expressionViewJSP", expression);
        modelAndView.addObject("titleMapViewJSP", searchService.getTitleMap());
        modelAndView.addObject("resultViewJSP", searchService.findExpression(expression));
        modelAndView.setViewName("mainSearch");
        return modelAndView;
    }
    */
}
