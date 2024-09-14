package com.example.schooltaskmanagementsystem.User_Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schooltaskmanagementsystem.MainActivity;
import com.example.schooltaskmanagementsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;

    String url_login = "https://scarlet2.io/Group11/SchoolTaskManagementSystem/login.php"; // Ensure this is correct

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize views
        email = findViewById(R.id.SignInEmail);
        password = findViewById(R.id.SignInPassword);
        loginBtn = findViewById(R.id.btn_yellow_SignIn);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // LOGIN: Signing in
        loginBtn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            // Input validation
            if (!validateEmail() || !validatePassword()) {
                return;
            }

            // Perform network request for authentication
            authenticateUser(emailInput, passwordInput);
        });

        // Intent for Yellow_SignUp button
        Button yellowSignUp = findViewById(R.id.Yellow_SignUp);
        yellowSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, SignUpPage.class);
            startActivity(intent);
        });


    }

    private boolean validateEmail() {
        String val = email.getText().toString().trim();
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getText().toString().trim();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void authenticateUser(String emailInput, String passwordInput) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                response -> {
                    Log.d("LoginPage", "Server response: " + response);
                    try {
                        // Directly parse the response as JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String success = jsonResponse.getString("success");

                        if (success.equalsIgnoreCase("1")) {
                            if (jsonResponse.has("token")) {
                                String token = jsonResponse.getString("token");
                                saveToken(token);

                                // Start MainActivity
                                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d("LoginPage", "Token not found in response");
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Authentication failed");
                            password.setError(message);  // Ensure this is called on the EditText object
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(LoginPage.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace(); // Print stack trace for debugging
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailInput);
                params.put("password", passwordInput);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(LoginPage.this);
        queue.add(stringRequest);
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }
}
