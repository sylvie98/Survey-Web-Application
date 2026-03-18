package com.groupe.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SurveyReport {
    private Survey survey;
    private Map<String, List<SurveyResult>> resultsByQuestion = new LinkedHashMap<>();
    private List<SurveyResponse> responses;

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Map<String, List<SurveyResult>> getResultsByQuestion() {
        return resultsByQuestion;
    }

    public void setResultsByQuestion(Map<String, List<SurveyResult>> resultsByQuestion) {
        this.resultsByQuestion = resultsByQuestion;
    }

    public List<SurveyResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<SurveyResponse> responses) {
        this.responses = responses;
    }
}
