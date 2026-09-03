package com.shadowrealm.backend.dto;
import java.time.LocalDateTime;
public class ChatResponse {
    private Long id; private String username,message; private LocalDateTime createdAt;
    public ChatResponse(){}
    public ChatResponse(Long id,String username,String message,LocalDateTime createdAt){this.id=id;this.username=username;this.message=message;this.createdAt=createdAt;}
    public Long getId(){return id;} public String getUsername(){return username;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;}
}
