package com.dinesh.auth.anonauthbackend.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDTO {

    private String email;
    private String password;
    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
