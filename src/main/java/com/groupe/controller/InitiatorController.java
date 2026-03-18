package com.groupe.controller;

import com.groupe.model.Survey;
import com.groupe.model.SurveyOption;
import com.groupe.model.SurveyQuestion;
import com.groupe.model.SurveyReport;
import com.groupe.service.SurveyService;
import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/initiator")
public class InitiatorController extends BaseController {

    private final SurveyService surveyService;

    public InitiatorController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("surveys", surveyService.getInitiatorSurveys(user.getId()));
        return "initiator/dashboard";
    }

    @GetMapping("/surveys/new")
    public String newSurvey(Model model, HttpSession session) {
        if (!hasRole(currentUser(session), "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("survey", new Survey());
        model.addAttribute("courses", surveyService.getAllCourses());
        return "initiator/survey-form";
    }

    @GetMapping("/surveys/{surveyId}/edit")
    public String editSurvey(@PathVariable("surveyId") Long surveyId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("survey", surveyService.getSurveyForInitiator(surveyId, user.getId()).orElseThrow());
        model.addAttribute("courses", surveyService.getAllCourses());
        return "initiator/survey-form";
    }

    @PostMapping("/surveys/save")
    public String saveSurvey(@RequestParam(value = "id", required = false) Long surveyId,
                             @RequestParam("courseId") Long courseId,
                             @RequestParam("title") String title,
                             @RequestParam(value = "description", required = false) String description,
                             @RequestParam("accessMode") String accessMode,
                             @RequestParam(value = "published", required = false) String publishedFlag,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }

        try {
            if (surveyId == null) {
                Long createdSurveyId = surveyService.createSurvey(
                        user.getId(),
                        courseId,
                        title,
                        description,
                        accessMode,
                        publishedFlag != null);
                redirectAttributes.addFlashAttribute("successMessage", "Survey created. You can add questions next.");
                return "redirect:/initiator/surveys/" + createdSurveyId + "/edit";
            }

            surveyService.updateSurvey(
                    surveyId,
                    user.getId(),
                    courseId,
                    title,
                    description,
                    accessMode,
                    publishedFlag != null);
            redirectAttributes.addFlashAttribute("successMessage", "Survey updated successfully.");
            return "redirect:/initiator";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return surveyId == null
                    ? "redirect:/initiator/surveys/new"
                    : "redirect:/initiator/surveys/" + surveyId + "/edit";
        }
    }

    @PostMapping("/surveys/{surveyId}/delete")
    public String deleteSurvey(@PathVariable("surveyId") Long surveyId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        surveyService.deleteSurvey(surveyId, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Survey deleted successfully.");
        return "redirect:/initiator";
    }

    @GetMapping("/surveys/{surveyId}/questions/new")
    public String newQuestion(@PathVariable("surveyId") Long surveyId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        Survey survey = surveyService.getSurveyForInitiator(surveyId, user.getId()).orElseThrow();
        SurveyQuestion question = new SurveyQuestion();
        question.setSurveyId(surveyId);
        model.addAttribute("survey", survey);
        model.addAttribute("question", question);
        return "initiator/question-form";
    }

    @GetMapping("/questions/{questionId}/edit")
    public String editQuestion(@PathVariable("questionId") Long questionId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        SurveyQuestion question = surveyService.getQuestionForInitiator(questionId, user.getId());
        Survey survey = surveyService.getSurveyForInitiator(question.getSurveyId(), user.getId()).orElseThrow();
        model.addAttribute("survey", survey);
        model.addAttribute("question", question);
        return "initiator/question-form";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@RequestParam(value = "id", required = false) Long questionId,
                               @RequestParam("surveyId") Long surveyId,
                               @RequestParam("questionText") String questionText,
                               @RequestParam("displayOrder") Integer displayOrder,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }

        try {
            if (questionId == null) {
                surveyService.addQuestion(surveyId, user.getId(), questionText, displayOrder);
                redirectAttributes.addFlashAttribute("successMessage", "Question added successfully.");
            } else {
                surveyService.updateQuestion(questionId, user.getId(), questionText, displayOrder);
                redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully.");
            }
            return "redirect:/initiator/surveys/" + surveyId + "/edit";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return questionId == null
                    ? "redirect:/initiator/surveys/" + surveyId + "/questions/new"
                    : "redirect:/initiator/questions/" + questionId + "/edit";
        }
    }

    @PostMapping("/questions/{questionId}/delete")
    public String deleteQuestion(@PathVariable("questionId") Long questionId,
                                 @RequestParam("surveyId") Long surveyId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        surveyService.deleteQuestion(questionId, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully.");
        return "redirect:/initiator/surveys/" + surveyId + "/edit";
    }

    @GetMapping("/questions/{questionId}/options/new")
    public String newOption(@PathVariable("questionId") Long questionId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        SurveyQuestion question = surveyService.getQuestionForInitiator(questionId, user.getId());
        Survey survey = surveyService.getSurveyForInitiator(question.getSurveyId(), user.getId()).orElseThrow();
        SurveyOption option = new SurveyOption();
        option.setQuestionId(questionId);
        model.addAttribute("survey", survey);
        model.addAttribute("question", question);
        model.addAttribute("option", option);
        return "initiator/option-form";
    }

    @GetMapping("/options/{optionId}/edit")
    public String editOption(@PathVariable("optionId") Long optionId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        SurveyOption option = surveyService.getOptionForInitiator(optionId, user.getId());
        SurveyQuestion question = surveyService.getQuestionForInitiator(option.getQuestionId(), user.getId());
        Survey survey = surveyService.getSurveyForInitiator(question.getSurveyId(), user.getId()).orElseThrow();
        model.addAttribute("survey", survey);
        model.addAttribute("question", question);
        model.addAttribute("option", option);
        return "initiator/option-form";
    }

    @PostMapping("/options/save")
    public String saveOption(@RequestParam(value = "id", required = false) Long optionId,
                             @RequestParam("questionId") Long questionId,
                             @RequestParam("surveyId") Long surveyId,
                             @RequestParam("optionText") String optionText,
                             @RequestParam("displayOrder") Integer displayOrder,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }

        try {
            if (optionId == null) {
                surveyService.addOption(questionId, user.getId(), optionText, displayOrder);
                redirectAttributes.addFlashAttribute("successMessage", "Option added successfully.");
            } else {
                surveyService.updateOption(optionId, user.getId(), optionText, displayOrder);
                redirectAttributes.addFlashAttribute("successMessage", "Option updated successfully.");
            }
            return "redirect:/initiator/surveys/" + surveyId + "/edit";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return optionId == null
                    ? "redirect:/initiator/questions/" + questionId + "/options/new"
                    : "redirect:/initiator/options/" + optionId + "/edit";
        }
    }

    @PostMapping("/options/{optionId}/delete")
    public String deleteOption(@PathVariable("optionId") Long optionId,
                               @RequestParam("surveyId") Long surveyId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        surveyService.deleteOption(optionId, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Option deleted successfully.");
        return "redirect:/initiator/surveys/" + surveyId + "/edit";
    }

    @GetMapping("/surveys/{surveyId}/results")
    public String results(@PathVariable("surveyId") Long surveyId, Model model, HttpSession session) {
        SessionUser user = currentUser(session);
        if (!hasRole(user, "SURVEY_INITIATOR")) {
            return "redirect:/auth/login";
        }
        SurveyReport report = surveyService.getInitiatorReport(surveyId, user.getId());
        model.addAttribute("report", report);
        return "initiator/results";
    }
}
