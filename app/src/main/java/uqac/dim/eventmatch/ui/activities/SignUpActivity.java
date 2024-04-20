package uqac.dim.eventmatch.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.User;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText firstname;
    private EditText lastname;
    private EditText username;
    private EditText birthdate;
    private EditText adress;
    private EditText city;
    private Button signUp;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.signup_email);
        firstname = findViewById(R.id.signup_firstname);
        lastname = findViewById(R.id.signup_lastname);
        username = findViewById(R.id.signup_username);
        birthdate = findViewById(R.id.signup_birthdate);
        adress = findViewById(R.id.signup_address);
        city = findViewById(R.id.signup_city);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        signUp = findViewById(R.id.signup_button);
        login = findViewById(R.id.switch_login);

        //Get actual Date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String y = formattedDate.split("-")[2];




        // Date picker for birthdate
        birthdate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, (view, year, month, dayOfMonth) -> {
                birthdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

            }, Integer.parseInt(y), 0, 1);
            datePickerDialog.show();
        });



        signUp.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String username = emailText.split("@")[0];
            String confirmPasswordText = confirmPassword.getText().toString().trim();
            String firstnameText = firstname.getText().toString().trim();
            String lastnameText = lastname.getText().toString().trim();
            String birthdateText = birthdate.getText().toString().trim();
            String adressText = adress.getText().toString().trim();
            String cityText = city.getText().toString().trim();


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

                                Calendar date = Calendar.getInstance();
                                String[] dateParts = birthdate.getText().toString().split("/");
                                date.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]));

                                Timestamp birthdate = new Timestamp(date.getTime());

                                User user = new User(emailText, passwordText, username, firstnameText, lastnameText, birthdate, adressText, cityText);
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