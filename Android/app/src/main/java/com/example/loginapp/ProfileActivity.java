package com.example.loginapp;

import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_profile);
        setupTopBar("YOUR IDENTITY", "SESSION & SECURITY", "PROFILE");

        ((TextView) findViewById(R.id.tvProfileName)).setText(savedName());
        ((TextView) findViewById(R.id.tvProfileEmail)).setText(savedEmail());
        ((TextView) findViewById(R.id.tvSession)).setText(
                SecureStore.hasToken(this) ? "● Active • JWT session available" : "○ Signed out");

        findViewById(R.id.btnChangePassword).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ChangePasswordActivity.class)));
    }
}
