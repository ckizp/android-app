package uqac.dim.eventmatch.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import uqac.dim.eventmatch.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private Button login;
    private TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_button);
        signUp = findViewById(R.id.switch_signup);


        login.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if(emailText.isEmpty()) {
                email.setError("Email is required");
            }
            if(passwordText.isEmpty()) {
                password.setError("Password is required");
            }
            else{
                auth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signUp.setOnClickListener(v -> {
            Intent signup = new Intent(LoginActivity.this, SignUpActivity.class);
            signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signup);
        });


    }
}