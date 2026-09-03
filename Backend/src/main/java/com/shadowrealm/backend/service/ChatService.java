package com.shadowrealm.backend.service;

import com.shadowrealm.backend.dto.ChatResponse;
import com.shadowrealm.backend.entity.ChatMessage;
import com.shadowrealm.backend.entity.Game;
import com.shadowrealm.backend.entity.Player;
import com.shadowrealm.backend.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatMessageRepository repo;
    private final AuthenticationService auth;
    private final GameService games;
    private final SecretCryptoService crypto;

    public ChatService(ChatMessageRepository repo, AuthenticationService auth, GameService games, SecretCryptoService crypto){
        this.repo=repo;this.auth=auth;this.games=games;this.crypto=crypto;
    }

    public List<ChatResponse> get(String roomCode){
        auth.getCurrentPlayer(roomCode);
        return repo.findTop100ByRoomCodeOrderByCreatedAtAsc(roomCode).stream()
                .map(m->new ChatResponse(m.getId(),m.getUsername(),m.getMessage(),m.getCreatedAt())).collect(Collectors.toList());
    }

    public ChatResponse send(String roomCode,String message){
        Player player=auth.getCurrentPlayer(roomCode);
        if(!player.isAlive()) throw new IllegalStateException("Eliminated players cannot chat");
        Game game=games.getGame(roomCode);
        if(!"RUNNING".equals(game.getStatus()) || !("DAY".equals(game.getPhase()) || "VOTING".equals(game.getPhase())))
            throw new IllegalStateException("Chat is available during the DAY discussion and VOTING");
        String text=message==null?"":message.trim();
        if(text.isEmpty()||text.length()>300) throw new IllegalStateException("Message must be 1-300 characters");
        String word=crypto.decrypt(game.getSecretWord()).toLowerCase(Locale.ROOT);
        String hint=crypto.decrypt(game.getSecretHint()).toLowerCase(Locale.ROOT);
        String lower=text.toLowerCase(Locale.ROOT);
        if(lower.contains(word)||lower.contains(hint))
            throw new IllegalStateException("The moderator blocked a message that reveals the secret.");
        ChatMessage m=new ChatMessage();m.setRoomCode(roomCode);m.setUserId(player.getUserId());
        m.setUsername(auth.getCurrentUser().getUsername());m.setMessage(text);
        m=repo.save(m);
        return new ChatResponse(m.getId(),m.getUsername(),m.getMessage(),m.getCreatedAt());
    }
}
