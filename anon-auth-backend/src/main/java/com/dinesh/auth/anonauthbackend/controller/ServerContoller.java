package com.dinesh.auth.anonauthbackend.controller;

import com.dinesh.auth.anonauthbackend.dtos.FileItem;
import com.dinesh.auth.anonauthbackend.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.App;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RequestMapping("/api/v1/server")
@RestController
public class ServerContoller {
    @Autowired
    private ServerService serverService;

    @PostMapping("/sendFile")
    public ResponseEntity<String> sendFile(@RequestParam("file") MultipartFile file, @RequestParam String choice, @RequestParam  String peerName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        String response = serverService.sendFile(file,choice,peerName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/requestFile")
    public ResponseEntity<String> reqestFile(@RequestParam String fileName,@RequestParam String choice){
       String response = serverService.requestFile(fileName, choice);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/peer1/files")
    public ResponseEntity<List<FileItem>> getPeer1FileList() {
        List<FileItem> fileItems = serverService.getPeer1FileList();
        return ResponseEntity.status(HttpStatus.OK).body(fileItems);
    }

    @GetMapping("/peer2/files")
    public ResponseEntity<List<FileItem>> getPeer2FileList() {
        List<FileItem> fileItems = serverService.getPeer2FileList();
        return ResponseEntity.status(HttpStatus.OK).body(fileItems);
    }

}
