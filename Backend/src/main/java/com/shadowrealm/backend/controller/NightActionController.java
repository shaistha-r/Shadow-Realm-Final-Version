package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.service.NightActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/night")
public class NightActionController {
    private final NightActionService service;
    public NightActionController(NightActionService service) { this.service = service; }

    @PostMapping("/attack")
    public ResponseEntity<?> attack(@RequestParam String roomCode, @RequestParam Long targetId) {
        try { return ResponseEntity.ok(service.traitorAttack(roomCode, targetId)); }
        catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/protect")
    public ResponseEntity<?> protect(@RequestParam String roomCode, @RequestParam Long targetId) {
        try { return ResponseEntity.ok(service.doctorProtect(roomCode, targetId)); }
        catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/investigate")
    public ResponseEntity<?> investigate(@RequestParam String roomCode, @RequestParam Long targetId) {
        try { return ResponseEntity.ok(service.seerInvestigate(roomCode, targetId)); }
        catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/resolve")
    public ResponseEntity<?> resolve(@RequestParam String roomCode) {
        try { return ResponseEntity.ok(service.resolveNight(roomCode)); }
        catch (IllegalStateException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}
