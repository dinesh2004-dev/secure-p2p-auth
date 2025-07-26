package com.dinesh.auth.anonauthbackend.service;

import com.dinesh.auth.anonauthbackend.dtos.FileItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface ServerService {
    public String sendFile(MultipartFile filePath, String choice, String peerName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException;
    public String requestFile(String fileName, String choice);
    public List<FileItem> getPeer1FileList();
    public List<FileItem> getPeer2FileList();
}
