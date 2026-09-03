package com.shadowrealm.backend.controller;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Vote;
import com.shadowrealm.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    private VoteService voteService;

    /**
     * Cast a vote.
     */
    @PostMapping("/cast")
    public ResponseEntity<?> castVote(
            @RequestBody Vote vote
    ) {

        try {

            Vote savedVote =
                    voteService.castVote(vote);

            return ResponseEntity.ok(savedVote);

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * Get all votes in a room.
     */
    @GetMapping("/{roomCode}")
    public List<Vote> getVotes(
            @PathVariable String roomCode
    ) {

        return voteService.getVotes(roomCode);
    }

    /**
     * Get the player currently receiving
     * the most votes.
     */
    @GetMapping("/{roomCode}/most-voted")
    public ResponseEntity<?> getMostVoted(
            @PathVariable String roomCode
    ) {

        Player player =
                voteService.getMostVotedPlayer(roomCode);

        if (player == null) {
            return ResponseEntity.ok(
                    "No votes have been cast."
            );
        }

        return ResponseEntity.ok(player);
    }

    /**
     * Resolve voting and eliminate
     * the highest-voted player.
     */
    @PostMapping("/{roomCode}/resolve")
    public ResponseEntity<?> resolveVoting(
            @PathVariable String roomCode
    ) {

        try {

            Player eliminated =
                    voteService.resolveVoting(roomCode);

            if (eliminated == null) {
                return ResponseEntity.ok(
                        "No player was eliminated."
                );
            }

            return ResponseEntity.ok(eliminated);

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * Clear all votes.
     */
    @DeleteMapping("/clear")
    public String clearVotes() {

        voteService.clearVotes();

        return "All votes cleared successfully.";
    }
}