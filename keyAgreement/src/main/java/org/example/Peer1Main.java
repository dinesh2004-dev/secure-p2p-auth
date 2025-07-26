package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class Peer1Main {
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        String myPrivateKeyBase64 = new String(
                Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer1_private.key"))
        );
        String peer2PublicKeyBase64 = new String(
                Files.readAllBytes(Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys/peer2_public.key"))
        );
        PrivateKey myPrivateKey = ECDSAUtil.loadPrivateKey(myPrivateKeyBase64);
        PublicKey peer2PublicKey = ECDSAUtil.loadPublicKey(peer2PublicKeyBase64);
        int port = 5001;
        File sharedDir = new File("peer1");
        if (!sharedDir.exists()) sharedDir.mkdirs();
        Peer peer = new Peer(port, sharedDir,myPrivateKey, peer2PublicKey);
        peer.startServer();
        System.out.println("Peer1 started on port 5001 with folder 'peer1'.");


        // Keep running
        while (true) {

            try { Thread.sleep(10000); } catch (InterruptedException e) { break; }
        }
    }
}

