package com.shadowrealm.backend.dto;

public class VoteRequest {

    private String roomCode;
    private Long voterPlayerId;
    private Long votedPlayerId;

    public VoteRequest() {
    }

    public VoteRequest(String roomCode, Long voterPlayerId, Long votedPlayerId) {
        this.roomCode = roomCode;
        this.voterPlayerId = voterPlayerId;
        this.votedPlayerId = votedPlayerId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Long getVoterPlayerId() {
        return voterPlayerId;
    }

    public void setVoterPlayerId(Long voterPlayerId) {
        this.voterPlayerId = voterPlayerId;
    }

    public Long getVotedPlayerId() {
        return votedPlayerId;
    }

    public void setVotedPlayerId(Long votedPlayerId) {
        this.votedPlayerId = votedPlayerId;
    }
}