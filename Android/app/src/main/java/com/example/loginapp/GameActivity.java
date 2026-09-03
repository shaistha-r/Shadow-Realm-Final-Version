package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends RealmActivity {
    private String room;
    private GameResponse game;
    private PrivatePlayerResponse me;
    private Button chat, primary, vote;
    private TextView round, status, hint, roleView, players;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.game_screen);
        setupTopBar("SHADOW REALM", "THE REALM IS LIVE • STAY ALERT", "GAME");
        room = getIntent().getStringExtra("ROOM_CODE");
        if (room == null) room = OnlineSession.room(this);

        round=findViewById(R.id.tvRound); status=findViewById(R.id.txtGameStatus);
        hint=findViewById(R.id.tvGameHint); roleView=findViewById(R.id.tvGameRole);
        players=findViewById(R.id.tvAlivePlayers); chat=findViewById(R.id.btnChat);
        primary=findViewById(R.id.btnNight); vote=findViewById(R.id.btnVote);
        findViewById(R.id.btnSpectateEnd).setVisibility(View.GONE);
        findViewById(R.id.btnBackToHomeFromGame).setVisibility(View.GONE);

        if (room == null) { Ui.toast(this, "No active realm found."); finish(); return; }
        refresh();
    }

    private void refresh() {
        RetrofitClient.getApiService(this).game(room).enqueue(new Callback<GameResponse>() {
            @Override public void onResponse(Call<GameResponse> c, Response<GameResponse> r) {
                if (r.isSuccessful() && r.body()!=null) { game=r.body(); loadMe(); }
                else Ui.toast(GameActivity.this, "The realm state could not be loaded.");
            }
            @Override public void onFailure(Call<GameResponse> c, Throwable t) { Ui.toast(GameActivity.this, "Cannot reach the realm server."); }
        });
    }

    private void loadMe() {
        RetrofitClient.getApiService(this).currentPlayer(room).enqueue(new Callback<PrivatePlayerResponse>() {
            @Override public void onResponse(Call<PrivatePlayerResponse> c, Response<PrivatePlayerResponse> r) {
                if (r.isSuccessful() && r.body()!=null) { me=r.body(); render(); }
                else Ui.toast(GameActivity.this, "Your private player state is unavailable.");
            }
            @Override public void onFailure(Call<PrivatePlayerResponse> c, Throwable t) { Ui.toast(GameActivity.this, "Cannot load your role."); }
        });
        RetrofitClient.getApiService(this).players(room).enqueue(new Callback<List<PlayerResponse>>() {
            @Override public void onResponse(Call<List<PlayerResponse>> c, Response<List<PlayerResponse>> r) {
                if (!r.isSuccessful() || r.body()==null) return;
                StringBuilder sb=new StringBuilder("LIVING PLAYERS\n");
                for(PlayerResponse p:r.body()) if(p.isAlive())
                    sb.append("◈ ").append(p.getDisplayName()==null?"Player":p.getDisplayName())
                      .append(p.isHost()?"  •  HOST":"").append("\n");
                players.setText(sb.toString().trim());
            }
            @Override public void onFailure(Call<List<PlayerResponse>> c, Throwable t) {}
        });
    }

    private void render() {
        OnlineSession.syncRound(this, game.getCurrentRound());
        round.setText("ROUND " + game.getCurrentRound() + "  •  " + game.getPhase());
        roleView.setText((me.getRole()==null?"ROLE HIDDEN":me.getRole()) + "  •  PRIVATE");
        if (game.isFinished()) {
            status.setText("🏆 " + (game.getWinner()==null?"THE REALM":game.getWinner()) + " HAVE WON THE SHADOW REALM.");
            hint.setText("The ritual is complete. The verdict has been sealed.");
            primary.setText("RETURN TO HOME");
            primary.setEnabled(true);
            primary.setOnClickListener(v -> returnHome(v));
            vote.setEnabled(false); chat.setEnabled(false);
            return;
        }
        if (!me.isAlive()) {
            status.setText("YOU HAVE FALLEN INTO THE SHADOWS.");
            hint.setText("You can no longer act. Watch the realm from the outside.");
            primary.setText("SPECTATE TO END");
            primary.setEnabled(true);
            primary.setOnClickListener(v -> { Ui.toast(this,"The current realm continues on the server. Return to Home when the verdict is reached."); });
            vote.setEnabled(false); chat.setEnabled(false);
            return;
        }

        chat.setEnabled("DAY".equals(game.getPhase()) || "VOTING".equals(game.getPhase()));
        chat.setOnClickListener(v -> { Intent i=new Intent(this,ChatActivity.class); i.putExtra("ROOM_CODE",room); startActivity(i); });

        if ("NIGHT".equals(game.getPhase())) {
            if (OnlineSession.host(this)) {
                boolean needsAction = !"VILLAGER".equalsIgnoreCase(me.getRole()) && !OnlineSession.nightActed(this);
                primary.setText(needsAction ? "☾  NIGHT ACTION" : "☀  RESOLVE NIGHT");
                primary.setEnabled(true);
                primary.setOnClickListener(v -> {
                    if (needsAction) {
                        Intent i=new Intent(this,NightActionActivity.class); i.putExtra("ROOM_CODE",room); startActivity(i);
                    } else resolveNight();
                });
            } else {
                boolean special = !"VILLAGER".equalsIgnoreCase(me.getRole());
                primary.setText(special ? "☾  NIGHT ACTION" : "WAIT FOR DAWN");
                primary.setEnabled(special);
                primary.setOnClickListener(v -> { Intent i=new Intent(this,NightActionActivity.class); i.putExtra("ROOM_CODE",room); startActivity(i); });
            }
            vote.setEnabled(false);
            hint.setText("Night is active. Traitor hunts, Doctor protects, Seer investigates.");
        } else if ("DAY".equals(game.getPhase())) {
            primary.setText("☀  DAY DISCUSSION");
            primary.setEnabled(false);
            vote.setText(OnlineSession.host(this) ? "⚔  OPEN COUNCIL" : "WAIT FOR COUNCIL");
            vote.setEnabled(OnlineSession.host(this));
            vote.setOnClickListener(v -> nextPhase());
            hint.setText("Discuss the secret without saying it. The host opens the council when everyone is ready.");
        } else if ("VOTING".equals(game.getPhase())) {
            primary.setText("COUNCIL IS OPEN");
            primary.setEnabled(false);
            vote.setText("⚔  CAST MY VOTE");
            vote.setEnabled(true);
            vote.setOnClickListener(v -> { Intent i=new Intent(this,VotingActivity.class); i.putExtra("ROOM_CODE",room); startActivity(i); });
            hint.setText("Choose carefully. Every living player must cast one vote.");
        } else {
            primary.setText("RETURN TO REALM");
            primary.setEnabled(true);
            primary.setOnClickListener(v -> refresh());
            vote.setEnabled(false);
        }
    }

    private void resolveNight() {
        primary.setEnabled(false);
        RetrofitClient.getApiService(this).resolveNight(room).enqueue(new Callback<PlayerResponse>() {
            @Override public void onResponse(Call<PlayerResponse> c, Response<PlayerResponse> r) {
                OnlineSession.clear(GameActivity.this);
                OnlineSession.saveRoom(GameActivity.this,room, true);
                if (r.isSuccessful()) {
                    Intent i = new Intent(GameActivity.this, ResultsActivity.class);
                    i.putExtra("ONLINE", true);
                    i.putExtra("ROOM_CODE", room);
                    if (r.body() != null) i.putExtra("ELIMINATED_NAME", r.body().getDisplayName());
                    startActivity(i);
                    finish();
                } else { primary.setEnabled(true); Ui.toast(GameActivity.this,"Night cannot be resolved yet. Make sure the Traitor has acted."); }
            }
            @Override public void onFailure(Call<PlayerResponse> c, Throwable t) { primary.setEnabled(true); Ui.toast(GameActivity.this,"Cannot resolve night."); }
        });
    }

    private void nextPhase() {
        vote.setEnabled(false);
        RetrofitClient.getApiService(this).nextPhase(room).enqueue(new Callback<GameResponse>() {
            @Override public void onResponse(Call<GameResponse> c, Response<GameResponse> r) { if(r.isSuccessful()) refresh(); else {vote.setEnabled(true); Ui.toast(GameActivity.this,"The council cannot open yet.");} }
            @Override public void onFailure(Call<GameResponse> c, Throwable t) {vote.setEnabled(true); Ui.toast(GameActivity.this,"Cannot advance the realm.");}
        });
    }

    @Override protected void onResume() { super.onResume(); if (room!=null && game!=null) refresh(); }
}
