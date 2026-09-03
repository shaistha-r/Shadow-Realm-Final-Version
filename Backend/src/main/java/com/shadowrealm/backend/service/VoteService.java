package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Vote;
import com.shadowrealm.backend.repository.PlayerRepository;
import com.shadowrealm.backend.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final PlayerRepository playerRepository;
    private final GameService gameService;
    private final AuthenticationService authenticationService;

    public VoteService(VoteRepository votes, PlayerRepository players, GameService games, AuthenticationService auth) {
        this.voteRepository = votes; this.playerRepository = players; this.gameService = games; this.authenticationService = auth;
    }

    public Vote castVote(Vote vote) {
        Game game = gameService.getGame(vote.getRoomCode());
        if (!"RUNNING".equals(game.getStatus()) || !"VOTING".equals(game.getPhase()))
            throw new IllegalStateException("Voting is not active");

        Player voter = authenticationService.getCurrentPlayer(vote.getRoomCode());
        if (!voter.isAlive()) throw new IllegalStateException("Dead players cannot vote");
        Player target = playerRepository.findById(vote.getVotedPlayerId()).orElseThrow(() -> new IllegalStateException("Target not found"));
        if (!vote.getRoomCode().equals(target.getRoomCode()) || !target.isAlive()) throw new IllegalStateException("Invalid vote target");
        if (voter.getId().equals(target.getId())) throw new IllegalStateException("You cannot vote for yourself");

        if (voteRepository.existsByRoomCodeAndVoterIdAndCounted(vote.getRoomCode(), voter.getId(), false))
            throw new IllegalStateException("You have already voted this round");

        vote.setVoterId(voter.getId());
        vote.setCounted(false);
        return voteRepository.save(vote);
    }

    public List<Vote> getVotes(String roomCode) { return voteRepository.findByRoomCode(roomCode); }

    public Player getMostVotedPlayer(String roomCode) {
        List<Vote> votes = voteRepository.findByRoomCode(roomCode).stream().filter(v -> !v.isCounted()).collect(Collectors.toList());
        if (votes.isEmpty()) return null;
        Long id = votes.stream().collect(Collectors.groupingBy(Vote::getVotedPlayerId, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        return id == null ? null : playerRepository.findById(id).orElse(null);
    }

    public Player resolveVoting(String roomCode) {
        gameService.requireHost(roomCode);
        Game game = gameService.getGame(roomCode);
        if (!"RUNNING".equals(game.getStatus()) || !"VOTING".equals(game.getPhase()))
            throw new IllegalStateException("Voting is not active");

        long alive = playerRepository.countByRoomCodeAndAlive(roomCode, true);
        List<Vote> votes = voteRepository.findByRoomCode(roomCode).stream().filter(v -> !v.isCounted()).collect(Collectors.toList());
        if (votes.size() < alive) throw new IllegalStateException("Waiting for all living players to vote (" + votes.size() + "/" + alive + ")");

        Player eliminated = getMostVotedPlayer(roomCode);
        if (eliminated != null) { eliminated.setAlive(false); playerRepository.save(eliminated); }
        votes.forEach(v -> v.setCounted(true)); voteRepository.saveAll(votes);

        Game updated = gameService.checkWinner(roomCode);
        if (!updated.isFinished()) {
            gameService.beginNextRound(updated);
            gameService.updateGame(updated);
        }
        return eliminated;
    }

    public void clearVotes() { voteRepository.deleteAll(); }
}
