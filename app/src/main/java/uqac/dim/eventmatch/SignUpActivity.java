package uqac.dim.eventmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signUp;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        signUp = findViewById(R.id.signup_button);
        login = findViewById(R.id.switch_login);


        signUp.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if(emailText.isEmpty()) {
                email.setError("Email is required");
            }
            if(passwordText.isEmpty()) {
                password.setError("Password is required");
            }
            if(!passwordText.equals(confirmPassword.getText().toString().trim())) {
                confirmPassword.setError("Passwords do not match");
            }
            else{
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                Intent startActivity = new Intent(SignUpActivity.this, CreateFragment.class);
                                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startActivity);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        });

        login.setOnClickListener(v -> {
            Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(login);
        });
    }
}