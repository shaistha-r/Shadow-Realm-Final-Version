package com.shadowrealm.backend.repository;

import com.shadowrealm.backend.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository
        extends JpaRepository<Vote, Long> {


    List<Vote> findByRoomCode(String roomCode);


    List<Vote> findByVotedPlayerId(Long votedPlayerId);


    List<Vote> findByRoomCodeAndVotedPlayerId(
            String roomCode,
            Long votedPlayerId
    );


    // Check if player has already voted
    // in the CURRENT voting round
    boolean existsByRoomCodeAndVoterIdAndCounted(
            String roomCode,
            Long voterId,
            boolean counted
    );
}