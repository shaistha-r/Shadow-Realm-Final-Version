package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.entity.Role;
import com.shadowrealm.backend.entity.Room;
import com.shadowrealm.backend.exception.DuplicateResourceException;
import com.shadowrealm.backend.exception.ResourceNotFoundException;
import com.shadowrealm.backend.repository.GameRepository;
import com.shadowrealm.backend.repository.PlayerRepository;
import com.shadowrealm.backend.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SecretCryptoService secretCryptoService;


    // =========================================================
    // SECRET WORDS
    // =========================================================

    private static final String[][] SECRET_PAIRS = {

            {"CLOCK", "measures passing time"},
            {"CASTLE", "a fortified royal home"},
            {"THUNDER", "a loud sound after lightning"},
            {"OCEAN", "a vast body of salt water"},
            {"CANDLE", "gives light when it burns"},
            {"FOREST", "a place filled with trees"},
            {"MIRROR", "shows your reflection"},
            {"COMPASS", "helps you find direction"},
            {"VOLCANO", "a mountain that can erupt"},
            {"KEY", "opens a lock"},
            {"SHADOW", "follows you in the light"},
            {"CROWN", "worn by a monarch"},
            {"DRAGON", "a legendary fire-breathing creature"},
            {"LIBRARY", "a place full of books"}

    };


    // =========================================================
    // START GAME
    // =========================================================

    @Transactional
    public Game startGame(String roomCode) {

        System.out.println(
                "========== START GAME REQUEST =========="
        );

        System.out.println(
                "Room code: " + roomCode
        );


        // -----------------------------------------------------
        // Find room
        // -----------------------------------------------------

        Room room =
                roomRepository.findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Room not found: " + roomCode
                                )
                        );


        // -----------------------------------------------------
        // Get logged-in user
        // -----------------------------------------------------

        Long currentUserId =
                authenticationService
                        .getCurrentUser()
                        .getId();

        System.out.println(
                "Current user ID: " + currentUserId
        );

        System.out.println(
                "Room host ID: " + room.getHostId()
        );


        // -----------------------------------------------------
        // Verify host using ROOM hostId
        // -----------------------------------------------------

        if (!currentUserId.equals(room.getHostId())) {

            throw new IllegalStateException(
                    "Only the room host can start the game."
            );
        }


        // -----------------------------------------------------
        // Make sure room is still waiting
        // -----------------------------------------------------

        if (!"WAITING".equals(room.getStatus())) {

            throw new IllegalStateException(
                    "This room has already started."
            );
        }


        // -----------------------------------------------------
        // Get players
        // -----------------------------------------------------

        List<com.shadowrealm.backend.entity.Player> players =
                playerRepository.findByRoomCode(roomCode);


        System.out.println(
                "Players found: " + players.size()
        );


        // -----------------------------------------------------
        // Minimum 4 players
        // -----------------------------------------------------

        if (players.size() < 4) {

            throw new IllegalStateException(
                    "At least 4 players are required to start the game."
            );
        }


        // -----------------------------------------------------
        // Prevent duplicate game
        // -----------------------------------------------------

        if (gameRepository.findByRoomCode(roomCode).isPresent()) {

            throw new DuplicateResourceException(
                    "A game already exists for this room."
            );
        }


        // -----------------------------------------------------
        // Assign roles
        // -----------------------------------------------------

        System.out.println(
                "Assigning roles..."
        );

        playerService.assignRoles(roomCode);


        // -----------------------------------------------------
        // Create game
        // -----------------------------------------------------

        Game game = new Game();

        game.setRoomCode(roomCode);
        game.setStarted(true);
        game.setFinished(false);
        game.setStatus("RUNNING");
        game.setPhase("NIGHT");
        game.setWinner(null);
        game.setCurrentRound(1);


        // -----------------------------------------------------
        // Generate encrypted secret
        // -----------------------------------------------------

        System.out.println(
                "Generating encrypted secret..."
        );

        rotateSecret(game);


        // -----------------------------------------------------
        // Update room
        // -----------------------------------------------------

        room.setStatus("RUNNING");

        roomRepository.save(room);


        // -----------------------------------------------------
        // Save game
        // -----------------------------------------------------

        Game savedGame =
                gameRepository.save(game);


        System.out.println(
                "GAME STARTED SUCCESSFULLY: "
                        + savedGame.getRoomCode()
        );

        System.out.println(
                "========================================"
        );


        return savedGame;
    }


    // =========================================================
    // GET GAME
    // =========================================================

    public Game getGame(String roomCode) {

        return gameRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Game not found for room: "
                                        + roomCode
                        )
                );
    }


    // =========================================================
    // NEXT PHASE
    // =========================================================

    public Game nextPhase(String roomCode) {

        requireHost(roomCode);

        Game game = getGame(roomCode);


        if (!game.isStarted()
                || game.isFinished()) {

            throw new IllegalStateException(
                    "Game is not active."
            );
        }


        if (!"RUNNING".equals(game.getStatus())) {

            throw new IllegalStateException(
                    "Game is not running."
            );
        }


        switch (game.getPhase()) {

            case "NIGHT" ->
                    game.setPhase("DAY");

            case "DAY" ->
                    game.setPhase("VOTING");

            case "VOTING" ->
                    game.setPhase("ELIMINATION");

            case "ELIMINATION" ->
                    beginNextRound(game);

            default ->
                    throw new IllegalStateException(
                            "Invalid game phase: "
                                    + game.getPhase()
                    );
        }


        return gameRepository.save(game);
    }


    // =========================================================
    // END GAME
    // =========================================================

    public Game endGame(String roomCode) {

        requireHost(roomCode);

        Game game = getGame(roomCode);

        game.setFinished(true);
        game.setStatus("FINISHED");

        return gameRepository.save(game);
    }


    // =========================================================
    // CHECK WINNER
    // =========================================================

    public Game checkWinner(String roomCode) {

        Game game = getGame(roomCode);


        if (game.isFinished()) {
            return game;
        }


        long traitors =
                playerRepository
                        .countByRoomCodeAndAliveAndRole(
                                roomCode,
                                true,
                                Role.TRAITOR
                        );


        long others =
                playerRepository
                        .countByRoomCodeAndAliveAndRoleNot(
                                roomCode,
                                true,
                                Role.TRAITOR
                        );


        // -----------------------------------------------------
        // Villagers win
        // -----------------------------------------------------

        if (traitors == 0) {

            game.setWinner(Role.VILLAGER);
            game.setFinished(true);
            game.setStatus("FINISHED");
        }


        // -----------------------------------------------------
        // Traitors win
        // -----------------------------------------------------

        else if (traitors >= others) {

            game.setWinner(Role.TRAITOR);
            game.setFinished(true);
            game.setStatus("FINISHED");
        }


        return gameRepository.save(game);
    }


    // =========================================================
    // UPDATE GAME
    // =========================================================

    public Game updateGame(Game game) {

        return gameRepository.save(game);
    }


    // =========================================================
    // RESOLVE ELIMINATION
    // =========================================================

    public Game resolveElimination(String roomCode) {

        requireHost(roomCode);

        Game game = getGame(roomCode);


        if (!"RUNNING".equals(game.getStatus())) {

            throw new IllegalStateException(
                    "Game is not running."
            );
        }


        if (!"ELIMINATION".equals(game.getPhase())) {

            throw new IllegalStateException(
                    "Elimination is not active."
            );
        }


        game = checkWinner(roomCode);


        if (!game.isFinished()) {

            beginNextRound(game);

            game = gameRepository.save(game);
        }


        return game;
    }


    // =========================================================
    // BEGIN NEXT ROUND
    // =========================================================

    public Game beginNextRound(Game game) {

        game.setCurrentRound(
                game.getCurrentRound() + 1
        );

        rotateSecret(game);

        game.setPhase("NIGHT");

        return game;
    }


    // =========================================================
    // REQUIRE HOST
    // =========================================================

    public void requireHost(String roomCode) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Room not found: "
                                                + roomCode
                                )
                        );


        Long currentUserId =
                authenticationService
                        .getCurrentUser()
                        .getId();


        if (!currentUserId.equals(room.getHostId())) {

            throw new IllegalStateException(
                    "Only the room host can perform this action."
            );
        }
    }


    // =========================================================
    // ROTATE SECRET
    // =========================================================

    private void rotateSecret(Game game) {

        int index =
                ThreadLocalRandom.current()
                        .nextInt(
                                SECRET_PAIRS.length
                        );


        String word =
                SECRET_PAIRS[index][0];

        String hint =
                SECRET_PAIRS[index][1];


        game.setSecretWord(
                secretCryptoService.encrypt(word)
        );

        game.setSecretHint(
                secretCryptoService.encrypt(hint)
        );
    }
}