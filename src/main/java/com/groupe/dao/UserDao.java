package com.groupe.dao;

import com.groupe.model.User;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setApprovalStatus(resultSet.getString("approval_status"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    };

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
                "SELECT id, full_name, username, email, password_hash, role, approval_status, created_at "
                        + "FROM users WHERE username = ?",
                userRowMapper,
                username);
        return users.stream().findFirst();
    }

    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(
                "SELECT id, full_name, username, email, password_hash, role, approval_status, created_at "
                        + "FROM users WHERE id = ?",
                userRowMapper,
                id);
        return users.stream().findFirst();
    }

    public boolean usernameExists(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username);
        return count != null && count > 0;
    }

    public boolean emailExists(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                email);
        return count != null && count > 0;
    }

    public Long createTeacherRegistration(String fullName, String username, String email, String passwordHash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (full_name, username, email, password_hash, role, approval_status) "
                            + "VALUES (?, ?, ?, ?, 'TEACHER', 'PENDING')",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, fullName);
            statement.setString(2, username);
            statement.setString(3, email);
            statement.setString(4, passwordHash);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public List<User> findPendingTeachers() {
        return jdbcTemplate.query(
                "SELECT id, full_name, username, email, password_hash, role, approval_status, created_at "
                        + "FROM users WHERE role = 'TEACHER' AND approval_status = 'PENDING' ORDER BY created_at DESC",
                userRowMapper);
    }

    public List<User> findApprovedTeachers() {
        return jdbcTemplate.query(
                "SELECT id, full_name, username, email, password_hash, role, approval_status, created_at "
                        + "FROM users WHERE role = 'TEACHER' AND approval_status = 'APPROVED' ORDER BY full_name",
                userRowMapper);
    }

    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT id, full_name, username, email, password_hash, role, approval_status, created_at "
                        + "FROM users ORDER BY created_at DESC",
                userRowMapper);
    }

    public void updateTeacherStatus(Long userId, String approvalStatus) {
        jdbcTemplate.update(
                "UPDATE users SET approval_status = ? WHERE id = ? AND role = 'TEACHER'",
                approvalStatus,
                userId);
    }
}
