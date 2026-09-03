package com.example.loginapp;

import android.os.Bundle;

public class RulesActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rules);
        setupTopBar("HOW TO PLAY", "LEARN THE RULES BEFORE THE FIRST NIGHT", "RULES");
    }
}
