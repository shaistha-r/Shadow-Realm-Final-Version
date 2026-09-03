package com.example.loginapp;
public class GameResponse {
    private Long id; private String roomCode, winner, phase, status; private boolean started, finished; private int currentRound, round;
    public Long getId(){return id;} public String getRoomCode(){return roomCode;} public String getWinner(){return winner;}
    public String getPhase(){return phase;} public String getStatus(){return status;} public boolean isStarted(){return started;}
    public boolean isFinished(){return finished;} public int getCurrentRound(){return currentRound > 0 ? currentRound : round;}
}
