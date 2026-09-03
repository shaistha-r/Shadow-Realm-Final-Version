package com.example.loginapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class RealmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(3, 1, 8));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(3, 1, 8));
    }

    protected void setupTopBar(String title, String subtitle, String badge) {
        TextView titleView = findViewById(R.id.tvTopTitle);
        TextView subtitleView = findViewById(R.id.tvTopSubtitle);
        TextView badgeView = findViewById(R.id.tvTopBadge);
        if (titleView != null) titleView.setText(title);
        if (subtitleView != null) subtitleView.setText(subtitle);
        if (badgeView != null) badgeView.setText(badge);

        ImageButton back = findViewById(R.id.btnBack);
        if (back != null) {
            back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    protected String savedEmail() {
        return getSharedPreferences("realm_profile", MODE_PRIVATE)
                .getString("email", "player@shadowrealm.local");
    }

    protected String savedName() {
        return getSharedPreferences("realm_profile", MODE_PRIVATE)
                .getString("name", "Night Walker");
    }

    protected void saveProfile(String name, String email) {
        getSharedPreferences("realm_profile", MODE_PRIVATE).edit()
                .putString("name", name)
                .putString("email", email)
                .apply();
    }

    public void returnHome(android.view.View view) {
        android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void logout() {
        SecureStore.clear(this);
        getSharedPreferences("realm_profile", MODE_PRIVATE).edit().clear().apply();
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
