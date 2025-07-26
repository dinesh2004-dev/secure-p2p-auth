package org.example;

import java.io.*;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public class Peer {
    private int port;
    private Set<String> knownPeers = new HashSet<>();
    private File sharedDir;
    private final PrivateKey myPrivateKey;
    private final PublicKey otherPeerPublicKey;

    public Peer(int port, File sharedDir, PrivateKey myPrivateKey, PublicKey otherPeerPublicKey) {
        this.port = port;
        this.sharedDir = sharedDir;
        this.myPrivateKey = myPrivateKey;
        this.otherPeerPublicKey = otherPeerPublicKey;
    }

    public PrivateKey getMyPrivateKey(){
        return myPrivateKey;
    }
    public PublicKey getOtherPeerPublicKey() {
        return otherPeerPublicKey;
    }

    public void addPeer(String hostPort) {
        knownPeers.add(hostPort);
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Peer server started on port " + port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new PeerHandler(clientSocket, this)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean hasFile(String filename) {
        File file = new File(sharedDir, filename);
        return file.exists();
    }

    public File getFile(String filename,String username) {
        File userDir = new File(sharedDir.getName()+"/"+username);
        File file = new File(userDir, filename);
        return file.exists() ? file : null;
    }

    public Set<String> getKnownPeers() {
        return knownPeers;
    }

    public File getSharedDir() {
        return sharedDir;
    }
}

