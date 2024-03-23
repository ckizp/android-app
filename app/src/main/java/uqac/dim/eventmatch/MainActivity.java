package uqac.dim.eventmatch;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore database;
    Event event;
    EditText event_name;
    EditText event_nb_participants;
    EditText event_type;
    Button boutton_date_debut;
    Button boutton_date_fin;
    Button boutton_heure_debut;
    Button boutton_heure_fin;
    int[] debut;
    int[] fin;
    TextView affichage_debut;
    TextView affichage_fin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();
        event = new Event();

        event_name = findViewById(R.id.event_name);
        event_nb_participants = findViewById(R.id.event_nb_participants);
        event_type = findViewById(R.id.event_type);

        boutton_date_debut = findViewById(R.id.event_choix_date_debut);
        boutton_date_fin = findViewById(R.id.event_choix_date_fin);
        boutton_heure_debut = findViewById(R.id.event_choix_heure_debut);
        boutton_heure_fin = findViewById(R.id.event_choix_heure_fin);
        affichage_debut = findViewById(R.id.affichage_debut);
        affichage_fin = findViewById(R.id.affichage_fin);

        debut = new int[] {2024, 4, 1, 0, 0, 0};
        fin = new int[] {2024, 4, 1, 0, 0, 0};


        boutton_date_debut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog("date","debut");
            }
        });

        boutton_date_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog("date","fin");
            }
        });
        boutton_heure_debut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog("heure","debut");
            }
        });
        boutton_heure_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog("heure","fin");
            }
        });

        updateaffichagedate();

    }

    private void opendialog(String type,String quand){
        if (type == "date"){
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if (quand == "debut"){
                        debut[0] = year;
                        debut[1] = month+1;
                        debut[2] = dayOfMonth;
                    }
                    else if (quand == "fin") {
                        fin[0] = year;
                        fin[1] = month+1;
                        fin[2] = dayOfMonth;
                    }
                    updateaffichagedate();
                    Timestamp test;
                }
            }, 2024, 3, 1);
            dialog.show();
        }
        else if (type == "heure")
        {
            TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (quand == "debut"){
                        debut[3] = hourOfDay;
                        debut[4] = minute;
                    }
                    else if (quand == "fin") {
                        fin[3] = hourOfDay;
                        fin[4] = minute;
                    }
                    updateaffichagedate();
                }
            }, 0,0,true);
            dialog.show();
        }

    }



    public void eventsave(View view) {

        Log.i("DIM", "sauvegardeEvent");

        Log.w("DIM", "nom : " + event_name.getText().toString());
        Log.w("DIM", "nb : " + event_nb_participants.getText().toString());
        Log.w("DIM", "type : " + event_type.getText().toString());

        if (event_name.getText().toString().equals("") || event_nb_participants.getText().toString().equals("") || event_type.getText().toString().equals("")) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(MainActivity.this, "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
        else {

            event.setName(event_name.getText().toString());
            event.setNb_participants(Integer.parseInt(event_nb_participants.getText().toString()));
            event.setType(event_type.getText().toString());

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
    }

    public void eventclear(View view) {

        Log.i("DIM", "Clear des entrées utilisateurs");

        event_name.setText("");
        event_nb_participants.setText("");
        event_type.setText("");
        debut = new int[] {2024, 4, 1, 0, 0, 0};
        fin = new int[] {2024, 4, 1, 0, 0, 0};
        updateaffichagedate();

        Log.w("DIM", "Champs vidés");
        Toast.makeText(MainActivity.this, "Champs vidés", Toast.LENGTH_SHORT).show();
    }


    public String date_to_string(int[] tab){
        return "le "+tab[2]+"/" + tab[1] +"/"+ tab[0] +" à "+ tab[3] +":"+ tab[4] +":"+ tab[5];
    }
    public void updateaffichagedate() {
        affichage_debut.setText("Début "+date_to_string(debut));
        affichage_fin.setText("Fin "+date_to_string(fin));
        Date datedebut = new Date(debut[0]-1900,debut[1]-1,debut[2],debut[3], debut[4],debut[5]);
        Timestamp debut = new Timestamp(datedebut);
        Date datefin = new Date(fin[0]-1900,fin[1]-1,fin[2],fin[3],fin[4],fin[5]);
        Timestamp fin = new Timestamp(datefin);
        event.setDate_end(fin);
        event.setDate_start(debut);
    }
}