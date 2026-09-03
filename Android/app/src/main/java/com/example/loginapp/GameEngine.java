package com.example.loginapp;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Self-contained local game engine used by the presentation/demo mode.
 * It mirrors the backend concepts: room, players, roles, secret word,
 * night action, discussion, voting, elimination and winner detection.
 */
public final class GameEngine {
    public static final class Player {
        public final String name;
        public final String role;
        public boolean alive = true;
        public Player(String name, String role) { this.name = name; this.role = role; }
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String[][] WORDS = {
            {"MOON", "night sky"}, {"CASTLE", "ancient fortress"}, {"FOREST", "many trees"},
            {"OCEAN", "deep water"}, {"CANDLE", "small flame"}, {"LIBRARY", "place of books"},
            {"THUNDER", "loud storm sound"}, {"MIRROR", "shows a reflection"},
            {"DRAGON", "mythical creature"}, {"DESERT", "dry landscape"}, {"VOLCANO", "mountain that erupts"},
            {"CLOCK", "measures passing time"}, {"MASK", "covers the face"}, {"RAVEN", "dark-feathered bird"}
    };
    private static final String[] NAMES = {"Mira", "Kael", "Riven", "Nova", "Iris", "Ash"};
    private static GameEngine instance;

    private final List<Player> players = new ArrayList<>();
    private final Map<String, String> votes = new HashMap<>();
    private final List<String> chat = new ArrayList<>();
    private final Set<String> protectedTonight = new HashSet<>();
    private String roomCode;
    private String word;
    private String hint;
    private int round = 1;
    private String phase = "NIGHT";
    private String winner = null;
    private String lastNightMessage = "The realm waits for the first move.";
    private String lastEliminated = null;

    private GameEngine() {}

    public static GameEngine get() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }

    public void start(String playerName) {
        players.clear(); votes.clear(); chat.clear(); protectedTonight.clear();
        winner = null; lastEliminated = null; round = 1; phase = "NIGHT";
        roomCode = makeRoomCode();
        int wordIndex = RANDOM.nextInt(WORDS.length);
        word = WORDS[wordIndex][0];
        hint = WORDS[wordIndex][1];

        List<String> roles = new ArrayList<>();
        roles.add("TRAITOR");
        roles.add("SEER");
        roles.add("HEALER");
        roles.add("VILLAGER");
        roles.add("VILLAGER");
        roles.add("VILLAGER");
        roles.add("VILLAGER");
        Collections.shuffle(roles, RANDOM);

        players.add(new Player(playerName == null || playerName.trim().isEmpty() ? "Night Walker" : playerName.trim(), roles.remove(0)));
        for (int i = 0; i < NAMES.length; i++) players.add(new Player(NAMES[i], roles.remove(0)));

        chat.add("SYSTEM  •  The gates are sealed. Seven players remain.");
        chat.add("Mira  •  I have a strange feeling about this round.");
        chat.add("Kael  •  Nobody reveal the secret word. Not even by accident.");
    }

    public String roomCode() { return roomCode; }
    public String word() { return word; }
    public String hint() { return hint; }
    public int round() { return round; }
    public String phase() { return phase; }
    public String winner() { return winner; }
    public String lastNightMessage() { return lastNightMessage; }
    public String lastEliminated() { return lastEliminated; }
    public List<Player> players() { return Collections.unmodifiableList(players); }

    public Player me() { return players.isEmpty() ? null : players.get(0); }
    public boolean isAlive(String name) {
        for (Player p : players) if (p.name.equals(name)) return p.alive;
        return false;
    }

    public List<Player> aliveOthers() {
        List<Player> result = new ArrayList<>();
        for (int i = 1; i < players.size(); i++) if (players.get(i).alive) result.add(players.get(i));
        return result;
    }

    public String secretForMe() {
        return "TRAITOR".equals(me().role) ? "SHADOW HINT: " + hint : "SECRET WORD: " + word;
    }

    public boolean isTraitor() { return me() != null && "TRAITOR".equals(me().role); }

    public void enterDay() {
        if (winner != null) return;
        phase = "DAY";
        chat.add("SYSTEM  •  Dawn breaks. The living return to the council.");
        addBotDiscussion();
    }

    private void addBotDiscussion() {
        String[] lines = {
                "Nova  •  Someone is being too careful with their answers.",
                "Riven  •  I think the quiet player knows more than they say.",
                "Iris  •  We need evidence, not panic.",
                "Ash  •  Watch who changes their story when the pressure rises."
        };
        int start = Math.min(round - 1, lines.length - 1);
        chat.add(lines[start]);
    }

    public List<String> chat() { return Collections.unmodifiableList(chat); }

    public void addChat(String name, String message) {
        if (message == null || message.trim().isEmpty()) return;
        chat.add(name + "  •  " + message.trim());
    }

    public void resolveNight(String targetName) {
        if (winner != null) return;
        Player me = me();
        if (me == null || !me.alive) return;

        if ("HEALER".equals(me.role) && targetName != null) protectedTonight.add(targetName);
        if ("TRAITOR".equals(me.role) && targetName != null) {
            attack(targetName);
        } else {
            // The bot traitor acts automatically when the human is not the traitor.
            Player traitor = findRole("TRAITOR");
            if (traitor != null && traitor.alive) {
                List<Player> candidates = new ArrayList<>();
                for (Player p : players) if (p.alive && !p.role.equals("TRAITOR")) candidates.add(p);
                if (!candidates.isEmpty()) attack(candidates.get(RANDOM.nextInt(candidates.size())).name);
            }
        }
        if ("SEER".equals(me.role) && targetName != null) {
            Player inspected = find(targetName);
            if (inspected != null) lastNightMessage = inspected.name + " is " + inspected.role + ". Only you can see this truth.";
        } else if (!lastNightMessage.contains("Only you")) {
            if (lastNightMessage.equals("The realm waits for the first move.")) lastNightMessage = "Night action resolved. Dawn is near.";
        }
        phase = "DAY";
        protectedTonight.clear();
        chat.add("SYSTEM  •  Night is over. Review the clues before the council votes.");
        addBotDiscussion();
    }

    private void attack(String targetName) {
        Player target = find(targetName);
        if (target == null || !target.alive || target == me()) return;
        if (protectedTonight.contains(target.name)) {
            lastNightMessage = target.name + " survived the night. Someone protected them.";
            return;
        }
        target.alive = false;
        lastNightMessage = target.name + " vanished into the shadows during the night.";
        checkWinner();
    }

    public void vote(String voter, String target) { if (voter != null && target != null) votes.put(voter, target); }

    public void resolveVotes() {
        if (winner != null) return;
        Player me = me();
        // A living human must cast a vote. If the human was eliminated, the AI council may continue automatically.
        if (me != null && me.alive && !votes.containsKey(me.name)) return;

        // Bots vote. Traitor avoids voting for self and nudges suspicion toward a random living non-traitor.
        Player traitor = findRole("TRAITOR");
        for (Player bot : players) {
            if (!bot.alive || bot == me) continue;
            List<Player> candidates = new ArrayList<>();
            for (Player p : players) if (p.alive && p != bot) candidates.add(p);
            if (candidates.isEmpty()) continue;
            Player target;
            if ("TRAITOR".equals(bot.role)) {
                List<Player> safe = new ArrayList<>();
                for (Player p : candidates) if (!"TRAITOR".equals(p.role)) safe.add(p);
                target = safe.isEmpty() ? candidates.get(0) : safe.get(RANDOM.nextInt(safe.size()));
            } else if (traitor != null && traitor.alive && RANDOM.nextInt(100) < 48) {
                target = traitor;
            } else {
                target = candidates.get(RANDOM.nextInt(candidates.size()));
            }
            votes.put(bot.name, target.name);
        }

        Map<String, Integer> counts = new HashMap<>();
        for (String target : votes.values()) counts.put(target, counts.getOrDefault(target, 0) + 1);
        String top = null; int topCount = -1;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > topCount) { top = e.getKey(); topCount = e.getValue(); }
        }
        if (top != null) {
            Player eliminated = find(top);
            if (eliminated != null) {
                eliminated.alive = false;
                lastEliminated = eliminated.name;
                chat.add("SYSTEM  •  " + eliminated.name + " was eliminated by the council. Role: " + eliminated.role + ".");
            }
        }
        votes.clear();
        checkWinner();
        if (winner == null) {
            round++;
            phase = "NIGHT";
            protectedTonight.clear();
            lastNightMessage = "The realm darkens again. Choose your next move.";
        }
    }

    /** Finish the AI-vs-AI portion if the human player is eliminated. */
    public void spectateToEnd() {
        int guard = 0;
        while (winner == null && guard++ < 30) {
            if ("NIGHT".equals(phase)) {
                resolveBotNightOnly();
            } else {
                resolveVotes();
            }
        }
    }

    private void resolveBotNightOnly() {
        Player traitor = findRole("TRAITOR");
        if (traitor != null && traitor.alive) {
            List<Player> candidates = new ArrayList<>();
            for (Player p : players) if (p.alive && !"TRAITOR".equals(p.role)) candidates.add(p);
            if (!candidates.isEmpty()) attack(candidates.get(RANDOM.nextInt(candidates.size())).name);
        }
        protectedTonight.clear();
        phase = "DAY";
        addBotDiscussion();
        checkWinner();
    }

    public void checkWinner() {
        int traitors = 0, others = 0;
        for (Player p : players) if (p.alive) {
            if ("TRAITOR".equals(p.role)) traitors++; else others++;
        }
        if (traitors == 0) winner = "VILLAGERS";
        else if (traitors >= others) winner = "TRAITOR";
    }

    public Player find(String name) { for (Player p : players) if (p.name.equals(name)) return p; return null; }
    private Player findRole(String role) { for (Player p : players) if (p.alive && role.equals(p.role)) return p; return null; }

    private String makeRoomCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 6; i++) s.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        return s.toString();
    }
}
