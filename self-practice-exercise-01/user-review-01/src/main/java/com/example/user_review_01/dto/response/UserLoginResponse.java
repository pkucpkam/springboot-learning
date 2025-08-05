package com.example.user_review_01.dto.response;

import java.util.List;

public class UserLoginResponse {
    private String username;
    private String jwt;
    private List<String> roles;

    public UserLoginResponse(String username ,String jwt, List<String> roles) {
        this.username = username;
        this.jwt = jwt;
        this.roles = roles;
    }

    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }

    public String getUsername() { return username; }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
