package com.dinesh.auth.anonauthbackend.responses;

public class UsernameOnlyResponse {
    private String username;

    public UsernameOnlyResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
