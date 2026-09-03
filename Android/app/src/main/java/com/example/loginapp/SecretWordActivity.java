package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecretWordActivity extends RealmActivity {
    private boolean revealed;
    private String secret = "";
    private String room;
    private TextView value, caption;
    private Button reveal, continueButton;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_secret_word);
        setupTopBar("SECRET WORD", "PRIVATE PLAYER INTELLIGENCE • DO NOT SHARE", "HIDDEN");
        value=findViewById(R.id.tvSecretValue); caption=findViewById(R.id.tvSecretCaption); reveal=findViewById(R.id.btnRevealSecret); continueButton=findViewById(R.id.btnContinueSecret);
        room=getIntent().getStringExtra("ROOM_CODE");
        value.setText("••••••••");
        continueButton.setEnabled(false);
        if(room!=null) loadServerSecret(); else loadLocalSecret();
        reveal.setOnClickListener(v -> toggleSecret());
    }

    private void loadLocalSecret() {
        secret=GameEngine.get().secretForMe();
        caption.setText("This information belongs only to you. Never type it into public chat.");
        continueButton.setEnabled(true); continueButton.setOnClickListener(v -> openGame(null));
    }

    private void loadServerSecret() {
        RetrofitClient.getApiService(this).secret(room).enqueue(new Callback<SecretResponse>() {
            @Override public void onResponse(Call<SecretResponse> call, Response<SecretResponse> response) {
                if(response.isSuccessful() && response.body()!=null && response.body().getValue()!=null){
                    secret=response.body().getValue();
                    String kind="HINT".equalsIgnoreCase(response.body().getType()) ? "HINT" : "WORD";
                    caption.setText("ROUND "+response.body().getRound()+"  •  "+kind+"  •  PRIVATE TO THIS PLAYER");
                    continueButton.setEnabled(true); continueButton.setOnClickListener(v -> openGame(room));
                } else Ui.toast(SecretWordActivity.this,"Your private secret is not available yet. Make sure the game has started.");
            }
            @Override public void onFailure(Call<SecretResponse> call, Throwable t){ Ui.toast(SecretWordActivity.this,"Cannot load the private secret from the server."); }
        });
    }

    private void toggleSecret(){
        if(secret.isEmpty()) return;
        revealed=!revealed; value.setText(revealed ? secret : "••••••••"); reveal.setText(revealed ? "HIDE SECRET" : "REVEAL SECRET");
    }

    private void openGame(String onlineRoom){
        Intent i=new Intent(this,GameActivity.class); if(onlineRoom!=null)i.putExtra("ROOM_CODE",onlineRoom); startActivity(i); finish();
    }
}
