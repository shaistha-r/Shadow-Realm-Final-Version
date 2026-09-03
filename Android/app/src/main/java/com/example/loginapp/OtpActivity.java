package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OtpActivity extends RealmActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_otp);
        setupTopBar("IDENTITY CHECK", "ONE LAST STEP BEFORE THE GATES", "MFA");

        EditText otp = findViewById(R.id.otp);
        Button verify = findViewById(R.id.verifyOtp);
        TextView resend = findViewById(R.id.resendOtp);

        verify.setOnClickListener(v -> {
            if (otp.getText().toString().trim().length() != 6) {
                otp.setError("Enter 6 digits");
                return;
            }
            SecureStore.saveToken(this, "DEMO_REGISTERED_SESSION");
            Ui.toast(this, "Identity verified. Welcome to Shadow Realm.");
            startActivity(new Intent(this, HomeActivity.class));
            finishAffinity();
        });

        resend.setOnClickListener(v -> Ui.toast(this, "A fresh verification code has been requested."));
    }
}
