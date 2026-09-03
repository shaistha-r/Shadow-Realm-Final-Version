package com.shadowrealm.backend.dto;

public class StartGameRequest {

    private String roomCode;

    public StartGameRequest() {
    }

    public StartGameRequest(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}