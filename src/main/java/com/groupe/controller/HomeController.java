package com.groupe.controller;

import com.groupe.service.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final SurveyService surveyService;

    public HomeController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("publishedSurveys", surveyService.getPublishedSurveys());
        return "home";
    }
}
