package com.example.loginapp;
public class PrivatePlayerResponse {
    private Long id, userId; private String username, role; private boolean alive, host;
    public Long getId(){return id;} public Long getUserId(){return userId;} public String getUsername(){return username;}
    public String getRole(){return role;} public boolean isAlive(){return alive;} public boolean isHost(){return host;}
}
