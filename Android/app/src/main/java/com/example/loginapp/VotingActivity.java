package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotingActivity extends RealmActivity {
    private String room;
    private RadioGroup group;
    private Button submit;
    private List<PlayerResponse> targets;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_voting);
        setupTopBar("THE COUNCIL", "ONE VOTE • ONE CHANCE TO EXPOSE THE SHADOW", "VOTE");
        room=getIntent().getStringExtra("ROOM_CODE");
        if(room==null) room=OnlineSession.room(this);
        group=findViewById(R.id.playerGroup);
        submit=findViewById(R.id.btnSubmitVote);
        loadPlayers();
    }

    private void loadPlayers(){
        RetrofitClient.getApiService(this).alivePlayers(room).enqueue(new Callback<List<PlayerResponse>>() {
            @Override public void onResponse(Call<List<PlayerResponse>> c, Response<List<PlayerResponse>> r){
                if(!r.isSuccessful()||r.body()==null){Ui.toast(VotingActivity.this,"Could not load the council.");return;}
                targets=r.body(); showTargets();
            }
            @Override public void onFailure(Call<List<PlayerResponse>> c, Throwable t){Ui.toast(VotingActivity.this,"Cannot reach the realm server.");}
        });
    }

    private void showTargets(){
        RetrofitClient.getApiService(this).currentPlayer(room).enqueue(new Callback<PrivatePlayerResponse>() {
            @Override public void onResponse(Call<PrivatePlayerResponse> c, Response<PrivatePlayerResponse> r) {
                Long meId = r.isSuccessful() && r.body()!=null ? r.body().getId() : null;
                renderTargets(meId);
            }
            @Override public void onFailure(Call<PrivatePlayerResponse> c, Throwable t) { renderTargets(null); }
        });
    }

    private void renderTargets(Long meId){
        RadioButton[] buttons={findViewById(R.id.player1),findViewById(R.id.player2),findViewById(R.id.player3),findViewById(R.id.player4),findViewById(R.id.player5),findViewById(R.id.player6)};
        int i=0;
        for(PlayerResponse p:targets){
            if(meId != null && meId.equals(p.getId())) continue;
            if(i>=buttons.length)break;
            buttons[i].setText((p.getDisplayName()==null?"Player":p.getDisplayName()));
            buttons[i].setTag(p.getId()); buttons[i].setVisibility(View.VISIBLE); i++;
        }
        while(i<buttons.length){buttons[i].setVisibility(View.GONE);i++;}

        if(OnlineSession.voted(this)){
            if(OnlineSession.host(this)){
                submit.setText("⚖  RESOLVE THE COUNCIL");
                submit.setEnabled(true);
                submit.setOnClickListener(v->resolve());
            } else {
                submit.setText("VOTE RECORDED • WAITING");
                submit.setEnabled(false);
            }
        } else {
            submit.setText("⚔  LOCK MY VOTE");
            submit.setOnClickListener(v->cast());
        }
    }

    private void cast(){
        int id=group.getCheckedRadioButtonId();
        if(id==-1){Ui.toast(this,"Choose a suspect before locking your vote.");return;}
        RadioButton selected=findViewById(id);
        Long targetId=(Long)selected.getTag();
        submit.setEnabled(false);
        // voterId is intentionally not trusted by the backend; it replaces it with the JWT identity.
        VoteRequest req=new VoteRequest(room,0L,targetId);
        RetrofitClient.getApiService(this).castVote(req).enqueue(new Callback<Object>(){
            @Override public void onResponse(Call<Object> c, Response<Object> r){
                if(r.isSuccessful()){
                    OnlineSession.markVoted(VotingActivity.this);
                    if(OnlineSession.host(VotingActivity.this)) resolve();
                    else {Ui.toast(VotingActivity.this,"Your vote is sealed. Waiting for the council."); goGame();}
                } else {submit.setEnabled(true);Ui.toast(VotingActivity.this,"The server rejected your vote.");}
            }
            @Override public void onFailure(Call<Object> c, Throwable t){submit.setEnabled(true);Ui.toast(VotingActivity.this,"Cannot submit your vote.");}
        });
    }


    private void resolve(){
        submit.setEnabled(false);
        RetrofitClient.getApiService(this).resolveVote(room).enqueue(new Callback<PlayerResponse>(){
            @Override public void onResponse(Call<PlayerResponse> c, Response<PlayerResponse> r){
                if(r.isSuccessful()){
                    OnlineSession.markVoted(VotingActivity.this);
                    Intent i=new Intent(VotingActivity.this,ResultsActivity.class);
                    i.putExtra("ONLINE",true);i.putExtra("ROOM_CODE",room);
                    if(r.body()!=null){i.putExtra("ELIMINATED_ID",r.body().getId());i.putExtra("ELIMINATED_NAME",r.body().getDisplayName());}
                    startActivity(i);finish();
                } else {submit.setEnabled(true);Ui.toast(VotingActivity.this,"Not everyone has voted yet. Wait for the other living players.");}
            }
            @Override public void onFailure(Call<PlayerResponse> c, Throwable t){submit.setEnabled(true);Ui.toast(VotingActivity.this,"Cannot resolve the council.");}
        });
    }

    private void goGame(){Intent i=new Intent(this,GameActivity.class);i.putExtra("ROOM_CODE",room);startActivity(i);finish();}
}
