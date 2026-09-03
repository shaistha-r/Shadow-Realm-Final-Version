package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;


    // =========================================================
    // TEMPORARY TEST
    // =========================================================

    @GetMapping("/test")
    public String testGameController() {
        return "GameController is working";
    }


    // =========================================================
    // START GAME
    // =========================================================



    @PostMapping("/test-post")
public String testPost() {
    return "POST GameController is working";
}

    @PostMapping("/start")
    public ResponseEntity<Game> startGame(
            @RequestParam String roomCode
    ) {

        Game game = gameService.startGame(roomCode);

        return ResponseEntity.ok(game);
    }


    // =========================================================
    // GET GAME
    // =========================================================

    @GetMapping("/{roomCode}")
    public ResponseEntity<Game> getGame(
            @PathVariable String roomCode
    ) {

        Game game = gameService.getGame(roomCode);

        return ResponseEntity.ok(game);
    }


    // =========================================================
    // NEXT PHASE
    // =========================================================

    @PutMapping("/next")
    public ResponseEntity<?> nextPhase(
            @RequestParam String roomCode
    ) {

        try {

            Game game = gameService.nextPhase(roomCode);

            return ResponseEntity.ok(game);

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // END GAME
    // =========================================================

    @PutMapping("/end")
    public ResponseEntity<Game> endGame(
            @RequestParam String roomCode
    ) {

        Game game = gameService.endGame(roomCode);

        return ResponseEntity.ok(game);
    }


    // =========================================================
    // CHECK WINNER
    // =========================================================

    @PutMapping("/winner")
    public ResponseEntity<Game> checkWinner(
            @RequestParam String roomCode
    ) {

        Game game = gameService.checkWinner(roomCode);

        return ResponseEntity.ok(game);
    }
    // =========================================================
// RESOLVE ELIMINATION
// =========================================================

@PutMapping("/resolve-elimination")
public ResponseEntity<?> resolveElimination(
        @RequestParam String roomCode
) {

    try {

        Game game =
                gameService.resolveElimination(roomCode);

        return ResponseEntity.ok(game);

    } catch (IllegalStateException e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
}