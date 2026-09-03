package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends RealmActivity {
    private Button register;
    private EditText fullName, email, password, confirm;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);
        setupTopBar("CREATE IDENTITY", "A NEW NAME ENTERS THE NIGHT", "REGISTER");
        fullName = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.register);

        TextView back = findViewById(R.id.backToLogin);
        register.setOnClickListener(v -> registerUser());
        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void registerUser() {
        String name = fullName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pw = password.getText().toString();
        String cp = confirm.getText().toString();

        if (name.length() < 2) { fullName.setError("Choose a name with at least 2 characters"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) { email.setError("Enter a valid email"); return; }
        if (pw.length() < 6) { password.setError("Use at least 6 characters"); return; }
        if (!pw.equals(cp)) { confirm.setError("Passwords do not match"); return; }

        register.setEnabled(false);
        register.setText("CREATING YOUR IDENTITY…");

        RetrofitClient.getApiService(this).register(new RegisterRequest(name, mail, pw))
                .enqueue(new Callback<RegistrationResponse>() {
                    @Override public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                        register.setEnabled(true);
                        register.setText("CREATE IDENTITY  ✦");
                        if (response.isSuccessful() && response.body() != null) {
                            saveProfile(name, mail);
                            Ui.toast(RegisterActivity.this, "Identity created. Enter the realm with your new ID.");
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            i.putExtra("PREFILL_EMAIL", mail);
                            startActivity(i);
                            finish();
                        } else {
                            Ui.toast(RegisterActivity.this, "That identity could not be created. The email may already exist.");
                        }
                    }

                    @Override public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                        register.setEnabled(true);
                        register.setText("CREATE IDENTITY  ✦");
                        Ui.toast(RegisterActivity.this, "Cannot reach the realm server. Start Spring Boot and try again.");
                    }
                });
    }

    protected void saveProfile(String name, String mail) {
        getSharedPreferences("realm_profile", MODE_PRIVATE).edit()
                .putString("name", name).putString("email", mail).apply();
    }
}
