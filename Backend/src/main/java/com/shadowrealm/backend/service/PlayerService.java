package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Role;
import com.shadowrealm.backend.repository.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AuthenticationService authenticationService;


    // =========================================================
    // ADD CURRENT LOGGED-IN USER AS PLAYER
    // =========================================================

    public Player addCurrentPlayer(
            String roomCode,
            boolean host
    ) {

        // Get the currently logged-in user from the JWT
        var currentUser =
                authenticationService.getCurrentUser();

        Long userId = currentUser.getId();


        // -----------------------------------------------------
        // Prevent the same user from joining the same room twice
        // -----------------------------------------------------

        if (playerRepository
                .findByUserIdAndRoomCode(
                        userId,
                        roomCode
                )
                .isPresent()) {

            throw new IllegalStateException(
                    "User is already a player in this room"
            );
        }


        // -----------------------------------------------------
        // Create player
        // -----------------------------------------------------

        Player player = new Player();

        player.setUserId(userId);
        player.setRoomCode(roomCode);
        player.setHost(host);
        player.setAlive(true);
        player.setRole(null);


        return playerRepository.save(player);
    }


    // =========================================================
    // GET ALL PLAYERS IN ROOM
    // =========================================================

    public List<Player> getPlayers(
            String roomCode
    ) {

        return playerRepository
                .findByRoomCode(roomCode);
    }


    // =========================================================
    // GET ALL ALIVE PLAYERS
    // =========================================================

    public List<Player> getAlivePlayers(
            String roomCode
    ) {

        return playerRepository
                .findByRoomCodeAndAlive(
                        roomCode,
                        true
                );
    }


    // =========================================================
    // GET PLAYER BY ID
    // =========================================================

    public Player getPlayer(
            Long id
    ) {

        return playerRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Player not found"
                        )
                );
    }


    // =========================================================
    // UPDATE PLAYER
    // =========================================================

    public Player updatePlayer(
            Player player
    ) {

        return playerRepository.save(player);
    }


    // =========================================================
    // DELETE PLAYER
    // =========================================================

    public void deletePlayer(
            Long id
    ) {

        playerRepository.deleteById(id);
    }


    // =========================================================
    // COUNT PLAYERS
    // =========================================================

    public long countPlayers(
            String roomCode
    ) {

        return playerRepository
                .countByRoomCode(roomCode);
    }


    // =========================================================
    // ASSIGN ROLES RANDOMLY
    // =========================================================

    public void assignRoles(
            String roomCode
    ) {

        List<Player> players =
                playerRepository
                        .findByRoomCode(roomCode);


        // -----------------------------------------------------
        // Minimum 4 players required
        // -----------------------------------------------------

        if (players.size() < 4) {

            throw new RuntimeException(
                    "At least 4 players are required to start the game."
            );
        }


        // -----------------------------------------------------
        // Randomize players
        // -----------------------------------------------------

        Collections.shuffle(players);


        // -----------------------------------------------------
        // Assign roles
        // -----------------------------------------------------

        players.get(0)
                .setRole(Role.TRAITOR);

        players.get(1)
                .setRole(Role.SEER);

        players.get(2)
                .setRole(Role.DOCTOR);


        // -----------------------------------------------------
        // Remaining players become villagers
        // -----------------------------------------------------

        for (
                int i = 3;
                i < players.size();
                i++
        ) {

            players.get(i)
                    .setRole(Role.VILLAGER);
        }


        playerRepository.saveAll(players);
    }


    // =========================================================
    // ELIMINATE PLAYER
    // =========================================================

    public Player eliminatePlayer(
            Long id
    ) {

        Player player =
                playerRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Player not found"
                                )
                        );


        // -----------------------------------------------------
        // Prevent eliminating an already dead player
        // -----------------------------------------------------

        if (!player.isAlive()) {

            throw new IllegalStateException(
                    "Player is already eliminated"
            );
        }


        // -----------------------------------------------------
        // Mark player as dead
        // -----------------------------------------------------

        player.setAlive(false);

        return playerRepository.save(player);
    }
}