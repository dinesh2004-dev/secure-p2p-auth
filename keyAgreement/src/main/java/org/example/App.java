package org.example;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static Set<String> knownPeers = new HashSet<>();



    public App() throws IOException {
        // Initialize known peers
        knownPeers.add("localhost:5001");
        knownPeers.add("localhost:5002");
    }
    // Add to App.java
    private static PublicKey getServerPublicKey(String peerName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyFile;

        if ("peer1".equalsIgnoreCase(peerName)) {
            keyFile = "D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_public.key";
        } else if ("peer2".equalsIgnoreCase(peerName)) {
            keyFile = "D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_public.key";
        } else {
            throw new IllegalArgumentException("Unknown peer: " + peerName);
        }

//        System.out.println("🔍 Loading key from: " + keyFile);
        String keyBase64 = new String(Files.readAllBytes(Paths.get(keyFile))).trim();

        if (keyBase64.isEmpty()) {
            throw new IOException(" Key file is empty.");
        }

        PublicKey key = ECDSAUtil.loadPublicKey(keyBase64);

        if (key == null) {
            throw new IllegalStateException("loadPublicKey returned null");
        }

//        System.out.println("Loaded public key: " + key.getAlgorithm());
        return key;
    }


    private static boolean authenticatePeer(DataInputStream in,DataOutputStream out,PrivateKey myPrivateKey,PublicKey serverPublicKey,String peerIdentity,String username) throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        int challengeLen = in.readInt();
        byte[] challenge = new byte[challengeLen];
        in.readFully(challenge);

        String signature = ECDSAUtil.sign(challenge, myPrivateKey);
        byte[] sigBytes = signature.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        out.writeInt(sigBytes.length);
        out.write(sigBytes);
        String authResult = in.readUTF();
        System.out.println("Client Authentication result: " + authResult);
        if ("AUTH_FAILED".equals(authResult)) {
            System.out.println("Client Authentication failed. Please check your keys.");
            return false;
        }
        // 1. Client generates challenge
        byte[] clientChallenge = new byte[32]; // 32 bytes for challenge
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(clientChallenge);
        out.writeInt(clientChallenge.length);
        out.write(clientChallenge);

        // 2. Client receives signature
        int signLen = in.readInt();
        byte[] serverSigBytes = new byte[signLen];
        in.readFully(serverSigBytes);
        String serverSignature = new String(serverSigBytes, StandardCharsets.UTF_8);
//        serverSignature = serverSignature +"tampered"; // Tamper: flip a bit in the signature

        // 3.client verifies server's signature
        if(!ECDSAUtil.verify(clientChallenge,serverSignature,serverPublicKey)){
            System.out.println("Server signature verification failed. Please check your keys.");
            return false;
        }
        out.writeUTF("AUTH_SUCCESS");

        // Update CryptoService state for successful ECDSA signature validation
        if ("AUTH_SUCCESS".equals(authResult)) {
            CryptoService.setValidECDSASignature(peerIdentity,username, true);
        }

        return "AUTH_SUCCESS".equals(authResult);
    }
    public static List<String> loadKeys(String choice,String username) throws IOException {
        String myPrivateKeyBase64, otherPeerPublicKeyBase64,peerPublicKeyBase64;
        if ("1".equals(choice)) {
            myPrivateKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_private.key"))
            );
            otherPeerPublicKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_public.key"))
            );
            peerPublicKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_public.key"))
            );
        } else {
            myPrivateKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_private.key"))
            );
            otherPeerPublicKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_public.key"))
            );
            peerPublicKeyBase64 = new String(
                    Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_public.key"))
            );
        }
        CryptoService.setKeysLoaded((choice.equals("1") ? "peer1" : "peer2") ,username,true);
        return Arrays.asList(myPrivateKeyBase64,otherPeerPublicKeyBase64,peerPublicKeyBase64);
    }
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println("Welcome to the P2P Network Console!");
        System.out.println("This app is for interacting with running peers (peer1, peer2, etc.).");
        // At the start of main()
        System.out.print("Select peer identity (1 for peer1, 2 for peer2): ");
        String choice = scanner.nextLine();
//        List<String> keys = loadKeys(choice);
//        String myPrivateKeyBase64 = keys.get(0);
//        String otherPeerPublicKeyBase64 = keys.get(1);
//        String peerPublicKeyBase64 = keys.get(2);
//        String myPrivateKeyBase64, otherPeerPublicKeyBase64,peerPublicKeyBase64;
//        if ("1".equals(choice)) {
//            myPrivateKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer1_private.key"))
//            );;
//            otherPeerPublicKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer2_public.key"))
//            );
//            peerPublicKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer1_public.key"))
//            );
//        } else {
//            myPrivateKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer2_private.key"))
//            );;
//            otherPeerPublicKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer1_public.key"))
//            );;
//            peerPublicKeyBase64 = new String(
//                    Files.readAllBytes(Paths.get("src/main/resources/org/example/keys/peer2_public.key"))
//            );
//        }

        // Load keys
//        PrivateKey myPrivateKey = ECDSAUtil.loadPrivateKey(myPrivateKeyBase64);
//        PublicKey otherPeerPublicKey = ECDSAUtil.loadPublicKey(otherPeerPublicKeyBase64);


        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Add known peer");
            System.out.println("2. Send file to peer");
            System.out.println("3. Request file from network");
            System.out.println("4. List known peers");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            String choose = scanner.nextLine();
            switch (choose) {
                case "1":
                    addKnownPeer();
                    break;
                case "2":
//                    sendFileToPeer(myPrivateKey,choice);
                    break;
                case "3":
//                    requestFileFromNetwork(myPrivateKey,choice);
                    break;
                case "4":
                    listKnownPeers();
                    break;
                case "5":
                    System.exit(0);
            }
        }
    }

    public static void addKnownPeer() {
        System.out.print("Enter peer name (peer1 or peer2): ");
        String peerName = scanner.nextLine().trim();
        String hostPort = null;
        if (peerName.equalsIgnoreCase("peer1")) {
            hostPort = "localhost:5001";
        } else if (peerName.equalsIgnoreCase("peer2")) {
            hostPort = "localhost:5002";
        } else {
            System.out.println("Invalid peer name. Use 'peer1' or 'peer2'.");
            return;
        }
        knownPeers.add(hostPort);
        System.out.println("Peer added: " + hostPort);
    }

    public static String sendFileToPeer(PrivateKey myPrivateKey,String choice,String peerName,String filePath,String username) {
        // Get peer name and file path from user input
//        System.out.print("Enter peer name to send to (peer1 or peer2): ");
//        String peerName = scanner.nextLine().trim();

        String hostPort = null;
        if (peerName.equalsIgnoreCase("peer1")) {
            hostPort = "localhost:5001";
        } else if (peerName.equalsIgnoreCase("peer2")) {
            hostPort = "localhost:5002";
        } else {
//            System.out.println("Invalid peer name. Use 'peer1' or 'peer2'.");
            return "Invalid peer name. Use 'peer1' or 'peer2'.";
        }
//        System.out.print("Enter full path to file to send: ");
//        String filePath = scanner.nextLine().trim();
//        while (filePath.isEmpty()) {
//            System.out.print("File path cannot be empty. Enter full path to file to send: ");
//            filePath = scanner.nextLine().trim();
//        }
        System.out.println("Checking file path: " + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
//            System.out.println("File not found at: " + filePath);
            return "File not found at: " + filePath;
        }
        String[] parts = hostPort.split(":");

        try (Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            String peerIdentity = choice.equals("1") ? "peer1" : "peer2";
            out.writeUTF(username);
            out.writeUTF(choice.equals("1") ? "peer1" : "peer2");
            try {

                PublicKey serverPublicKey = getServerPublicKey(peerName);
                if (myPrivateKey == null) {
                    System.out.println("Private key is null for " + peerName);
                    return "Private key is null for " + peerName;
                }

                if (!authenticatePeer(in, out, myPrivateKey,serverPublicKey,peerIdentity,username)) {
//                    System.out.println("Authentication failed");
                    return "Authentication failed";
                }
            }
            catch (Exception e){

//                System.out.println("Authentication error: " + e.getMessage());
                return "Authentication error: " + e.getMessage();
            }

            SecretKey aesKey;
            try{
                aesKey = CryptoUtil.generateAESKEY(out, in);
                // Update CryptoService state for AES key generation
                CryptoService.setActiveAESKey(peerIdentity,username, true);
                // Update CryptoService state for successful ECDSA signature validation
                CryptoService.setECDHHandshake(peerIdentity, username,true);
            } catch (Exception e) {
//                System.out.println("Error generating AES key: " + e.getMessage());
                return "Error generating AES key: " + e.getMessage();
            }


            out.writeUTF("SEND_FILE");
            out.writeUTF(file.getName());
            out.writeUTF(username);
            out.writeLong(file.length());
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];

                SecureRandom secureRandom = new SecureRandom();
                byte[] iv = new byte[12]; // GCM standard IV size
                secureRandom.nextBytes(iv);
                out.writeInt(iv.length);
                out.write(iv);

                int read;
                while ((read = fis.read(buffer)) != -1) {
                    try {
                        byte[] encrypted = CryptoUtil.encryptAES(
                                Arrays.copyOf(buffer, read), aesKey, iv
                        );
                        out.writeInt(encrypted.length);
                        out.write(encrypted);
                    } catch (Exception e) {
//                        System.out.println("Error While Encrypting: " + e.getMessage());
                        return "Error While Encrypting: " + e.getMessage();
                    }

                }
            }
            String response = in.readUTF();
            System.out.println("Response: " + response);
        } catch (IOException e) {
//            System.out.println("Failed to send file: " + e.getMessage());
            return "Failed to send file: " + e.getMessage();
        }
        return "File sent successfully to " + peerName + ".";
    }

    public static String requestFileFromNetwork(PrivateKey myPrivateKey,String fileName,String choice,String username) {
//        System.out.print("Enter filename to request: ");
//        String filename = scanner.nextLine();
        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + File.separator + "Downloads" + File.separator + fileName;
        String[] peerNames = {"localhost:5001", "localhost:5002"};
        for (String hostPort : peerNames) {
            String[] parts = hostPort.split(":");
            if (parts.length != 2) {
                System.out.println("Skipping invalid peer address: " + hostPort);
                continue;
            }
            try (Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {
                out.writeUTF(username);
                String peerIdentity = choice.equals("1") ? "peer1" : "peer2";
                out.writeUTF(choice.equals("1") ? "peer1" : "peer2");

                String peerName = (parts[1].equals("5001")) ? "peer1" : "peer2";
                try {
                    PublicKey serverPublicKey = getServerPublicKey(peerName);
                    if (myPrivateKey == null) {
//                        System.out.println("Private key is null for " + peerName);
                        return "Private key is null for " + peerName;
                    }
                    if (!authenticatePeer(in, out, myPrivateKey,serverPublicKey,peerIdentity,username)) {
//                        System.out.println("Authentication failed");
                        return "Authentication failed";
                    }
                }
                catch (Exception e){

//                    System.out.println("Authentication error: " + e.getMessage());
                    return "Authentication error: " + e.getMessage();
                }

                SecretKey aesKey;
                try{
                    aesKey = CryptoUtil.generateAESKEY(out, in);
                    // Update CryptoService state for AES key generation
                    CryptoService.setActiveAESKey(peerIdentity, username,true);
                    // Update CryptoService state for successful ECDSA signature validation
                    CryptoService.setECDHHandshake(peerIdentity, username,true);
                } catch (Exception e) {
//                    System.out.println("Error generating AES key: " + e.getMessage());
                    return "Error generating AES key: " + e.getMessage();
                }

                out.writeUTF("REQUEST_FILE");
                out.writeUTF(fileName);
                out.writeUTF(username);
                String status = in.readUTF();
                if ("FOUND".equals(status)) {
                    File file = new File(downloadsPath);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        long size = in.readLong();
                        int ivLen = in.readInt();
                        byte[] iv = new byte[ivLen];
                        in.readFully(iv);

                        long received = 0;
                        while (received < size) {
                           int encLen = in.readInt();
                           byte[] encData = new byte[encLen];
                           in.readFully(encData);
//                             Tamper: flip a bit in the encrypted data
//                            encData[0] ^= 0x01;
                           try{
                               byte[] plain = CryptoUtil.decryptAES(encData,aesKey,iv);
                               fos.write(plain);
                               received += plain.length;

                           } catch (Exception e) {
                               throw new RuntimeException(e);
                           }
                        }
                    }
//                    System.out.println("File received and saved to Downloads: " + downloadsPath);
                    return "File received and saved to Downloads: " + downloadsPath;
                }
            } catch (IOException e) {
//                System.out.println("Failed to contact peer: " + hostPort + " (" + e.getMessage() + ")");
                return "Failed to contact peer: " + hostPort + " (" + e.getMessage() + ")";
            }
        }
//        System.out.println("File not found in network.");
        return "File not found in network.";
    }

    public static void listKnownPeers() {
        System.out.println("Known peers:");
        for (String peerAddr : knownPeers) {
            System.out.println(peerAddr);
        }
    }
}

