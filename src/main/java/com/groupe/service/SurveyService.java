package com.groupe.service;

import com.groupe.dao.CourseDao;
import com.groupe.dao.ResponseDao;
import com.groupe.dao.SurveyDao;
import com.groupe.model.Course;
import com.groupe.model.Survey;
import com.groupe.model.SurveyOption;
import com.groupe.model.SurveyQuestion;
import com.groupe.model.SurveyReport;
import com.groupe.model.SurveyResponse;
import com.groupe.model.SurveyResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private final SurveyDao surveyDao;
    private final CourseDao courseDao;
    private final ResponseDao responseDao;

    public SurveyService(SurveyDao surveyDao, CourseDao courseDao, ResponseDao responseDao) {
        this.surveyDao = surveyDao;
        this.courseDao = courseDao;
        this.responseDao = responseDao;
    }

    public List<Survey> getPublishedSurveys() {
        return surveyDao.findPublished();
    }

    public List<Survey> getInitiatorSurveys(Long initiatorId) {
        return surveyDao.findByInitiator(initiatorId);
    }

    public List<Survey> getTeacherSurveys(Long teacherId) {
        return surveyDao.findByTeacher(teacherId);
    }

    public List<Survey> getAllSurveys() {
        return surveyDao.findAll();
    }

    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    public Optional<Survey> getSurveyDetails(Long surveyId) {
        return surveyDao.findDetailedById(surveyId);
    }

    public Optional<Survey> getSurveyForInitiator(Long surveyId, Long initiatorId) {
        Optional<Survey> surveyOptional = surveyDao.findDetailedById(surveyId);
        if (surveyOptional.isPresent() && initiatorId.equals(surveyOptional.get().getInitiatorId())) {
            return surveyOptional;
        }
        return Optional.empty();
    }

    public Optional<Survey> getSurveyForTeacher(Long surveyId, Long teacherId) {
        Optional<Survey> surveyOptional = surveyDao.findDetailedById(surveyId);
        if (surveyOptional.isEmpty()) {
            return Optional.empty();
        }

        for (Survey survey : surveyDao.findByTeacher(teacherId)) {
            if (surveyId.equals(survey.getId())) {
                return surveyOptional;
            }
        }
        return Optional.empty();
    }

    public Long createSurvey(Long initiatorId, Long courseId, String title, String description, String accessMode, boolean published) {
        validateSurveyInput(courseId, title, accessMode);
        Survey survey = new Survey();
        survey.setInitiatorId(initiatorId);
        survey.setCourseId(courseId);
        survey.setTitle(title.trim());
        survey.setDescription(description == null ? "" : description.trim());
        survey.setAccessMode(accessMode);
        survey.setPublished(published);
        return surveyDao.createSurvey(survey);
    }

    public void updateSurvey(Long surveyId, Long initiatorId, Long courseId, String title, String description, String accessMode, boolean published) {
        Survey survey = getSurveyForInitiator(surveyId, initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        validateSurveyInput(courseId, title, accessMode);
        survey.setCourseId(courseId);
        survey.setTitle(title.trim());
        survey.setDescription(description == null ? "" : description.trim());
        survey.setAccessMode(accessMode);
        survey.setPublished(published);
        surveyDao.updateSurvey(survey);
    }

    public void deleteSurvey(Long surveyId, Long initiatorId) {
        getSurveyForInitiator(surveyId, initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        surveyDao.deleteSurvey(surveyId);
    }

    public void addQuestion(Long surveyId, Long initiatorId, String questionText, Integer displayOrder) {
        getSurveyForInitiator(surveyId, initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        if (isBlank(questionText) || displayOrder == null) {
            throw new IllegalArgumentException("Question text and order are required.");
        }
        SurveyQuestion question = new SurveyQuestion();
        question.setSurveyId(surveyId);
        question.setQuestionText(questionText.trim());
        question.setDisplayOrder(displayOrder);
        surveyDao.createQuestion(question);
    }

    public SurveyQuestion getQuestionForInitiator(Long questionId, Long initiatorId) {
        SurveyQuestion question = surveyDao.findQuestionById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found."));
        Survey survey = getSurveyForInitiator(question.getSurveyId(), initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found."));
        question.setSurveyId(survey.getId());
        return question;
    }

    public void updateQuestion(Long questionId, Long initiatorId, String questionText, Integer displayOrder) {
        SurveyQuestion question = getQuestionForInitiator(questionId, initiatorId);
        if (isBlank(questionText) || displayOrder == null) {
            throw new IllegalArgumentException("Question text and order are required.");
        }
        question.setQuestionText(questionText.trim());
        question.setDisplayOrder(displayOrder);
        surveyDao.updateQuestion(question);
    }

    public void deleteQuestion(Long questionId, Long initiatorId) {
        getQuestionForInitiator(questionId, initiatorId);
        surveyDao.deleteQuestion(questionId);
    }

    public void addOption(Long questionId, Long initiatorId, String optionText, Integer displayOrder) {
        SurveyQuestion question = getQuestionForInitiator(questionId, initiatorId);
        if (isBlank(optionText) || displayOrder == null) {
            throw new IllegalArgumentException("Option text and order are required.");
        }
        SurveyOption option = new SurveyOption();
        option.setQuestionId(question.getId());
        option.setOptionText(optionText.trim());
        option.setDisplayOrder(displayOrder);
        surveyDao.createOption(option);
    }

    public SurveyOption getOptionForInitiator(Long optionId, Long initiatorId) {
        SurveyOption option = surveyDao.findOptionById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("Option not found."));
        SurveyQuestion question = getQuestionForInitiator(option.getQuestionId(), initiatorId);
        option.setQuestionId(question.getId());
        return option;
    }

    public void updateOption(Long optionId, Long initiatorId, String optionText, Integer displayOrder) {
        SurveyOption option = getOptionForInitiator(optionId, initiatorId);
        if (isBlank(optionText) || displayOrder == null) {
            throw new IllegalArgumentException("Option text and order are required.");
        }
        option.setOptionText(optionText.trim());
        option.setDisplayOrder(displayOrder);
        surveyDao.updateOption(option);
    }

    public void deleteOption(Long optionId, Long initiatorId) {
        getOptionForInitiator(optionId, initiatorId);
        surveyDao.deleteOption(optionId);
    }

    public SurveyReport getInitiatorReport(Long surveyId, Long initiatorId) {
        getSurveyForInitiator(surveyId, initiatorId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        return buildReport(surveyId);
    }

    public SurveyReport getTeacherReport(Long surveyId, Long teacherId) {
        getSurveyForTeacher(surveyId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        return buildReport(surveyId);
    }

    private SurveyReport buildReport(Long surveyId) {
        Survey survey = surveyDao.findDetailedById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found."));
        List<SurveyResult> rawResults = surveyDao.findResultsBySurvey(surveyId);
        List<SurveyResponse> responses = responseDao.findBySurvey(surveyId);

        Map<Long, Integer> totalsByQuestion = new LinkedHashMap<>();
        for (SurveyResult result : rawResults) {
            totalsByQuestion.merge(result.getQuestionId(), result.getVoteCount(), Integer::sum);
        }

        Map<String, List<SurveyResult>> groupedResults = new LinkedHashMap<>();
        for (SurveyResult result : rawResults) {
            int total = totalsByQuestion.getOrDefault(result.getQuestionId(), 0);
            result.setTotalResponses(total);
            result.setPercentage(total == 0 ? 0.0 : (result.getVoteCount() * 100.0) / total);
            groupedResults.computeIfAbsent(result.getQuestionText(), ignored -> new ArrayList<>()).add(result);
        }

        SurveyReport report = new SurveyReport();
        report.setSurvey(survey);
        report.setResultsByQuestion(groupedResults);
        report.setResponses(responses);
        return report;
    }

    private void validateSurveyInput(Long courseId, String title, String accessMode) {
        if (courseId == null || isBlank(title) || isBlank(accessMode)) {
            throw new IllegalArgumentException("Course, title, and access mode are required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
