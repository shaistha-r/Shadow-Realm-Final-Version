package com.shadowrealm.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="chat_messages")
public class ChatMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=20) private String roomCode;
    @Column(nullable=false) private Long userId;
    @Column(nullable=false, length=50) private String username;
    @Column(nullable=false, length=300) private String message;
    @Column(nullable=false) private LocalDateTime createdAt = LocalDateTime.now();

    public ChatMessage(){}
    public Long getId(){return id;} public String getRoomCode(){return roomCode;} public Long getUserId(){return userId;}
    public String getUsername(){return username;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;}
    public void setRoomCode(String v){roomCode=v;} public void setUserId(Long v){userId=v;} public void setUsername(String v){username=v;}
    public void setMessage(String v){message=v;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
