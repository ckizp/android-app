package uqac.dim.eventmatch.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.User;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();

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
                                // Create a new user in the database
                                CollectionReference dbUsers = db.collection("users");
                                User user = new User(emailText, passwordText);
                                String uid = auth.getCurrentUser().getUid();

                                dbUsers.document(uid).set(user).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Log.i("SignUpActivity", "User created in database");
                                        Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        Log.i("SignUpActivity", "Error creating user in database: " + task2.getException().getMessage());
                                        Toast.makeText(SignUpActivity.this, "Error creating user", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        });

        /// Switch to login activity
        login.setOnClickListener(v -> {
            Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(login);
        });
    }

}