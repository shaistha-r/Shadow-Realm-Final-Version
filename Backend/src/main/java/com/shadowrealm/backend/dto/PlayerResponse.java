package com.shadowrealm.backend.dto;

public class PlayerResponse {
    private Long id;
    private Long userId;
    private String displayName;
    private boolean alive;
    private boolean host;

    public PlayerResponse() {}

    public PlayerResponse(Long id, Long userId, String displayName, boolean alive, boolean host) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.alive = alive;
        this.host = host;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public boolean isAlive() { return alive; }
    public boolean isHost() { return host; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setHost(boolean host) { this.host = host; }
}
