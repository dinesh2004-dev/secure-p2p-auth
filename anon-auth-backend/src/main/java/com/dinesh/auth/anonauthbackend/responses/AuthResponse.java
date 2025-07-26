package com.dinesh.auth.anonauthbackend.responses;

import com.dinesh.auth.anonauthbackend.dtos.UserDTO;

public class AuthResponse {
    private String token;
    private  UsernameOnlyResponse usernameOnlyResponse;
    public AuthResponse(String token, UsernameOnlyResponse  usernameOnlyResponse){
        this.token = token;
        this.usernameOnlyResponse =  usernameOnlyResponse;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public  UsernameOnlyResponse getUserDTO() {
        return  usernameOnlyResponse;
    }
    public void setUserDTO(UsernameOnlyResponse  usernameOnlyResponse) {
       this.usernameOnlyResponse =  usernameOnlyResponse;
    }
}
