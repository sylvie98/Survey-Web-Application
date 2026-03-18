package com.groupe.controller;

import com.groupe.model.SurveyReport;
import com.groupe.service.SurveyService;
import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherController extends BaseController {

    private final SurveyService surveyService;

    public TeacherController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "TEACHER")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("surveys", surveyService.getTeacherSurveys(user.getId()));
        return "teacher/dashboard";
    }

    @GetMapping("/surveys/{surveyId}/results")
    public String results(@PathVariable("surveyId") Long surveyId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "TEACHER")) {
            return "redirect:/auth/login";
        }
        SurveyReport report = surveyService.getTeacherReport(surveyId, user.getId());
        model.addAttribute("report", report);
        return "teacher/results";
    }
}
