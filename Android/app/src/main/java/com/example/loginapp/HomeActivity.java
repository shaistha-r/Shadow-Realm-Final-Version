package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_home);
        setupTopBar("SHADOW REALM", "TRUST NO ONE • SURVIVE THE NIGHT", "HOME");

        TextView welcome = findViewById(R.id.tvWelcome);
        welcome.setText(savedName() + " — the night is waiting.");

        findViewById(R.id.btnQuickStart).setOnClickListener(v -> startActivity(new Intent(this, QuickStartActivity.class)));
        findViewById(R.id.btnCreateRoom).setOnClickListener(v -> openRoom("CREATE"));
        findViewById(R.id.btnJoinRoom).setOnClickListener(v -> openRoom("JOIN"));
        findViewById(R.id.btnRules).setOnClickListener(v -> startActivity(new Intent(this, RulesActivity.class)));
        findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void openRoom(String mode) {
        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra("MODE", mode);
        startActivity(intent);
    }
}
