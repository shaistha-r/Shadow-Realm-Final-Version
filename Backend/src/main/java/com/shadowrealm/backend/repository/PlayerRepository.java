package com.shadowrealm.backend.repository;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // =========================================================
    // GET ALL PLAYERS IN A ROOM
    // =========================================================

    List<Player> findByRoomCode(String roomCode);


    // =========================================================
    // GET ALIVE / DEAD PLAYERS IN A ROOM
    // =========================================================

    List<Player> findByRoomCodeAndAlive(
            String roomCode,
            boolean alive
    );


    // =========================================================
    // COUNT PLAYERS IN A ROOM
    // =========================================================

    long countByRoomCode(String roomCode);

    long countByRoomCodeAndAlive(String roomCode, boolean alive);


    // =========================================================
    // COUNT ALIVE PLAYERS WITH A SPECIFIC ROLE
    // =========================================================

    long countByRoomCodeAndAliveAndRole(
            String roomCode,
            boolean alive,
            Role role
    );


    // =========================================================
    // COUNT ALIVE PLAYERS WHO ARE NOT A SPECIFIC ROLE
    // =========================================================

    long countByRoomCodeAndAliveAndRoleNot(
            String roomCode,
            boolean alive,
            Role role
    );


    // =========================================================
    // GET PLAYER BELONGING TO A USER IN A SPECIFIC ROOM
    // =========================================================
    //
    // Used by AuthenticationService:
    //
    // JWT
    //   ↓
    // Current User
    //   ↓
    // User ID
    //   ↓
    // Player in this room
    //
    // =========================================================

    Optional<Player> findByUserIdAndRoomCode(
            Long userId,
            String roomCode
    );
}