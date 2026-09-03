package com.example.loginapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_change_password);
        setupTopBar("CHANGE PASSWORD", "HARDEN YOUR IDENTITY", "SECURITY");

        EditText password = findViewById(R.id.newPassword);
        EditText confirm = findViewById(R.id.confirmNewPassword);
        Button save = findViewById(R.id.changePasswordBtn);

        save.setOnClickListener(v -> {
            String p = password.getText().toString();
            String c = confirm.getText().toString();
            if (p.length() < 6) { password.setError("At least 6 characters"); return; }
            if (!p.equals(c)) { confirm.setError("Passwords do not match"); return; }
            Ui.toast(this, "Password updated in the demo UI. Connect the backend endpoint for persistence.");
            finish();
        });
    }
}
