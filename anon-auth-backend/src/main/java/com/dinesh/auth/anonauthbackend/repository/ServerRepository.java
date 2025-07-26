package com.dinesh.auth.anonauthbackend.repository;

import com.dinesh.auth.anonauthbackend.entity.Server;
import com.dinesh.auth.anonauthbackend.enums.PeerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer> {
    Optional<Server> findByFileNameAndPeerType(String fileName, PeerType peerType);
//    @Query("SELECT s.fileName FROM Server s WHERE s.user.username = :username AND s.peerType = :peerType")
    @Query("SELECT s.fileName FROM Server s JOIN  s.user u WHERE u.username = :username AND s.peerType = :peerType")
    List<String> findFileNameByUsernameAndPeerType(@Param("username") String username,
                                                   @Param("peerType") PeerType peerType);

}
