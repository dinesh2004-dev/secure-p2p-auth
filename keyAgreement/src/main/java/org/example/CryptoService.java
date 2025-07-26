package org.example;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CryptoService {

    private static class PeerCryptoState {
        boolean keysLoaded = false;
        boolean ecdhHandshakeTime = false;
        boolean activeAESKey = false;
        boolean validECDSASignature = false;
    }

    private static final Map<String, PeerCryptoState> peerStates = new ConcurrentHashMap<>();

    public static boolean areKeysLoadedForPeer(String peerIdentity,String username) {
        String key = username + "_" + peerIdentity;
        PeerCryptoState state = peerStates.get(key);
        return state != null && state.keysLoaded;
    }

    public static boolean hasRecentECDHHandshake(String peerIdentity,String username) {
        String key = username + "_" + peerIdentity;
        PeerCryptoState state = peerStates.get(key);
        return state != null && state.ecdhHandshakeTime;
    }

    public static boolean hasActiveAESKey(String peerIdentity,String username) {
        String key = username + "_" + peerIdentity;
        PeerCryptoState state = peerStates.get(key);
        return state != null && state.activeAESKey;
    }

    public static boolean hasValidECDSASignature(String peerIdentity,String username) {
        String key = username + "_" + peerIdentity;
        PeerCryptoState state = peerStates.get(key);
        return state != null && state.validECDSASignature;
    }
    public static boolean initializeKeysForPeer(String peerIdentity,String username) {
        try {
            // Ensure peer state exists
            String key = username + "_" + peerIdentity;
            PeerCryptoState state = peerStates.computeIfAbsent(key, k -> new PeerCryptoState());

            // Initialize cryptographic components for the peer
            // This could include loading public keys, generating key pairs, etc.

            // For now, we'll mark keys as loaded
            state.keysLoaded = true;

            // Reset other states for fresh initialization
            state.ecdhHandshakeTime = false;
            state.activeAESKey = false;
            state.validECDSASignature = false;

            return true;
        } catch (Exception e) {
            // Log error and return false on failure
            System.err.println("Failed to initialize keys for peer " + peerIdentity + ": " + e.getMessage());
            return false;
        }
    }

    // Helper methods to update boolean states
    public static void setKeysLoaded(String peerIdentity,String username, boolean loaded) {
        String key = username + "_" + peerIdentity;
        peerStates.computeIfAbsent(key, k -> new PeerCryptoState()).keysLoaded = loaded;
    }

    public static void setECDHHandshake(String peerIdentity,String username, boolean handshake) {
        String key = username + "_" + peerIdentity;
        peerStates.computeIfAbsent(key, k -> new PeerCryptoState()).ecdhHandshakeTime = handshake;
    }

    public static void setActiveAESKey(String peerIdentity,String username, boolean aesKey) {
        String key = username + "_" + peerIdentity;
        peerStates.computeIfAbsent(key, k -> new PeerCryptoState()).activeAESKey = aesKey;
    }

    public static void setValidECDSASignature(String peerIdentity,String username, boolean validSignature) {
        String key = username + "_" + peerIdentity;
        peerStates.computeIfAbsent(key, k -> new PeerCryptoState()).validECDSASignature = validSignature;
    }

    public static void clearPeerState(String peerIdentity,String username){
        String key = username + "_" + peerIdentity;
        peerStates.remove(key);
    }

    public static Set<String> getAllPeerStates() {
        return peerStates.keySet();
    }
}