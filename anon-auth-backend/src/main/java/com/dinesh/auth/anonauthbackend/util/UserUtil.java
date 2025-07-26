package com.dinesh.auth.anonauthbackend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    public String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder
                                                .getContext()
                                                .getAuthentication();
        String username = authentication.getName();
        return username;
    }
}
