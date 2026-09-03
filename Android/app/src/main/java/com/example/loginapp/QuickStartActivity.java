package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class QuickStartActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_quick_start);
        setupTopBar("NEW REALM", "SOLO DEMO • 7 PLAYERS • FULL RULE ENGINE", "START");

        TextView player = findViewById(R.id.tvStarterName);
        player.setText("Playing as  •  " + savedName());
        findViewById(R.id.btnBeginRealm).setOnClickListener(v -> {
            GameEngine.get().start(savedName());
            startActivity(new Intent(this, RoleActivity.class));
            finish();
        });
    }
}
