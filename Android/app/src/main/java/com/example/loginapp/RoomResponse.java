package com.example.loginapp;

public class RoomResponse {
    private Long id; private String roomCode; private Long hostId; private String status;
    private int maxPlayers; private int currentPlayers;
    public Long getId(){return id;} public String getRoomCode(){return roomCode;} public Long getHostId(){return hostId;}
    public String getStatus(){return status;} public int getMaxPlayers(){return maxPlayers;} public int getCurrentPlayers(){return currentPlayers;}
}
