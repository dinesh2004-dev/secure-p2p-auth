package com.dinesh.auth.anonauthbackend.controller;

import com.dinesh.auth.anonauthbackend.util.UserUtil;
import org.apache.coyote.Response;
import org.example.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/server/crypto")
public class CryptoStatusController {
    @Autowired
    private UserUtil userUtil;
    @GetMapping("/status/{peerIdentity}")
    public ResponseEntity<Map<String,Object>> getCryptoStatus(@PathVariable int peerIdentity){
        Map<String,Object>  status = new HashMap<>();
        try {
            String peerIdentityStr = (peerIdentity == 1) ? "peer1" : "peer2";
            String username = userUtil.getCurrentUsername();
            boolean keysLoaded = CryptoService.areKeysLoadedForPeer(peerIdentityStr,username);
            boolean ecdhCompleted = CryptoService.hasRecentECDHHandshake(peerIdentityStr,username);
            boolean aesGenerated = CryptoService.hasActiveAESKey(peerIdentityStr,username);
            boolean ecdsaVerified = CryptoService.hasValidECDSASignature(peerIdentityStr,username);

            // Add debug logging
            System.out.println("Crypto Status for user " + username + ", peer " + peerIdentity + ":");
            System.out.println("  keysLoaded: " + keysLoaded);
            System.out.println("  ecdhCompleted: " + ecdhCompleted);
            System.out.println("  aesGenerated: " + aesGenerated);
            System.out.println("  ecdsaVerified: " + ecdsaVerified);

            status.put("keysLoaded", keysLoaded);
            status.put("ecdhCompleted", ecdhCompleted);
            status.put("aesGenerated", aesGenerated);
            status.put("ecdsaVerified", ecdsaVerified);
            status.put("peerIdentity", peerIdentity);
            status.put("username", username);
            status.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.OK).body(status);
        }
        catch (Exception e) {
            status.put("error", "Failed to get crypto status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }

    }
    @PostMapping("/initialize/{peerIdentity}")
    public ResponseEntity<Map<String, Object>> initializeCrypto(@PathVariable int peerIdentity) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Initialize keys for this peer
            String username = userUtil.getCurrentUsername();
            String peerIdentityStr = String.valueOf(peerIdentity);
            boolean success = CryptoService.initializeKeysForPeer(peerIdentityStr,username);

            result.put("success", success);
            result.put("keysLoaded", success);
            result.put("message", success ? "Crypto initialized successfully" : "Failed to initialize crypto");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/clear/{peerIdentity}")
    public ResponseEntity<Map<String, Object>> clearCryptoState(@PathVariable int peerIdentity) {
        Map<String, Object> result = new HashMap<>();
        try {
            String peerIdentityStr = (peerIdentity == 1) ? "peer1" : "peer2";
            String username = userUtil.getCurrentUsername();
            // Clear the crypto state for this peer
            CryptoService.clearPeerState("peer2",username);
            CryptoService.clearPeerState("peer1",username);

            System.out.println("🗑️ Cleared crypto state for peer: " + peerIdentityStr);

            result.put("success", true);
            result.put("message", "Crypto state cleared successfully");
            result.put("peerIdentity", peerIdentity);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Error clearing crypto state: " + e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
