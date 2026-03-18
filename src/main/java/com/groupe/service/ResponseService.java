package com.groupe.service;

import com.groupe.dao.ResponseDao;
import com.groupe.model.Respondent;
import com.groupe.model.SubmissionReceipt;
import com.groupe.model.Survey;
import com.groupe.model.SurveyOption;
import com.groupe.model.SurveyQuestion;
import com.groupe.util.SessionUser;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    private final SurveyService surveyService;
    private final ResponseDao responseDao;
    private final EmailService emailService;

    public ResponseService(SurveyService surveyService, ResponseDao responseDao, EmailService emailService) {
        this.surveyService = surveyService;
        this.responseDao = responseDao;
        this.emailService = emailService;
    }

    public SubmissionReceipt submitSurvey(Long surveyId, SessionUser currentUser, String guestName, String guestEmail, Map<String, String> rawAnswers) {
        Survey survey = surveyService.getSurveyDetails(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));

        if (!survey.isPublished()) {
            throw new IllegalArgumentException("That survey is not currently published.");
        }

        Respondent respondent = resolveRespondent(survey, currentUser, guestName, guestEmail);
        if (responseDao.hasSubmitted(surveyId, respondent.getId())) {
            throw new IllegalArgumentException("This respondent has already completed the survey.");
        }

        Map<Long, Long> answers = validateAnswers(survey, rawAnswers);
        Long responseId = responseDao.createSurveyResponse(surveyId, respondent.getId());
        responseDao.saveAnswers(responseId, answers);

        SubmissionReceipt receipt = new SubmissionReceipt();
        receipt.setRespondentEmail(respondent.getEmail());
        receipt.setEmailStatus(emailService.sendSubmissionConfirmation(
                respondent.getEmail(),
                respondent.getName(),
                survey));
        return receipt;
    }

    private Respondent resolveRespondent(Survey survey, SessionUser currentUser, String guestName, String guestEmail) {
        if ("AUTHENTICATED".equalsIgnoreCase(survey.getAccessMode())) {
            if (currentUser == null) {
                throw new IllegalArgumentException("You must log in before responding to this survey.");
            }
            return responseDao.findByUserId(currentUser.getId())
                    .orElseGet(() -> createRespondent(currentUser.getId(), currentUser.getFullName(), currentUser.getEmail(), false));
        }

        if (currentUser != null) {
            return responseDao.findByUserId(currentUser.getId())
                    .orElseGet(() -> createRespondent(currentUser.getId(), currentUser.getFullName(), currentUser.getEmail(), false));
        }

        if (isBlank(guestName) || isBlank(guestEmail)) {
            throw new IllegalArgumentException("Guest respondents must provide a name and email address.");
        }

        return responseDao.findGuestByEmail(guestEmail.trim())
                .orElseGet(() -> createRespondent(null, guestName.trim(), guestEmail.trim(), true));
    }

    private Respondent createRespondent(Long userId, String name, String email, boolean guest) {
        Respondent respondent = new Respondent();
        respondent.setUserId(userId);
        respondent.setName(name);
        respondent.setEmail(email);
        respondent.setGuest(guest);
        Long respondentId = responseDao.createRespondent(respondent);
        respondent.setId(respondentId);
        return respondent;
    }

    private Map<Long, Long> validateAnswers(Survey survey, Map<String, String> rawAnswers) {
        Map<Long, Long> answers = new LinkedHashMap<>();
        for (SurveyQuestion question : survey.getQuestions()) {
            String optionIdText = rawAnswers.get("question_" + question.getId());
            if (isBlank(optionIdText)) {
                throw new IllegalArgumentException("Please answer every question before submitting.");
            }

            Long optionId;
            try {
                optionId = Long.parseLong(optionIdText);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("One of the selected answers was invalid.");
            }

            boolean optionBelongsToQuestion = false;
            for (SurveyOption option : question.getOptions()) {
                if (optionId.equals(option.getId())) {
                    optionBelongsToQuestion = true;
                    break;
                }
            }
            if (!optionBelongsToQuestion) {
                throw new IllegalArgumentException("One of the selected answers did not belong to the right question.");
            }

            answers.put(question.getId(), optionId);
        }
        return answers;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
