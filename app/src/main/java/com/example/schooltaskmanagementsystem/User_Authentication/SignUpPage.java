package com.example.schooltaskmanagementsystem.User_Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schooltaskmanagementsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {
    EditText signUpUsername, signUpEmail, signUpPassword;
    Button signUpBtn;

    // URL for the signup endpoint
    String url_signup = "https://scarlet2.io/Group11/SchoolTaskManagementSystem/register.php"; // Ensure this is correct

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Initialize UI elements
        signUpUsername = findViewById(R.id.SignUpUsername);
        signUpEmail = findViewById(R.id.SignUpEmail);
        signUpPassword = findViewById(R.id.SignUpPassword);
        signUpBtn = findViewById(R.id.btn_yellow_SignUp);

        // Button click listeners
        signUpBtn.setOnClickListener(v -> {
            String email = signUpEmail.getText().toString().trim();
            String password = signUpPassword.getText().toString().trim();
            String username = signUpUsername.getText().toString().trim();

            // Proceed with signup if form is valid
            if (!validateName(username) || !validateEmail(email) || !validatePassword(password)) {
                return;
            }
            signupUser(email, password, username);
        });
    }

    private boolean validateName(String username) {
        if (username.isEmpty()) {
            signUpUsername.setError("Field cannot be empty");
            return false;
        } else {
            signUpUsername.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            signUpEmail.setError("Field cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmail.setError("Please enter a valid email");
            return false;
        } else {
            signUpEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        String passwordVal = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        if (password.isEmpty()) {
            signUpPassword.setError("Field cannot be empty");
            return false;
        } else if (!password.matches(passwordVal)) {
            signUpPassword.setError("Password must have at least:\n- 1 uppercase letter\n- 1 lowercase letter\n- 1 digit\n- 8 characters");
            return false;
        } else {
            signUpPassword.setError(null);
            return true;
        }
    }

    private void signupUser(String email, String password, String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        String success = jsonResponse.optString("success", "0");

                        if ("1".equals(success)) {
                            onSignupSuccess();
                        } else {
                            String message = jsonResponse.optString("message", "Signup failed");
                            onSignupFailure(message);
                        }
                    } catch (JSONException e) {
                        Log.e("SignupPage", "JSON parsing error: ", e);
                        runOnUiThread(() -> showToast("Error: " + e.getMessage()));
                    }
                },
                error -> {
                    Log.e("SignupPage", "Network error: ", error);
                    runOnUiThread(() -> showToast("Network error: " + error.getMessage()));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("name", username);
                return params;
            }
        };

        int socketTimeout = 30000; // 30 seconds timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpPage.this);
        requestQueue.add(stringRequest);
    }

    private void onSignupSuccess() {
        Log.d("SignupPage", "Signup successful");
        runOnUiThread(() -> {
            showToast("Account created successfully");
            Intent intent = new Intent(SignUpPage.this, LoginPage.class);
            startActivity(intent);
        });
    }

    private void onSignupFailure(String message) {
        Log.e("SignupPage", "Signup failed: " + message);
        runOnUiThread(() -> showToast(message));
    }

    private void showToast(String message) {
        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
