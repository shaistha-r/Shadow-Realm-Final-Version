package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Room;
import com.shadowrealm.backend.repository.PlayerRepository;
import com.shadowrealm.backend.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AuthenticationService authenticationService;


    // =========================================================
    // CREATE ROOM
    // =========================================================

    public Room createRoom(int maxPlayers) {

        // Get currently logged-in user
        Long currentUserId =
                authenticationService
                        .getCurrentUser()
                        .getId();

        // Create room
        Room room = new Room();

        room.setHostId(currentUserId);
        room.setMaxPlayers(maxPlayers);
        room.setCurrentPlayers(1);
        room.setStatus("WAITING");
        room.setRoomCode(generateUniqueRoomCode());

        Room savedRoom =
                roomRepository.save(room);


        // =====================================================
        // AUTOMATICALLY ADD HOST AS PLAYER
        // =====================================================

        Player player = new Player();

        player.setUserId(currentUserId);
        player.setRoomCode(savedRoom.getRoomCode());
        player.setHost(true);
        player.setAlive(true);

        playerRepository.save(player);


        return savedRoom;
    }


    // =========================================================
    // JOIN ROOM
    // =========================================================

    public Room joinRoom(String roomCode) {

        // Find room
        Room room =
                roomRepository.findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );


        // Room must still be waiting
        if (!"WAITING".equals(room.getStatus())) {

            throw new IllegalStateException(
                    "Game has already started"
            );
        }


        // Get logged-in user
        Long currentUserId =
                authenticationService
                        .getCurrentUser()
                        .getId();


        // Check whether user is already in room
        boolean alreadyPlayer =
                playerRepository
                        .findByUserIdAndRoomCode(
                                currentUserId,
                                roomCode
                        )
                        .isPresent();

        if (alreadyPlayer) {

            throw new IllegalStateException(
                    "User is already a player in this room"
            );
        }


        // Check room capacity
        if (room.getCurrentPlayers()
                >= room.getMaxPlayers()) {

            throw new IllegalStateException(
                    "Room is full"
            );
        }


        // =====================================================
        // ADD USER AS PLAYER
        // =====================================================

        Player player = new Player();

        player.setUserId(currentUserId);
        player.setRoomCode(roomCode);
        player.setHost(false);
        player.setAlive(true);

        playerRepository.save(player);


        // Increase player count
        room.setCurrentPlayers(
                room.getCurrentPlayers() + 1
        );


        return roomRepository.save(room);
    }


    // =========================================================
    // LEAVE ROOM
    // =========================================================

    public Room leaveRoom(String roomCode) {

        Room room =
                roomRepository.findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );


        Long currentUserId =
                authenticationService
                        .getCurrentUser()
                        .getId();


        Player player =
                playerRepository
                        .findByUserIdAndRoomCode(
                                currentUserId,
                                roomCode
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "You are not a player in this room"
                                )
                        );


        // Remove player
        playerRepository.delete(player);


        // Decrease player count
        if (room.getCurrentPlayers() > 0) {

            room.setCurrentPlayers(
                    room.getCurrentPlayers() - 1
            );
        }


        return roomRepository.save(room);
    }


    // =========================================================
    // DELETE ROOM
    // =========================================================

    public void deleteRoom(String roomCode) {

        Room room =
                roomRepository.findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );

        Long currentUserId = authenticationService.getCurrentUser().getId();
        if (!currentUserId.equals(room.getHostId())) {
            throw new IllegalStateException("Only the room host can delete the room");
        }
        playerRepository.deleteAll(playerRepository.findByRoomCode(roomCode));
        roomRepository.delete(room);
    }


    // =========================================================
    // GENERATE UNIQUE ROOM CODE
    // =========================================================

    private String generateUniqueRoomCode() {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();

        String code;

        do {

            StringBuilder builder =
                    new StringBuilder();

            for (int i = 0; i < 6; i++) {

                builder.append(
                        characters.charAt(
                                random.nextInt(
                                        characters.length()
                                )
                        )
                );
            }

            code = builder.toString();

        } while (
                roomRepository.existsByRoomCode(code)
        );

        return code;
    }
}