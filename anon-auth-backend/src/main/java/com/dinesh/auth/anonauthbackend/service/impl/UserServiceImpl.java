package com.dinesh.auth.anonauthbackend.service.impl;

import com.dinesh.auth.anonauthbackend.dtos.UserDTO;
import com.dinesh.auth.anonauthbackend.entity.User;
import com.dinesh.auth.anonauthbackend.repository.UserRepository;
import com.dinesh.auth.anonauthbackend.service.UserService;
import com.dinesh.auth.anonauthbackend.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    public UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public int registerUser(UserDTO userDTO) {
         User user = userMapper.toUser(userDTO);
         String encodedPassword = passwordEncoder.encode(user.getPassword());
         user.setPassword(encodedPassword);
         userRepository.save(user);
         return user.getId();
    }
}
