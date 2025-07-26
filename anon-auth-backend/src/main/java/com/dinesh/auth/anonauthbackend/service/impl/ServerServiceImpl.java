package com.dinesh.auth.anonauthbackend.service.impl;

import com.dinesh.auth.anonauthbackend.dtos.FileItem;
import com.dinesh.auth.anonauthbackend.entity.Server;
import com.dinesh.auth.anonauthbackend.entity.User;
import com.dinesh.auth.anonauthbackend.enums.PeerType;
import com.dinesh.auth.anonauthbackend.repository.ServerRepository;
import com.dinesh.auth.anonauthbackend.repository.UserRepository;
import com.dinesh.auth.anonauthbackend.service.ServerService;
import com.dinesh.auth.anonauthbackend.util.UserUtil;
import org.example.App;
import org.example.ECDSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService {
    private App app = new App();
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtil userUtil;

    private String myPrivateKeyBase64;
    private String otherPeerPublicKeyBase64;
    private String peerPublicKeyBase64;
    private PrivateKey myPrivateKey;
    private PublicKey otherPeerPublicKey;

    public ServerServiceImpl() throws IOException {
    }
    private void loadKeys(String choice,String username) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        List<String> keys = app.loadKeys(choice,username);
        this.myPrivateKeyBase64 = keys.get(0);
        this.otherPeerPublicKeyBase64 = keys.get(1);
        this.peerPublicKeyBase64 = keys.get(2);
        this.myPrivateKey = ECDSAUtil.loadPrivateKey(myPrivateKeyBase64);
        this.otherPeerPublicKey = ECDSAUtil.loadPublicKey(otherPeerPublicKeyBase64);
    }

    @Override
    public String sendFile(MultipartFile file, String choice, String peerName) throws IOException {
        String currentUser = userUtil.getCurrentUsername();
        try {
            loadKeys(choice,currentUser);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error loading keys: " + e.getMessage();
        }
        // Always resolve absolute path for upload directory
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        new File(uploadDir).mkdirs(); // Create the folder if not exists

        // Sanitize file name
        String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String filePath = uploadDir + File.separator + originalFileName;

        File destination = new File(filePath);
        file.transferTo(destination);

        String absolutePath = destination.getAbsolutePath();




        String response = app.sendFileToPeer(myPrivateKey,choice,peerName,absolutePath ,currentUser);
        User user = userRepository.findByUsername(currentUser)
                                  .orElseThrow(() -> new RuntimeException("User not found: " + currentUser));
        if(user == null) {
            return "User not found: " + currentUser;
        }
        if(response.equals("File sent successfully to " + peerName + ".")){
            String fileName = new File(absolutePath).getName();
            Server server = new Server();
            server.setFileName(fileName);
            server.setPeerType(PeerType.valueOf(peerName));
            server.setUser(user);
            serverRepository.save(server);
        }


        return response;

    }

    @Override
    public String requestFile(String fileName, String choice) {
        String currentUser = userUtil.getCurrentUsername();
        try {
            loadKeys(choice,currentUser);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error loading keys: " + e.getMessage();
        }

//        String peerName = "peer1"; // Assuming you want to request from peer1

        String response = app.requestFileFromNetwork(myPrivateKey,fileName, choice,currentUser);
        return response;

    }

    @Override
    public List<FileItem> getPeer1FileList(){
        String currentUser = userUtil.getCurrentUsername();
        List<String> files = serverRepository.findFileNameByUsernameAndPeerType(currentUser,PeerType.valueOf("peer1"));
        if (files.isEmpty()) {
            return List.of(); // Return an empty list if no files found
        }
        List<FileItem> fileItemList = files.stream()
                .map(fileName ->{
                    File file = new File(System.getProperty("user.dir")+File.separator+"uploads"+File.separator+fileName);
                    long size = file.length();
                    String lastModified = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(file.lastModified()));
                    FileItem fileItem = new FileItem(fileName,size,lastModified);
                    return fileItem;
                }).collect(Collectors.toList());
        return fileItemList;
    }

    @Override
    public List<FileItem> getPeer2FileList(){
        String currentUser = userUtil.getCurrentUsername();
        List<String> files = serverRepository.findFileNameByUsernameAndPeerType(currentUser,PeerType.valueOf("peer2"));
        if (files.isEmpty()) {
            return List.of(); // Return an empty list if no files found
        }
        List<FileItem> fileItemList = files.stream()
                .map(fileName ->{
                    File file = new File(System.getProperty("user.dir")+File.separator+"uploads"+File.separator+fileName);
                    long size = file.length();
                    String lastModified = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(file.lastModified()));
                    FileItem fileItem = new FileItem(fileName,size,lastModified);
                    return fileItem;
                }).collect(Collectors.toList());
        return fileItemList;
    }
}
