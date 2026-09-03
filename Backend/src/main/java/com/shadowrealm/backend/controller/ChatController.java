package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.dto.ChatRequest;
import com.shadowrealm.backend.dto.ChatResponse;
import com.shadowrealm.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService service;
    public ChatController(ChatService service){this.service=service;}

    @GetMapping("/{roomCode}") public ResponseEntity<List<ChatResponse>> get(@PathVariable String roomCode){
        return ResponseEntity.ok(service.get(roomCode));
    }

    @PostMapping("/{roomCode}") public ResponseEntity<?> send(@PathVariable String roomCode,@Valid @RequestBody ChatRequest request){
        try{return ResponseEntity.ok(service.send(roomCode,request.getMessage()));}
        catch(IllegalStateException e){return ResponseEntity.badRequest().body(e.getMessage());}
    }
}
