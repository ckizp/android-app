package uqac.dim.eventmatch;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void eventsave(View view) {

        Log.i("DIM", "sauvegardeEvent");

        EditText event_name = findViewById(R.id.event_name);
        EditText event_start = findViewById(R.id.event_start);
        EditText event_end = findViewById(R.id.event_end);
        EditText event_nb_participants = findViewById(R.id.event_nb_participants);
        EditText event_type = findViewById(R.id.event_type);


        Event event = new Event(
                event_name.getText().toString(),
                event_end.getText().toString(),
                event_start.getText().toString(),
                Integer.parseInt(event_nb_participants.getText().toString()),
                event_type.getText().toString());

        database = FirebaseFirestore.getInstance();
        database.collection("events").add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(MainActivity.this, "événement créé et stocké dans Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(MainActivity.this, "Erreur lors de l'envoi des données à Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void eventclear(View view) {

        Log.i("DIM", "Clear des entrées utilisateurs");

        EditText event_name = findViewById(R.id.event_name);
        EditText event_start = findViewById(R.id.event_start);
        EditText event_end = findViewById(R.id.event_end);
        EditText event_nb_participants = findViewById(R.id.event_nb_participants);
        EditText event_type = findViewById(R.id.event_type);

        event_name.setText("");
        event_start.setText("");
        event_end.setText("");
        event_nb_participants.setText("");
        event_type.setText("");
    }
}