package com.shadowrealm.backend.dto;
public class InvestigationResponse {
    private String playerName;
    private String role;
    public InvestigationResponse() {}
    public InvestigationResponse(String playerName, String role) { this.playerName=playerName; this.role=role; }
    public String getPlayerName(){return playerName;}
    public String getRole(){return role;}
}
