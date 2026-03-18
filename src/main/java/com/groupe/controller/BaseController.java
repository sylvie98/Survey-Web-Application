package com.groupe.controller;

import com.groupe.util.SessionUser;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    protected SessionUser currentUser(HttpSession session) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser instanceof SessionUser sessionUser) {
            return sessionUser;
        }
        return null;
    }

    protected boolean hasRole(SessionUser user, String... roles) {
        if (user == null) {
            return false;
        }
        for (String role : roles) {
            if (role.equalsIgnoreCase(user.getRole())) {
                return true;
            }
        }
        return false;
    }
}
