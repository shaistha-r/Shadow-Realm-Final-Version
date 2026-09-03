package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.dto.InvestigationResponse;
import com.shadowrealm.backend.entity.User;
import com.shadowrealm.backend.repository.UserRepository;
import com.shadowrealm.backend.entity.NightAction;
import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.Role;
import com.shadowrealm.backend.repository.NightActionRepository;
import com.shadowrealm.backend.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NightActionService {
    private final NightActionRepository nightActionRepository;
    private final PlayerRepository playerRepository;
    private final GameService gameService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public NightActionService(NightActionRepository repo, PlayerRepository players, GameService games, AuthenticationService auth, UserRepository users) {
        this.nightActionRepository = repo;
        this.playerRepository = players;
        this.gameService = games;
        this.authenticationService = auth;
        this.userRepository = users;
    }

    public InvestigationResponse seerInvestigate(String roomCode, Long targetId) {
        Player actor = currentActor(roomCode, Role.SEER);
        ensureNight(roomCode);
        Player target = target(targetId, roomCode);
        if (actor.getId().equals(targetId)) throw new IllegalStateException("You cannot investigate yourself");
        ensureAlive(target);
        ensureOneAction(roomCode, actor.getId(), Role.SEER);
        save(roomCode, actor.getId(), targetId, Role.SEER);
        String name = userRepository.findById(target.getUserId()).map(User::getUsername).orElse("Player");
        return new InvestigationResponse(name, target.getRole().name());
    }

    public NightAction traitorAttack(String roomCode, Long targetId) {
        Player actor = currentActor(roomCode, Role.TRAITOR);
        ensureNight(roomCode);
        Player target = target(targetId, roomCode);
        if (actor.getId().equals(targetId)) throw new IllegalStateException("You cannot attack yourself");
        ensureAlive(target);
        ensureOneAction(roomCode, actor.getId(), Role.TRAITOR);
        return save(roomCode, actor.getId(), targetId, Role.TRAITOR);
    }

    public NightAction doctorProtect(String roomCode, Long targetId) {
        Player actor = currentActor(roomCode, Role.DOCTOR);
        ensureNight(roomCode);
        Player target = target(targetId, roomCode);
        ensureAlive(target);
        ensureOneAction(roomCode, actor.getId(), Role.DOCTOR);
        return save(roomCode, actor.getId(), targetId, Role.DOCTOR);
    }

    public Player resolveNight(String roomCode) {
        gameService.requireHost(roomCode);
        ensureNight(roomCode);
        List<NightAction> actions = nightActionRepository.findByRoomCodeAndResolved(roomCode, false);

        boolean traitorActed = actions.stream().anyMatch(a -> a.getActionRole() == Role.TRAITOR);
        if (!traitorActed) throw new IllegalStateException("The Traitor must choose a target before dawn.");

        Player killed = null;
        for (NightAction a : actions) {
            if (a.getActionRole() == Role.TRAITOR) {
                killed = playerRepository.findById(a.getTargetId()).orElse(null);
                break;
            }
        }
        final Long killedId = killed != null ? killed.getId() : null;
        boolean protectedPlayer = killedId != null && actions.stream()
                .anyMatch(a -> a.getActionRole() == Role.DOCTOR && a.getTargetId().equals(killedId));

        if (killed != null && !protectedPlayer) {
            killed.setAlive(false);
            playerRepository.save(killed);
        }

        actions.forEach(a -> a.setResolved(true));
        nightActionRepository.saveAll(actions);

        Game game = gameService.checkWinner(roomCode);
        if (!game.isFinished()) {
            game.setPhase("DAY");
            gameService.updateGame(game);
        }
        return protectedPlayer ? null : killed;
    }

    private Player currentActor(String roomCode, Role role) {
        Player p = authenticationService.getCurrentPlayer(roomCode);
        if (!p.isAlive()) throw new IllegalStateException("Dead players cannot perform night actions");
        if (p.getRole() != role) throw new IllegalStateException("Only the " + role + " can perform this action");
        return p;
    }

    private void ensureNight(String roomCode) {
        Game g = gameService.getGame(roomCode);
        if (!"RUNNING".equals(g.getStatus()) || !"NIGHT".equals(g.getPhase())) throw new IllegalStateException("This action is only available during NIGHT");
    }

    private Player target(Long id, String roomCode) {
        Player p = playerRepository.findById(id).orElseThrow(() -> new IllegalStateException("Target player not found"));
        if (!roomCode.equals(p.getRoomCode())) throw new IllegalStateException("Target is not in this room");
        return p;
    }

    private void ensureAlive(Player p) { if (!p.isAlive()) throw new IllegalStateException("Target is already eliminated"); }

    private void ensureOneAction(String roomCode, Long actorId, Role role) {
        if (nightActionRepository.existsByRoomCodeAndActorIdAndActionRoleAndResolved(roomCode, actorId, role, false))
            throw new IllegalStateException("You have already acted this night");
    }

    private NightAction save(String roomCode, Long actorId, Long targetId, Role role) {
        NightAction a = new NightAction();
        a.setRoomCode(roomCode); a.setActorId(actorId); a.setTargetId(targetId); a.setActionRole(role); a.setResolved(false);
        return nightActionRepository.save(a);
    }
}
