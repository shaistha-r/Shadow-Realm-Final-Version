package com.shadowrealm.backend.entity;

import jakarta.persistence.*;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode;

    private Long voterId;

    private Long votedPlayerId;

    private boolean counted;

    // Constructor
    public Vote() {
        this.counted = false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Long getVotedPlayerId() {
        return votedPlayerId;
    }

    public void setVotedPlayerId(Long votedPlayerId) {
        this.votedPlayerId = votedPlayerId;
    }

    public boolean isCounted() {
        return counted;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }
}