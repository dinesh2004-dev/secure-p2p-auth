package com.dinesh.auth.anonauthbackend.repository;

import com.dinesh.auth.anonauthbackend.dtos.FileItem;
import com.dinesh.auth.anonauthbackend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

}
