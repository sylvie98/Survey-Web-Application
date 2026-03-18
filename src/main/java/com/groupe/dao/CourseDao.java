package com.groupe.dao;

import com.groupe.model.Course;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CourseDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Course> courseRowMapper = (resultSet, rowNum) -> {
        Course course = new Course();
        course.setId(resultSet.getLong("id"));
        course.setCode(resultSet.getString("code"));
        course.setName(resultSet.getString("name"));
        course.setDescription(resultSet.getString("description"));
        course.setTeacherNames(resultSet.getString("teacher_names"));
        return course;
    };

    public CourseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Course> findAll() {
        return jdbcTemplate.query(
                "SELECT c.id, c.code, c.name, c.description, "
                        + "GROUP_CONCAT(u.full_name ORDER BY u.full_name SEPARATOR ', ') AS teacher_names "
                        + "FROM courses c "
                        + "LEFT JOIN course_teachers ct ON ct.course_id = c.id "
                        + "LEFT JOIN users u ON u.id = ct.teacher_id "
                        + "GROUP BY c.id, c.code, c.name, c.description "
                        + "ORDER BY c.code",
                courseRowMapper);
    }

    public List<Course> findByTeacher(Long teacherId) {
        return jdbcTemplate.query(
                "SELECT c.id, c.code, c.name, c.description, ? AS teacher_names "
                        + "FROM courses c "
                        + "JOIN course_teachers ct ON ct.course_id = c.id "
                        + "WHERE ct.teacher_id = ? ORDER BY c.code",
                courseRowMapper,
                "",
                teacherId);
    }

    public Optional<Course> findById(Long id) {
        List<Course> courses = jdbcTemplate.query(
                "SELECT c.id, c.code, c.name, c.description, "
                        + "GROUP_CONCAT(u.full_name ORDER BY u.full_name SEPARATOR ', ') AS teacher_names "
                        + "FROM courses c "
                        + "LEFT JOIN course_teachers ct ON ct.course_id = c.id "
                        + "LEFT JOIN users u ON u.id = ct.teacher_id "
                        + "WHERE c.id = ? "
                        + "GROUP BY c.id, c.code, c.name, c.description",
                courseRowMapper,
                id);
        return courses.stream().findFirst();
    }

    public Long create(Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO courses (code, name, description) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, course.getCode());
            statement.setString(2, course.getName());
            statement.setString(3, course.getDescription());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public void update(Course course) {
        jdbcTemplate.update(
                "UPDATE courses SET code = ?, name = ?, description = ? WHERE id = ?",
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getId());
    }

    public void delete(Long courseId) {
        jdbcTemplate.update("DELETE FROM courses WHERE id = ?", courseId);
    }

    public void replaceTeacherAssignments(Long courseId, List<Long> teacherIds) {
        jdbcTemplate.update("DELETE FROM course_teachers WHERE course_id = ?", courseId);
        if (teacherIds == null || teacherIds.isEmpty()) {
            return;
        }
        for (Long teacherId : teacherIds) {
            jdbcTemplate.update(
                    "INSERT INTO course_teachers (course_id, teacher_id) VALUES (?, ?)",
                    courseId,
                    teacherId);
        }
    }

    public List<Long> findAssignedTeacherIds(Long courseId) {
        List<Long> teacherIds = jdbcTemplate.query(
                "SELECT teacher_id FROM course_teachers WHERE course_id = ? ORDER BY teacher_id",
                (resultSet, rowNum) -> resultSet.getLong("teacher_id"),
                courseId);
        return teacherIds == null ? Collections.emptyList() : teacherIds;
    }
}
