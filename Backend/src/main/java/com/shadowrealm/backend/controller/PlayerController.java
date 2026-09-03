package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.dto.PlayerResponse;
import com.shadowrealm.backend.dto.PrivatePlayerResponse;
import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.User;
import com.shadowrealm.backend.repository.UserRepository;
import com.shadowrealm.backend.service.AuthenticationService;
import com.shadowrealm.backend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public PlayerController(PlayerService playerService, AuthenticationService authenticationService, UserRepository userRepository) {
        this.playerService = playerService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<PlayerResponse> addPlayer(@RequestParam String roomCode, @RequestParam boolean host) {
        return ResponseEntity.ok(toPublic(playerService.addCurrentPlayer(roomCode, host)));
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<List<PlayerResponse>> getPlayers(@PathVariable String roomCode) {
        return ResponseEntity.ok(playerService.getPlayers(roomCode).stream().map(this::toPublic).collect(Collectors.toList()));
    }

    @GetMapping("/{roomCode}/alive")
    public ResponseEntity<List<PlayerResponse>> getAlivePlayers(@PathVariable String roomCode) {
        return ResponseEntity.ok(playerService.getAlivePlayers(roomCode).stream().map(this::toPublic).collect(Collectors.toList()));
    }

    @GetMapping("/me/{roomCode}")
    public ResponseEntity<PrivatePlayerResponse> currentPlayer(@PathVariable String roomCode) {
        Player p = authenticationService.getCurrentPlayer(roomCode);
        User u = authenticationService.getCurrentUser();
        return ResponseEntity.ok(new PrivatePlayerResponse(p.getId(), p.getUserId(), u.getUsername(),
                p.getRole() == null ? null : p.getRole().name(), p.isAlive(), p.isHost()));
    }

    private PlayerResponse toPublic(Player p) {
        String displayName = "Player " + p.getUserId();
        return userRepository.findById(p.getUserId())
                .map(u -> new PlayerResponse(p.getId(), p.getUserId(), u.getUsername(), p.isAlive(), p.isHost()))
                .orElse(new PlayerResponse(p.getId(), p.getUserId(), displayName, p.isAlive(), p.isHost()));
    }
}
