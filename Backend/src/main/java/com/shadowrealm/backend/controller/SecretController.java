package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.dto.SecretResponse;
import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Role;
import com.shadowrealm.backend.service.AuthenticationService;
import com.shadowrealm.backend.service.GameService;
import com.shadowrealm.backend.service.SecretCryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class SecretController {
    private final GameService gameService;
    private final AuthenticationService authenticationService;
    private final SecretCryptoService crypto;

    public SecretController(GameService gameService, AuthenticationService authenticationService, SecretCryptoService crypto) {
        this.gameService = gameService;
        this.authenticationService = authenticationService;
        this.crypto = crypto;
    }

    @GetMapping("/{roomCode}/secret")
    public ResponseEntity<SecretResponse> mySecret(@PathVariable String roomCode) {
        Game game = gameService.getGame(roomCode);
        if (!game.isStarted() || game.isFinished()) throw new IllegalStateException("Secret is not available");
        Player player = authenticationService.getCurrentPlayer(roomCode);
        if (player.getRole() == null) throw new IllegalStateException("Role has not been assigned");
        boolean traitor = player.getRole() == Role.TRAITOR;
        String encrypted = traitor ? game.getSecretHint() : game.getSecretWord();
        return ResponseEntity.ok(new SecretResponse(traitor ? "HINT" : "WORD",
                crypto.decrypt(encrypted), game.getCurrentRound()));
    }
}
