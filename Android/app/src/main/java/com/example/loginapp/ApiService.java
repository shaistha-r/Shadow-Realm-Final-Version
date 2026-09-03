package com.example.loginapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @POST("users/login") Call<LoginResponse> login(@Body LoginRequest request);
    @POST("users/register") Call<RegistrationResponse> register(@Body RegisterRequest request);

    @POST("room/create") Call<RoomResponse> createRoom(@Query("maxPlayers") int maxPlayers);
    @POST("room/join") Call<RoomResponse> joinRoom(@Query("roomCode") String roomCode);
    @POST("room/leave") Call<RoomResponse> leaveRoom(@Query("roomCode") String roomCode);
    @DELETE("room/delete") Call<String> deleteRoom(@Query("roomCode") String roomCode);

    @GET("player/{roomCode}") Call<List<PlayerResponse>> players(@Path("roomCode") String roomCode);
    @GET("player/{roomCode}/alive") Call<List<PlayerResponse>> alivePlayers(@Path("roomCode") String roomCode);
    @GET("player/me/{roomCode}") Call<PrivatePlayerResponse> currentPlayer(@Path("roomCode") String roomCode);

    @POST("game/start") Call<GameResponse> startGame(@Query("roomCode") String roomCode);
    @GET("game/{roomCode}") Call<GameResponse> game(@Path("roomCode") String roomCode);
    @GET("game/{roomCode}/secret") Call<SecretResponse> secret(@Path("roomCode") String roomCode);
    @PUT("game/next") Call<GameResponse> nextPhase(@Query("roomCode") String roomCode);
    @PUT("game/winner") Call<GameResponse> winner(@Query("roomCode") String roomCode);
    @PUT("game/resolve-elimination") Call<GameResponse> resolveElimination(@Query("roomCode") String roomCode);

    @POST("night/attack") Call<Object> attack(@Query("roomCode") String roomCode, @Query("targetId") Long targetId);
    @POST("night/protect") Call<Object> protect(@Query("roomCode") String roomCode, @Query("targetId") Long targetId);
    @POST("night/investigate") Call<Object> investigate(@Query("roomCode") String roomCode, @Query("targetId") Long targetId);
    @POST("night/resolve") Call<PlayerResponse> resolveNight(@Query("roomCode") String roomCode);

    @GET("chat/{roomCode}") Call<java.util.List<ChatResponse>> chat(@Path("roomCode") String roomCode);
    @POST("chat/{roomCode}") Call<ChatResponse> sendChat(@Path("roomCode") String roomCode, @Body ChatRequest request);

    @POST("vote/cast") Call<Object> castVote(@Body VoteRequest vote);
    @GET("vote/{roomCode}/most-voted") Call<PlayerResponse> mostVoted(@Path("roomCode") String roomCode);
    @POST("vote/{roomCode}/resolve") Call<PlayerResponse> resolveVote(@Path("roomCode") String roomCode);
}
