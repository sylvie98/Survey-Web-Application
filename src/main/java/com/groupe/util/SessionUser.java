package com.groupe.util;

import com.groupe.model.User;

public class SessionUser {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String approvalStatus;

    public static SessionUser from(User user) {
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(user.getId());
        sessionUser.setFullName(user.getFullName());
        sessionUser.setUsername(user.getUsername());
        sessionUser.setEmail(user.getEmail());
        sessionUser.setRole(user.getRole());
        sessionUser.setApprovalStatus(user.getApprovalStatus());
        return sessionUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(approvalStatus);
    }
}
