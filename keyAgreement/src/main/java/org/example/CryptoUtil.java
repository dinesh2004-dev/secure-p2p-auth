package org.example;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class CryptoUtil {
    public static KeyPair generateECDHKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        return kpg.generateKeyPair();
    }

    public static PublicKey decodePublicKey(byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("EC");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
        return kf.generatePublic(spec);
    }

    public static SecretKey deriveAESKey(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(privateKey);
        ka.doPhase(publicKey,true);
        byte[] sharedSecret = ka.generateSecret();
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] aesKetBytes = sha256.digest(sharedSecret);
        return new SecretKeySpec(aesKetBytes,0,32,"AES");
    }

    public static byte[] encryptAES(byte[] plainText,SecretKey key,byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128,iv);
        cipher.init(Cipher.ENCRYPT_MODE,key,spec);
        return cipher.doFinal(plainText);
    }

    public static byte[] decryptAES(byte[] ciphertext,SecretKey key,byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128,iv);
        cipher.init(Cipher.DECRYPT_MODE,key,spec);
        return cipher.doFinal(ciphertext);
    }

    public static SecretKey generateAESKEY(DataOutputStream out, DataInputStream in) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException {
        // 1. Generate ECDH key pair for this session
        KeyPair keyPair = generateECDHKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        // 2. Send public key to peer
        out.writeInt(publicKey.getEncoded().length);
        out.write(publicKey.getEncoded());
        // 3. Receive peer's public key
        int peerKeyLen = in.readInt();
        byte[] peerKeyBytes = new byte[peerKeyLen];
        in.readFully(peerKeyBytes);
//        Arrays.fill(peerKeyBytes, (byte) 0); //key tampered
        PublicKey peerECDHPub = CryptoUtil.decodePublicKey(peerKeyBytes);
        // 4. Derive shared AES key
       SecretKey aesKey = CryptoUtil.deriveAESKey(keyPair.getPrivate(), peerECDHPub);

       return aesKey;
    }
}
