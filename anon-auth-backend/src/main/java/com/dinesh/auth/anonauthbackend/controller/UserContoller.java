package com.dinesh.auth.anonauthbackend.controller;

import com.dinesh.auth.anonauthbackend.dtos.UserDTO;
import com.dinesh.auth.anonauthbackend.responses.AuthResponse;
import com.dinesh.auth.anonauthbackend.responses.UsernameOnlyResponse;
import com.dinesh.auth.anonauthbackend.service.UserService;
import com.dinesh.auth.anonauthbackend.service.impl.TokenBlacklistService;
import com.dinesh.auth.anonauthbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/user")
@RestController
public class UserContoller {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<Integer> registerUser(@RequestBody UserDTO userDTO){
        int id = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> logninUser(@RequestBody UserDTO userDTO){
        try{
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
            );
            String token = jwtUtil.generateToken(userDTO.getUsername());
            UsernameOnlyResponse user = new UsernameOnlyResponse(userDTO.getUsername());
            AuthResponse authResponse = new AuthResponse(token, user);
            return ResponseEntity.status(HttpStatus.OK).body(authResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader){

        String token = authHeader.replace("Bearer ","");

        tokenBlacklistService.addToBlackList(token);

        return ResponseEntity.status(HttpStatus.OK).body("user Logged Out");
    }
}
