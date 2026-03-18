package com.groupe.service;

import com.groupe.dao.UserDao;
import com.groupe.model.User;
import com.groupe.util.PasswordUtil;
import com.groupe.util.SessionUser;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public SessionUser login(String username, String password) {
        Optional<User> userOptional = userDao.findByUsername(username == null ? "" : username.trim());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        User user = userOptional.get();
        if (!PasswordUtil.matches(password == null ? "" : password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        if ("TEACHER".equalsIgnoreCase(user.getRole()) && !"APPROVED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new IllegalArgumentException(
                    "Teacher access is currently " + user.getApprovalStatus().toLowerCase() + ". Wait for administrator approval.");
        }

        return SessionUser.from(user);
    }

    public void registerTeacher(String fullName, String username, String email, String password) {
        if (isBlank(fullName) || isBlank(username) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("All registration fields are required.");
        }

        if (userDao.usernameExists(username.trim())) {
            throw new IllegalArgumentException("That username is already in use.");
        }

        if (userDao.emailExists(email.trim())) {
            throw new IllegalArgumentException("That email address is already in use.");
        }

        userDao.createTeacherRegistration(
                fullName.trim(),
                username.trim(),
                email.trim(),
                PasswordUtil.hash(password));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
