package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_results);
        setupTopBar("THE VERDICT", "THE COUNCIL HAS SPOKEN", "RESULTS");
        if(getIntent().getBooleanExtra("ONLINE",false)) renderOnline();
        else renderLocal();
    }

    private void renderOnline(){
        String room=getIntent().getStringExtra("ROOM_CODE");
        String eliminated=getIntent().getStringExtra("ELIMINATED_NAME");
        TextView result=findViewById(R.id.txtResult), winner=findViewById(R.id.txtWinner),
                detail=findViewById(R.id.tvVerdictDetail), outcome=findViewById(R.id.tvOutcome);
        result.setText("SERVER VERDICT  •  ROUND IN PROGRESS");
        winner.setText(eliminated==null?"THE NIGHT PASSED WITHOUT AN ELIMINATION":eliminated+" HAS BEEN ELIMINATED");
        detail.setText("The result is synchronized by the Shadow Realm server. Roles remain private except where the game rules reveal them.");
        Button next=findViewById(R.id.btnNextRound), home=findViewById(R.id.btnBackHome);
        next.setText("RETURN TO THE REALM  ✦");
        next.setOnClickListener(v->{Intent i=new Intent(this,GameActivity.class);i.putExtra("ROOM_CODE",room);startActivity(i);finish();});
        home.setOnClickListener(v->returnHome(v));
        RetrofitClient.getApiService(this).game(room).enqueue(new Callback<GameResponse>(){
            @Override public void onResponse(Call<GameResponse> c, Response<GameResponse> r){
                if(!r.isSuccessful()||r.body()==null)return;
                GameResponse g=r.body();
                if(g.isFinished()){
                    outcome.setText("🏆 "+(g.getWinner()==null?"THE REALM":g.getWinner())+" WIN THE SHADOW REALM");
                    next.setText("PLAY ANOTHER REALM  ✦");
                    next.setOnClickListener(v->{OnlineSession.clear(ResultsActivity.this);returnHome(v);});
                } else outcome.setText("THE NIGHT RETURNS  •  ROUND "+g.getCurrentRound());
            }
            @Override public void onFailure(Call<GameResponse> c, Throwable t){}
        });
    }

    private void renderLocal(){
        GameEngine game=GameEngine.get();
        String voted=getIntent().getStringExtra("VOTED_PLAYER"), nightEliminated=getIntent().getStringExtra("NIGHT_ELIMINATED");
        boolean spectator=getIntent().getBooleanExtra("SPECTATOR",false);
        GameEngine.Player eliminated=game.find(voted!=null?voted:nightEliminated);
        TextView winner=findViewById(R.id.txtWinner), detail=findViewById(R.id.tvVerdictDetail),
                outcome=findViewById(R.id.tvOutcome), result=findViewById(R.id.txtResult);
        if(spectator){winner.setText("Your story ended, but the realm continued.");detail.setText("The remaining players completed the game.");result.setText("SPECTATOR MODE • FINAL VERDICT");}
        else if(nightEliminated!=null&&eliminated!=null){winner.setText(eliminated.name+" was taken by the night.");detail.setText("Hidden role • "+eliminated.role);result.setText("NIGHT RESULT • DAWN REVEALED");}
        else if(eliminated!=null){winner.setText(eliminated.name+" was eliminated.");detail.setText("Hidden role • "+eliminated.role);}
        outcome.setText(game.winner()!=null?"🏆 "+game.winner()+" WIN THE SHADOW REALM":"THE NIGHT RETURNS • ROUND "+game.round());
        Button next=findViewById(R.id.btnNextRound),home=findViewById(R.id.btnBackHome);
        next.setText(game.winner()!=null?"PLAY ANOTHER REALM ✦":"ENTER THE NEXT NIGHT ✦");
        next.setOnClickListener(v->{if(game.winner()!=null){GameEngine.get().start(savedName());Intent i=new Intent(this,RoleActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);startActivity(i);finish();}else{Intent i=new Intent(this,GameActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);startActivity(i);finish();}});
        home.setOnClickListener(v->returnHome(v));
    }
}
