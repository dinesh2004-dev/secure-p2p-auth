package com.dinesh.auth.anonauthbackend.entity;

import com.dinesh.auth.anonauthbackend.enums.PeerType;
import jakarta.persistence.*;;


@Entity(name = "Server")
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private PeerType peerType;
    @Column(name = "file_name", nullable = false,unique = true)
    private String fileName;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public int getId() {
        return id;
    }

    public PeerType getPeerType() {
        return peerType;
    }
    public void setPeerType(PeerType peerType) {
        this.peerType = peerType;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
