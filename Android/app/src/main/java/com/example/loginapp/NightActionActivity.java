package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NightActionActivity extends RealmActivity {
    private String room, role;
    private RadioGroup group;
    private RadioButton[] buttons;
    private Button action;
    private List<PlayerResponse> targets;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_night);
        setupTopBar("NIGHT PHASE", "THE VILLAGE SLEEPS • CHOOSE CAREFULLY", "NIGHT");
        room=getIntent().getStringExtra("ROOM_CODE");
        if(room==null) room=OnlineSession.room(this);
        group=findViewById(R.id.nightGroup);
        buttons=new RadioButton[]{findViewById(R.id.night1),findViewById(R.id.night2),findViewById(R.id.night3),findViewById(R.id.night4),findViewById(R.id.night5),findViewById(R.id.night6)};
        action=findViewById(R.id.btnNightAction);
        loadMe();
    }

    private void loadMe() {
        RetrofitClient.getApiService(this).currentPlayer(room).enqueue(new Callback<PrivatePlayerResponse>() {
            @Override public void onResponse(Call<PrivatePlayerResponse> c, Response<PrivatePlayerResponse> r) {
                if(r.isSuccessful()&&r.body()!=null){ role=r.body().getRole(); configure(); loadTargets(); }
                else Ui.toast(NightActionActivity.this,"Your private role could not be loaded.");
            }
            @Override public void onFailure(Call<PrivatePlayerResponse> c, Throwable t){Ui.toast(NightActionActivity.this,"Cannot reach the realm server.");}
        });
    }

    private void configure() {
        TextView title=findViewById(R.id.tvNightRole), instruction=findViewById(R.id.tvNightInstruction), note=findViewById(R.id.tvNightNote);
        if("TRAITOR".equals(role)){title.setText("TRAITOR • HUNT");instruction.setText("Choose a living player to eliminate.");action.setText("LOCK TARGET");note.setText("Your attack is private.");}
        else if("SEER".equals(role)){title.setText("SEER • INVESTIGATE");instruction.setText("Choose a living player to inspect.");action.setText("REVEAL TRUTH");note.setText("Only you will see the result.");}
        else if("DOCTOR".equals(role)){title.setText("DOCTOR • PROTECT");instruction.setText("Choose a living player to protect.");action.setText("GUARD PLAYER");note.setText("Your protection can stop the night attack.");}
        else {title.setText("VILLAGER • WAIT");instruction.setText("You have no night power. Wait for the host to resolve dawn.");action.setText("RETURN TO REALM");note.setText("Stay alert for the Day discussion.");action.setEnabled(false);}
    }

    private void loadTargets() {
        RetrofitClient.getApiService(this).alivePlayers(room).enqueue(new Callback<List<PlayerResponse>>() {
            @Override public void onResponse(Call<List<PlayerResponse>> c, Response<List<PlayerResponse>> r) {
                if(!r.isSuccessful()||r.body()==null)return;
                targets=r.body();
                // Current player ID is obtained again to filter self.
                RetrofitClient.getApiService(NightActionActivity.this).currentPlayer(room).enqueue(new Callback<PrivatePlayerResponse>() {
                    @Override public void onResponse(Call<PrivatePlayerResponse> c2, Response<PrivatePlayerResponse> r2) {
                        if(r2.isSuccessful() && r2.body()!=null){ showTargets(r2.body().getId()); }
                        else { showTargets(null); }
                    }
                    @Override public void onFailure(Call<PrivatePlayerResponse> c2, Throwable t){showTargets(null);}
                });
            }
            @Override public void onFailure(Call<List<PlayerResponse>> c, Throwable t){}
        });
    }

    private void showTargets(Long myId) {
        int i=0;
        for(PlayerResponse p:targets){
            if(p.getId()==null || (myId!=null && myId.equals(p.getId()))) continue;
            if(i>=buttons.length) break;
            buttons[i].setText(p.getDisplayName()==null?"Player":p.getDisplayName());
            buttons[i].setTag(p.getId());
            buttons[i].setVisibility(View.VISIBLE);
            i++;
        }
        while(i<buttons.length){buttons[i].setVisibility(View.GONE);i++;}
        action.setOnClickListener(v->submit());
    }

    private void submit() {
        if("VILLAGER".equals(role))return;
        int id=group.getCheckedRadioButtonId();
        if(id==-1){Ui.toast(this,"Choose a living player first.");return;}
        RadioButton selected=findViewById(id);
        Long targetId=(Long)selected.getTag();
        action.setEnabled(false);
        Call<?> call;
        if("TRAITOR".equals(role)) call=RetrofitClient.getApiService(this).attack(room,targetId);
        else if("DOCTOR".equals(role)) call=RetrofitClient.getApiService(this).protect(room,targetId);
        else call=RetrofitClient.getApiService(this).investigate(room,targetId);

        if("SEER".equals(role)){
            ((Call<InvestigationResponse>)call).enqueue(new Callback<InvestigationResponse>(){
                @Override public void onResponse(Call<InvestigationResponse> c, Response<InvestigationResponse> r){
                    action.setEnabled(true);
                    if(r.isSuccessful()&&r.body()!=null){
                        Ui.toast(NightActionActivity.this, r.body().getPlayerName()+" is "+r.body().getRole()+".");
                        OnlineSession.markNightActed(NightActionActivity.this); goGame();
                    } else {Ui.toast(NightActionActivity.this,"Investigation failed.");}
                }
                @Override public void onFailure(Call<InvestigationResponse> c, Throwable t){action.setEnabled(true);Ui.toast(NightActionActivity.this,"Cannot perform investigation.");}
            });
        } else {
            ((Call<Object>)call).enqueue(new Callback<Object>(){
                @Override public void onResponse(Call<Object> c, Response<Object> r){
                    action.setEnabled(true);
                    if(r.isSuccessful()){OnlineSession.markNightActed(NightActionActivity.this);goGame();}
                    else Ui.toast(NightActionActivity.this,"Night action was rejected by the server.");
                }
                @Override public void onFailure(Call<Object> c, Throwable t){action.setEnabled(true);Ui.toast(NightActionActivity.this,"Cannot perform night action.");}
            });
        }
    }

    private void goGame(){Intent i=new Intent(this,GameActivity.class);i.putExtra("ROOM_CODE",room);startActivity(i);finish();}
}
