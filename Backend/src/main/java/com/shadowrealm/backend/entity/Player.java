package com.shadowrealm.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_code")
    private String roomCode;

    /*
     * Store the role as normal VARCHAR text instead of relying
     * on an old MySQL ENUM definition.
     *
     * This prevents the existing database ENUM from causing
     * "Data truncated for column 'role'" errors.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean alive;

    @Column(nullable = false)
    private boolean host;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Player() {
        this.alive = true;
        this.host = false;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isHost() {
        return host;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setHost(boolean host) {
        this.host = host;
    }
}