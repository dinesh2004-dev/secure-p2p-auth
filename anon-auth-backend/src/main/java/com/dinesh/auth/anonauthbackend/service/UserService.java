package com.dinesh.auth.anonauthbackend.service;

import com.dinesh.auth.anonauthbackend.dtos.UserDTO;

public interface UserService {

    int registerUser(UserDTO userDTO);
}
