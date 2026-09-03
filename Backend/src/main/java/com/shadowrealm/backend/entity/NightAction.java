package com.shadowrealm.backend.entity;

import jakarta.persistence.*;

@Entity
public class NightAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode;

    private Long actorId;

    private Long targetId;

    @Enumerated(EnumType.STRING)
    private Role actionRole;

    private boolean resolved;

    public NightAction() {
        this.resolved = false;
    }


    public Long getId() {
        return id;
    }


    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }


    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }


    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }


    public Role getActionRole() {
        return actionRole;
    }

    public void setActionRole(Role actionRole) {
        this.actionRole = actionRole;
    }


    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}