package com.example.loginapp;
public class PlayerResponse {
    private Long id, userId; private String displayName, role; private boolean alive, host;
    public Long getId(){return id;} public Long getUserId(){return userId;} public String getDisplayName(){return displayName;}
    public String getRole(){return role;} public boolean isAlive(){return alive;} public boolean isHost(){return host;}
}
