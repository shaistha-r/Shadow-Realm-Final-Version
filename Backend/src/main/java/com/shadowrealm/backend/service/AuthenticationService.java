package com.shadowrealm.backend.service;

import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.entity.User;
import com.shadowrealm.backend.exception.ResourceNotFoundException;
import com.shadowrealm.backend.repository.PlayerRepository;
import com.shadowrealm.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;


    public AuthenticationService(
            UserRepository userRepository,
            PlayerRepository playerRepository
    ) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }


    // =========================================================
    // GET CURRENT LOGGED-IN USER
    // =========================================================

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                        authentication.getPrincipal()
                )) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }


    // =========================================================
    // GET CURRENT LOGGED-IN PLAYER IN A ROOM
    // =========================================================

    public Player getCurrentPlayer(String roomCode) {

        User currentUser = getCurrentUser();

        return playerRepository
                .findByUserIdAndRoomCode(
                        currentUser.getId(),
                        roomCode
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user is not a player in this room"
                        )
                );
    }
}