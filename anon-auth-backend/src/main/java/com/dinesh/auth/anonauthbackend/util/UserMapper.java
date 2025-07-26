package com.dinesh.auth.anonauthbackend.util;

import com.dinesh.auth.anonauthbackend.dtos.UserDTO;
import com.dinesh.auth.anonauthbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserDTO userDTO){
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setUsername(userDTO.getUsername());
        return user;
    }
}
