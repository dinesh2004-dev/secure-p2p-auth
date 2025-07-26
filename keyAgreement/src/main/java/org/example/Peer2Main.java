package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class Peer2Main {
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String myPrivateKeyBase64 = new String(
                Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_private.key"))
        );
        String peer1PublicKeyBase64 = new String(
                Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_public.key"))
        );
        PrivateKey myPrivateKey = ECDSAUtil.loadPrivateKey(myPrivateKeyBase64);
        PublicKey peer1PublicKey = ECDSAUtil.loadPublicKey(peer1PublicKeyBase64);
        int port = 5002;
        File sharedDir = new File("peer2");
        if (!sharedDir.exists()) sharedDir.mkdirs();
        Peer peer = new Peer(port, sharedDir, myPrivateKey, peer1PublicKey);
        peer.startServer();
        System.out.println("Peer2 started on port 5002 with folder 'peer2'.");

        // Keep running
        while (true) {
            try { Thread.sleep(10000); } catch (InterruptedException e) { break; }
        }
    }
}

