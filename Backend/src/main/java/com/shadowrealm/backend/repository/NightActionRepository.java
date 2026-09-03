package com.shadowrealm.backend.repository;

import com.shadowrealm.backend.entity.NightAction;
import com.shadowrealm.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NightActionRepository
        extends JpaRepository<NightAction, Long> {

    // Get all night actions in a room
    List<NightAction> findByRoomCode(String roomCode);

    // Find an action by room and role
    Optional<NightAction> findByRoomCodeAndActionRole(
            String roomCode,
            Role actionRole
    );

    // Check whether a player has already performed an action
    boolean existsByRoomCodeAndActorIdAndActionRole(
            String roomCode,
            Long actorId,
            Role actionRole
    );

    // Check whether a player has already performed
    // an unresolved action during the current night
    boolean existsByRoomCodeAndActorIdAndActionRoleAndResolved(
            String roomCode,
            Long actorId,
            Role actionRole,
            boolean resolved
    );

    // Get all unresolved night actions for a room
    List<NightAction> findByRoomCodeAndResolved(
            String roomCode,
            boolean resolved
    );
}