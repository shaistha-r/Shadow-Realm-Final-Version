package com.shadowrealm.backend.dto;

public class GameStatusResponse {

    private String roomCode;
    private String status;
    private String currentPhase;

    public GameStatusResponse() {
    }

    public GameStatusResponse(String roomCode, String status, String currentPhase) {
        this.roomCode = roomCode;
        this.status = status;
        this.currentPhase = currentPhase;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }
}