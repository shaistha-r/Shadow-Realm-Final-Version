package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.entity.Room;
import com.shadowrealm.backend.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;


    // =========================================================
    // CREATE ROOM
    // =========================================================

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(
            @RequestParam int maxPlayers
    ) {

        try {

            Room room =
                    roomService.createRoom(maxPlayers);

            return ResponseEntity.ok(room);

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // JOIN ROOM
    // =========================================================

    @PostMapping("/join")
    public Room joinRoom(
            @RequestParam String roomCode
    ) {

        return roomService.joinRoom(roomCode);
    }


    // =========================================================
    // LEAVE ROOM
    // =========================================================

    @PostMapping("/leave")
    public Room leaveRoom(
            @RequestParam String roomCode
    ) {

        return roomService.leaveRoom(roomCode);
    }


    // =========================================================
    // DELETE ROOM
    // =========================================================

    @DeleteMapping("/delete")
    public String deleteRoom(
            @RequestParam String roomCode
    ) {

        roomService.deleteRoom(roomCode);

        return "Room deleted successfully";
    }
}