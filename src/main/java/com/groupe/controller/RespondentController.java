package com.groupe.controller;

import com.groupe.model.SubmissionReceipt;
import com.groupe.model.Survey;
import com.groupe.service.ResponseService;
import com.groupe.service.SurveyService;
import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/surveys")
public class RespondentController extends BaseController {

    private final SurveyService surveyService;
    private final ResponseService responseService;

    public RespondentController(SurveyService surveyService, ResponseService responseService) {
        this.surveyService = surveyService;
        this.responseService = responseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("surveys", surveyService.getPublishedSurveys());
        return "respondent/surveys";
    }

    @GetMapping("/thanks")
    public String thanks() {
        return "respondent/thanks";
    }

    @GetMapping("/{surveyId}")
    public String takeSurvey(@PathVariable("surveyId") Long surveyId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        Survey survey = surveyService.getSurveyDetails(surveyId).orElseThrow();
        if (!survey.isPublished()) {
            redirectAttributes.addFlashAttribute("errorMessage", "That survey is not currently available.");
            return "redirect:/surveys";
        }
        if ("AUTHENTICATED".equalsIgnoreCase(survey.getAccessMode()) && user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in to respond to this survey.");
            return "redirect:/auth/login?target=/surveys/" + surveyId;
        }
        model.addAttribute("survey", survey);
        return "respondent/take-survey";
    }

    @PostMapping("/{surveyId}/submit")
    public String submitSurvey(@PathVariable("surveyId") Long surveyId,
                               @RequestParam Map<String, String> requestParameters,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            SubmissionReceipt receipt = responseService.submitSurvey(
                    surveyId,
                    currentUser(session),
                    requestParameters.get("guestName"),
                    requestParameters.get("guestEmail"),
                    requestParameters);
            redirectAttributes.addFlashAttribute("successMessage", "Survey submitted successfully.");
            redirectAttributes.addFlashAttribute("submissionReceipt", receipt);
            return "redirect:/surveys/thanks";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/surveys/" + surveyId;
        }
    }
}
