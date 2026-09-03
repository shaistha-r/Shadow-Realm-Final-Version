package com.example.loginapp;

public class VoteRequest {
    private final String roomCode; private final Long voterId; private final Long votedPlayerId;
    public VoteRequest(String roomCode, Long voterId, Long votedPlayerId){this.roomCode=roomCode;this.voterId=voterId;this.votedPlayerId=votedPlayerId;}
    public String getRoomCode(){return roomCode;} public Long getVoterId(){return voterId;} public Long getVotedPlayerId(){return votedPlayerId;}
}
