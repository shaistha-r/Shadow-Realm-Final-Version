package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleActivity extends RealmActivity {
    private String room;
    private String role;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_role);
        setupTopBar("SECRET ROLE", "PRIVATE ASSIGNMENT • NEVER REVEAL IT", "HIDDEN");
        room = getIntent().getStringExtra("ROOM_CODE");
        if (room != null) loadOnlineRole(); else showLocalRole(GameEngine.get().me().role);
    }

    private void loadOnlineRole() {
        findViewById(R.id.btnContinueRole).setEnabled(false);
        RetrofitClient.getApiService(this).currentPlayer(room).enqueue(new Callback<PrivatePlayerResponse>() {
            @Override public void onResponse(Call<PrivatePlayerResponse> call, Response<PrivatePlayerResponse> response) {
                if (response.isSuccessful() && response.body()!=null && response.body().getRole()!=null) {
                    role=response.body().getRole(); showRoleAndContinue();
                } else Ui.toast(RoleActivity.this,"Your role is not ready yet. Start the game from the host lobby.");
            }
            @Override public void onFailure(Call<PrivatePlayerResponse> call, Throwable t) { Ui.toast(RoleActivity.this,"Cannot load your private role from the server."); }
        });
    }

    private void showLocalRole(String value) { role=value; showRoleAndContinue(); }

    private void showRoleAndContinue() {
        TextView roleView=findViewById(R.id.tvRole), tagline=findViewById(R.id.tvRoleTagline), power=findViewById(R.id.tvRolePower), desc=findViewById(R.id.tvRoleDescription);
        Button button=findViewById(R.id.btnContinueRole);
        roleView.setText(role);
        if ("TRAITOR".equals(role)) {
            tagline.setText("Lie well. Survive the council. Leave no trace."); power.setText("NIGHT POWER  •  HUNT");
            desc.setText("You are the hidden Traitor. Your secret word is replaced by a related hint. Mislead the council and survive.");
        } else if ("SEER".equals(role)) {
            tagline.setText("Truth is hidden in plain sight."); power.setText("NIGHT POWER  •  INVESTIGATE");
            desc.setText("Inspect one living player each night. Your result is private and must be used carefully.");
        } else if ("DOCTOR".equals(role)) {
            tagline.setText("Keep one soul alive through the darkness."); power.setText("NIGHT POWER  •  PROTECT");
            desc.setText("Protect one living player from the Traitor's night attack.");
        } else {
            tagline.setText("Trust your instincts. Find the hidden Traitor."); power.setText("DAY POWER  •  DISCUSS & VOTE");
            desc.setText("You have no night power. Study the secret word, challenge suspicious clues and vote wisely.");
        }
        button.setEnabled(true); button.setText("OPEN MY SECRET WORD  ✦");
        button.setOnClickListener(v -> { Intent i=new Intent(this,SecretWordActivity.class); if(room!=null)i.putExtra("ROOM_CODE",room); startActivity(i); });
    }
}
