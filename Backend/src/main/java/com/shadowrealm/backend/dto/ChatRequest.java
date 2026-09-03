package com.shadowrealm.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ChatRequest {
    @NotBlank @Size(max=300) private String message;
    public ChatRequest(){}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
}
