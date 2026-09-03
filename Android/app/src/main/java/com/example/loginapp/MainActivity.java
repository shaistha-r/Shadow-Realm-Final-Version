package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        String prefill = getIntent().getStringExtra("PREFILL_EMAIL");
        if (prefill != null) emailEditText.setText(prefill);
        Button demoButton = findViewById(R.id.demoButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(v -> login());
        demoButton.setOnClickListener(v -> enterDemo());
        signUpButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        forgotPassword.setOnClickListener(v ->
                Ui.toast(this, "Password reset is not exposed by the current backend yet."));

    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }
        if (pass.length() < 6) {
            passwordEditText.setError("Use at least 6 characters");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("ENTERING…");

        RetrofitClient.getApiService(this).login(new LoginRequest(email, pass))
                .enqueue(new Callback<LoginResponse>() {
                    @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        loginButton.setEnabled(true);
                        loginButton.setText("ENTER THE REALM  ✦");

                        if (response.isSuccessful() && response.body() != null
                                && response.body().getToken() != null) {
                            SecureStore.saveToken(MainActivity.this, response.body().getToken());
                            String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : "Night Walker";
                            saveProfile(name, email);
                            openHome();
                        } else {
                            Ui.toast(MainActivity.this, "The realm rejected your credentials.");
                        }
                    }

                    @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loginButton.setEnabled(true);
                        loginButton.setText("ENTER THE REALM  ✦");
                        Ui.toast(MainActivity.this, "Cannot reach the realm server. Start Spring Boot on port 8080 and try again.");
                    }
                });
    }

    private void enterDemo() {
        SecureStore.saveToken(this, "DEMO_SESSION_TOKEN");
        saveProfile("Night Walker", "demo@shadowrealm.local");
        startActivity(new Intent(this, QuickStartActivity.class));
        finish();
    }

    private void saveProfile(String name, String email) {
        getSharedPreferences("realm_profile", MODE_PRIVATE).edit()
                .putString("name", name).putString("email", email).apply();
    }

    private void openHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
