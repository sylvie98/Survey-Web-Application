package com.groupe.dao;

import com.groupe.model.Survey;
import com.groupe.model.SurveyOption;
import com.groupe.model.SurveyQuestion;
import com.groupe.model.SurveyResult;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDao {

    private static final String SURVEY_SELECT =
            "SELECT s.id, s.course_id, s.initiator_id, s.title, s.description, s.access_mode, s.published, s.created_at, "
                    + "c.code AS course_code, c.name AS course_name, u.full_name AS initiator_name, "
                    + "COALESCE(sr.response_count, 0) AS response_count "
                    + "FROM surveys s "
                    + "JOIN courses c ON c.id = s.course_id "
                    + "JOIN users u ON u.id = s.initiator_id "
                    + "LEFT JOIN (SELECT survey_id, COUNT(*) AS response_count FROM survey_responses GROUP BY survey_id) sr "
                    + "ON sr.survey_id = s.id ";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Survey> surveyRowMapper = (resultSet, rowNum) -> {
        Survey survey = new Survey();
        survey.setId(resultSet.getLong("id"));
        survey.setCourseId(resultSet.getLong("course_id"));
        survey.setInitiatorId(resultSet.getLong("initiator_id"));
        survey.setCourseCode(resultSet.getString("course_code"));
        survey.setCourseName(resultSet.getString("course_name"));
        survey.setInitiatorName(resultSet.getString("initiator_name"));
        survey.setTitle(resultSet.getString("title"));
        survey.setDescription(resultSet.getString("description"));
        survey.setAccessMode(resultSet.getString("access_mode"));
        survey.setPublished(resultSet.getBoolean("published"));
        survey.setResponseCount(resultSet.getInt("response_count"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            survey.setCreatedAt(createdAt.toLocalDateTime());
        }
        return survey;
    };

    private final RowMapper<SurveyQuestion> questionRowMapper = (resultSet, rowNum) -> {
        SurveyQuestion question = new SurveyQuestion();
        question.setId(resultSet.getLong("id"));
        question.setSurveyId(resultSet.getLong("survey_id"));
        question.setQuestionText(resultSet.getString("question_text"));
        question.setDisplayOrder(resultSet.getInt("display_order"));
        return question;
    };

    private final RowMapper<SurveyOption> optionRowMapper = (resultSet, rowNum) -> {
        SurveyOption option = new SurveyOption();
        option.setId(resultSet.getLong("id"));
        option.setQuestionId(resultSet.getLong("question_id"));
        option.setOptionText(resultSet.getString("option_text"));
        option.setDisplayOrder(resultSet.getInt("display_order"));
        return option;
    };

    private final RowMapper<SurveyResult> resultRowMapper = (resultSet, rowNum) -> {
        SurveyResult surveyResult = new SurveyResult();
        surveyResult.setQuestionId(resultSet.getLong("question_id"));
        surveyResult.setQuestionText(resultSet.getString("question_text"));
        surveyResult.setOptionId(resultSet.getLong("option_id"));
        surveyResult.setOptionText(resultSet.getString("option_text"));
        surveyResult.setVoteCount(resultSet.getInt("vote_count"));
        return surveyResult;
    };

    public SurveyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Survey> findAll() {
        return jdbcTemplate.query(
                SURVEY_SELECT + " ORDER BY s.created_at DESC",
                surveyRowMapper);
    }

    public List<Survey> findPublished() {
        return jdbcTemplate.query(
                SURVEY_SELECT + " WHERE s.published = TRUE ORDER BY s.created_at DESC",
                surveyRowMapper);
    }

    public List<Survey> findByInitiator(Long initiatorId) {
        return jdbcTemplate.query(
                SURVEY_SELECT + " WHERE s.initiator_id = ? ORDER BY s.created_at DESC",
                surveyRowMapper,
                initiatorId);
    }

    public List<Survey> findByTeacher(Long teacherId) {
        return jdbcTemplate.query(
                SURVEY_SELECT
                        + " JOIN course_teachers ct ON ct.course_id = c.id "
                        + "WHERE ct.teacher_id = ? ORDER BY s.created_at DESC",
                surveyRowMapper,
                teacherId);
    }

    public Optional<Survey> findById(Long surveyId) {
        List<Survey> surveys = jdbcTemplate.query(
                SURVEY_SELECT + " WHERE s.id = ?",
                surveyRowMapper,
                surveyId);
        return surveys.stream().findFirst();
    }

    public Optional<Survey> findDetailedById(Long surveyId) {
        Optional<Survey> surveyOptional = findById(surveyId);
        if (surveyOptional.isEmpty()) {
            return surveyOptional;
        }

        Survey survey = surveyOptional.get();
        List<SurveyQuestion> questions = jdbcTemplate.query(
                "SELECT id, survey_id, question_text, display_order "
                        + "FROM survey_questions WHERE survey_id = ? ORDER BY display_order, id",
                questionRowMapper,
                surveyId);

        Map<Long, SurveyQuestion> questionMap = new LinkedHashMap<>();
        for (SurveyQuestion question : questions) {
            questionMap.put(question.getId(), question);
        }

        List<SurveyOption> options = jdbcTemplate.query(
                "SELECT o.id, o.question_id, o.option_text, o.display_order "
                        + "FROM survey_options o "
                        + "JOIN survey_questions q ON q.id = o.question_id "
                        + "WHERE q.survey_id = ? ORDER BY q.display_order, o.display_order, o.id",
                optionRowMapper,
                surveyId);

        for (SurveyOption option : options) {
            SurveyQuestion question = questionMap.get(option.getQuestionId());
            if (question != null) {
                question.getOptions().add(option);
            }
        }

        survey.setQuestions(new ArrayList<>(questionMap.values()));
        return Optional.of(survey);
    }

    public Optional<SurveyQuestion> findQuestionById(Long questionId) {
        List<SurveyQuestion> questions = jdbcTemplate.query(
                "SELECT id, survey_id, question_text, display_order "
                        + "FROM survey_questions WHERE id = ?",
                questionRowMapper,
                questionId);
        return questions.stream().findFirst();
    }

    public Optional<SurveyOption> findOptionById(Long optionId) {
        List<SurveyOption> options = jdbcTemplate.query(
                "SELECT id, question_id, option_text, display_order "
                        + "FROM survey_options WHERE id = ?",
                optionRowMapper,
                optionId);
        return options.stream().findFirst();
    }

    public Long createSurvey(Survey survey) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO surveys (course_id, initiator_id, title, description, access_mode, published) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, survey.getCourseId());
            statement.setLong(2, survey.getInitiatorId());
            statement.setString(3, survey.getTitle());
            statement.setString(4, survey.getDescription());
            statement.setString(5, survey.getAccessMode());
            statement.setBoolean(6, survey.isPublished());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public void updateSurvey(Survey survey) {
        jdbcTemplate.update(
                "UPDATE surveys SET course_id = ?, title = ?, description = ?, access_mode = ?, published = ? "
                        + "WHERE id = ?",
                survey.getCourseId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getAccessMode(),
                survey.isPublished(),
                survey.getId());
    }

    public void deleteSurvey(Long surveyId) {
        jdbcTemplate.update("DELETE FROM surveys WHERE id = ?", surveyId);
    }

    public Long createQuestion(SurveyQuestion question) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO survey_questions (survey_id, question_text, display_order) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, question.getSurveyId());
            statement.setString(2, question.getQuestionText());
            statement.setInt(3, question.getDisplayOrder());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public void updateQuestion(SurveyQuestion question) {
        jdbcTemplate.update(
                "UPDATE survey_questions SET question_text = ?, display_order = ? WHERE id = ?",
                question.getQuestionText(),
                question.getDisplayOrder(),
                question.getId());
    }

    public void deleteQuestion(Long questionId) {
        jdbcTemplate.update("DELETE FROM survey_questions WHERE id = ?", questionId);
    }

    public Long createOption(SurveyOption option) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO survey_options (question_id, option_text, display_order) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, option.getQuestionId());
            statement.setString(2, option.getOptionText());
            statement.setInt(3, option.getDisplayOrder());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public void updateOption(SurveyOption option) {
        jdbcTemplate.update(
                "UPDATE survey_options SET option_text = ?, display_order = ? WHERE id = ?",
                option.getOptionText(),
                option.getDisplayOrder(),
                option.getId());
    }

    public void deleteOption(Long optionId) {
        jdbcTemplate.update("DELETE FROM survey_options WHERE id = ?", optionId);
    }

    public List<SurveyResult> findResultsBySurvey(Long surveyId) {
        return jdbcTemplate.query(
                "SELECT q.id AS question_id, q.question_text, o.id AS option_id, o.option_text, "
                        + "COUNT(ra.id) AS vote_count "
                        + "FROM survey_questions q "
                        + "JOIN survey_options o ON o.question_id = q.id "
                        + "LEFT JOIN response_answers ra ON ra.option_id = o.id "
                        + "WHERE q.survey_id = ? "
                        + "GROUP BY q.id, q.question_text, o.id, o.option_text, q.display_order, o.display_order "
                        + "ORDER BY q.display_order, q.id, o.display_order, o.id",
                resultRowMapper,
                surveyId);
    }
}
