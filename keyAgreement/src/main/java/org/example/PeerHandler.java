package org.example;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class PeerHandler implements Runnable {
    private Socket socket;
    private Peer peer;
    private final PrivateKey myPrivateKey;
    private final PublicKey otherPeerPublicKey;


    public PeerHandler(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
        this.myPrivateKey = peer.getMyPrivateKey();
        this.otherPeerPublicKey = peer.getOtherPeerPublicKey();
    }

    @Override
    public void run() {
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {

            String username = in.readUTF();
            String peerIdentity = in.readUTF();

            PublicKey clientPublicKey;
            try {
                String publicKeyBase64 = new String(
                        java.nio.file.Files.readAllBytes(
                                java.nio.file.Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/" + peerIdentity + "_public.key")
                        )
                );


                clientPublicKey = ECDSAUtil.loadPublicKey(publicKeyBase64);
                // Update CryptoService state for key loading
                CryptoService.setKeysLoaded(peerIdentity,username, true);
            } catch (Exception e) {
                System.out.println("Failed to load public key for peer: " + peerIdentity);
                out.writeUTF("AUTH_FAILED");
                out.flush();
                socket.close();
                return;
            }

            SecureRandom secureRandom = new SecureRandom();
            // 1.Generate and Send challenge
            byte[] challenge = new byte[32]; // 32 bytes for challenge
            secureRandom.nextBytes(challenge);
            out.writeInt(challenge.length);
            out.flush();
            out.write(challenge);
            out.flush();

            // 2.Receive signature
            int signLen = in.readInt();
            byte[] sigBytes = new byte[signLen];
            in.readFully(sigBytes);
            String signature = new String(sigBytes,java.nio.charset.StandardCharsets.UTF_8);
//            signature += "signature tampered"; // For debugging purposes, you can remove this line in production
            // 3.Verify signature
            try {
                if (!ECDSAUtil.verify(challenge, signature,clientPublicKey)) {
                    out.writeUTF("AUTH_FAILED");
                    socket.close();
                    return;
                }
                // Update CryptoService state for successful ECDSA signature validation
                CryptoService.setValidECDSASignature(peerIdentity,username, true);
            }
            catch(Exception e){
                System.out.println("Signature verification failed: " + e.getMessage());
                socket.close();
                return;
            }
            out.writeUTF("AUTH_SUCCESS");

            // 1.Receive client's challenge
            int clientChallengeLen = in.readInt();
            byte[] clientChallenge = new byte[clientChallengeLen];
            in.readFully(clientChallenge);

            // 2.Sign the challenge with my private key
            String serverSignature = ECDSAUtil.sign(clientChallenge, myPrivateKey);
            byte[] serverSigBytes = serverSignature.getBytes(StandardCharsets.UTF_8);
            out.writeInt(serverSigBytes.length);
            out.write(serverSigBytes);

            String authResult = in.readUTF();
            System.out.println("server Authentication result: " + authResult);
            if (!authResult.equals("AUTH_SUCCESS")) {
                System.out.println("Authentication failed for peer: " + peerIdentity);
                socket.close();
                return;
            }

            // Update CryptoService state for successful ECDSA signature validation
            CryptoService.setValidECDSASignature(peerIdentity,username, true);



            SecretKey aesKey;
            try{
                aesKey = CryptoUtil.generateAESKEY(out, in);
                // Update CryptoService state for AES key generation
                CryptoService.setActiveAESKey(peerIdentity,username, true);
                // Update ECDH handshake state
                CryptoService.setECDHHandshake(peerIdentity,username, true);
            } catch (Exception e) {
                System.out.println("Error generating AES key: " + e.getMessage());
                socket.close();
                return;
            }

            String command = in.readUTF();
            if (command.equals("REQUEST_FILE")) {
                String filename = in.readUTF();
                String requestUsername = in.readUTF();
                File file = peer.getFile(filename,requestUsername);
                if (file != null) {
                    out.writeUTF("FOUND");
                    sendFile(file, out,aesKey);
                } else {
                    out.writeUTF("NOT_FOUND");
                }
            } else if (command.equals("SEND_FILE")) {
                String filename = in.readUTF();
                String requestUsername = in.readUTF();
                File userDir = new File(peer.getSharedDir().getName()+"/"+requestUsername);
                File dir = peer.getSharedDir();
                if (!dir.exists()) dir.mkdirs(); // Ensure directory exists
                if(!userDir.exists()){
                    userDir.mkdir();
                }
                receiveFile(filename, in, userDir,aesKey,username);
                out.writeUTF("RECEIVED");
                out.flush(); // Ensure response is sent immediately
                System.out.println("File received and stored: " + filename + " in " + dir.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(File file, DataOutputStream out,SecretKey aesKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            out.writeLong(file.length());
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12]; // GCM standard IV size
            secureRandom.nextBytes(iv);
            out.writeInt(iv.length);
            out.write(iv);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                try{
                    byte[] encrypted = CryptoUtil.encryptAES(
                            Arrays.copyOf(buffer,read),aesKey,iv
                    );
                    out.writeInt(encrypted.length);
                    out.write(encrypted);
                } catch (Exception e) {
                    System.out.println("Error encrypting file chunk: " + e.getMessage());
                    return;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void receiveFile(String filename, DataInputStream in, File dir,SecretKey aesKey,String username) throws IOException {
        File file = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            long size = in.readLong();
            int ivLength = in.readInt();
            byte[] iv = new byte[ivLength];
            in.readFully(iv);

            long received = 0;
            while (received < size) {
               int encLen = in.readInt();
               byte[] encData = new byte[encLen];
               in.readFully(encData);
               try{
                   byte[] plain = CryptoUtil.decryptAES(encData,aesKey,iv);
                   fos.write(plain);
                   received += plain.length;
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
            }
            String filePath = file.getAbsolutePath();

            String peerFolder = filePath.contains("peer1") ? "peer1" : "peer2";

           AzureBlob.uploadFile(filePath,peerFolder,username);

        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}

