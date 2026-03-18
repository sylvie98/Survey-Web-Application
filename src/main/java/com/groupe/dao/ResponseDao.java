package com.groupe.dao;

import com.groupe.model.Respondent;
import com.groupe.model.SurveyResponse;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ResponseDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Respondent> respondentRowMapper = (resultSet, rowNum) -> {
        Respondent respondent = new Respondent();
        respondent.setId(resultSet.getLong("id"));
        long userId = resultSet.getLong("user_id");
        respondent.setUserId(resultSet.wasNull() ? null : userId);
        respondent.setName(resultSet.getString("name"));
        respondent.setEmail(resultSet.getString("email"));
        respondent.setGuest(resultSet.getBoolean("guest"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            respondent.setCreatedAt(createdAt.toLocalDateTime());
        }
        return respondent;
    };

    private final RowMapper<SurveyResponse> surveyResponseRowMapper = (resultSet, rowNum) -> {
        SurveyResponse response = new SurveyResponse();
        response.setId(resultSet.getLong("id"));
        response.setSurveyId(resultSet.getLong("survey_id"));
        response.setRespondentId(resultSet.getLong("respondent_id"));
        response.setRespondentName(resultSet.getString("respondent_name"));
        response.setRespondentEmail(resultSet.getString("respondent_email"));
        Timestamp submittedAt = resultSet.getTimestamp("submitted_at");
        if (submittedAt != null) {
            response.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        return response;
    };

    public ResponseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Respondent> findByUserId(Long userId) {
        List<Respondent> respondents = jdbcTemplate.query(
                "SELECT id, user_id, name, email, guest, created_at FROM respondents WHERE user_id = ?",
                respondentRowMapper,
                userId);
        return respondents.stream().findFirst();
    }

    public Optional<Respondent> findGuestByEmail(String email) {
        List<Respondent> respondents = jdbcTemplate.query(
                "SELECT id, user_id, name, email, guest, created_at FROM respondents WHERE email = ? AND guest = TRUE",
                respondentRowMapper,
                email);
        return respondents.stream().findFirst();
    }

    public Long createRespondent(Respondent respondent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO respondents (user_id, name, email, guest) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            if (respondent.getUserId() == null) {
                statement.setObject(1, null);
            } else {
                statement.setLong(1, respondent.getUserId());
            }
            statement.setString(2, respondent.getName());
            statement.setString(3, respondent.getEmail());
            statement.setBoolean(4, respondent.isGuest());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public boolean hasSubmitted(Long surveyId, Long respondentId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM survey_responses WHERE survey_id = ? AND respondent_id = ?",
                Integer.class,
                surveyId,
                respondentId);
        return count != null && count > 0;
    }

    public Long createSurveyResponse(Long surveyId, Long respondentId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO survey_responses (survey_id, respondent_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, surveyId);
            statement.setLong(2, respondentId);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public void saveAnswers(Long responseId, Map<Long, Long> answers) {
        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            jdbcTemplate.update(
                    "INSERT INTO response_answers (response_id, question_id, option_id) VALUES (?, ?, ?)",
                    responseId,
                    entry.getKey(),
                    entry.getValue());
        }
    }

    public List<SurveyResponse> findBySurvey(Long surveyId) {
        return jdbcTemplate.query(
                "SELECT sr.id, sr.survey_id, sr.respondent_id, r.name AS respondent_name, r.email AS respondent_email, sr.submitted_at "
                        + "FROM survey_responses sr "
                        + "JOIN respondents r ON r.id = sr.respondent_id "
                        + "WHERE sr.survey_id = ? ORDER BY sr.submitted_at DESC",
                surveyResponseRowMapper,
                surveyId);
    }
}
