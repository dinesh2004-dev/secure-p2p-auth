package org.example;

import java.nio.file.*;
import java.security.KeyPair;
import java.util.Base64;

public class KeyGen {
    public static void main(String[] args) throws Exception {
        // Absolute path for clarity (adjust this if needed)
        Path keysDir = Paths.get("D:/major-project-backup/keyAgreementJava/keyAgreement/src/main/resources/org/example/keys");

        // Create directory if it doesn't exist
        if (!Files.exists(keysDir)) {
            Files.createDirectories(keysDir);
        }

        System.out.println(" Saving keys to: " + keysDir.toAbsolutePath());
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        // --- Peer 1 Keys ---
        KeyPair kp1 = ECDSAUtil.generateKeyPair();
        String priv1 = Base64.getEncoder().encodeToString(kp1.getPrivate().getEncoded());
        String pub1 = Base64.getEncoder().encodeToString(kp1.getPublic().getEncoded());

        Files.write(keysDir.resolve("peer1_private.key"), priv1.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(keysDir.resolve("peer1_public.key"), pub1.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Peer1 keys written.");

        // --- Peer 2 Keys ---
        KeyPair kp2 = ECDSAUtil.generateKeyPair();
        String priv2 = Base64.getEncoder().encodeToString(kp2.getPrivate().getEncoded());
        String pub2 = Base64.getEncoder().encodeToString(kp2.getPublic().getEncoded());

        Files.write(keysDir.resolve("peer2_private.key"), priv2.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(keysDir.resolve("peer2_public.key"), pub2.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Peer2 keys written.");
        System.out.println("Keys generated and saved successfully.");
    }
}
