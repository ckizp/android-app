package uqac.dim.eventmatch;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot, Ibraguim Temirkhaev
 */
public class CreateFragment extends Fragment {
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
    Button button_save;
    Button button_clear;

    View view;

    public CreateFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create, container, false);
        super.onCreate(savedInstanceState);

        database = FirebaseFirestore.getInstance();
        event = new Event();

        event_name = view.findViewById(R.id.event_name);
        event_nb_participants = view.findViewById(R.id.event_nb_participants);
        event_type = view.findViewById(R.id.event_type);

        boutton_date_debut = view.findViewById(R.id.event_choix_date_debut);
        boutton_date_fin = view.findViewById(R.id.event_choix_date_fin);
        boutton_heure_debut = view.findViewById(R.id.event_choix_heure_debut);
        boutton_heure_fin = view.findViewById(R.id.event_choix_heure_fin);
        affichage_debut = view.findViewById(R.id.affichage_debut);
        affichage_fin = view.findViewById(R.id.affichage_fin);

        button_save = view.findViewById(R.id.event_button_save);
        button_clear = view.findViewById(R.id.event_button_clear);

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

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsave(button_save);
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventclear(button_clear);
            }
        });

        return view;
    }

    private void opendialog(String type,String quand){
        if (type == "date"){
            DatePickerDialog dialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
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
            TimePickerDialog dialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
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
            Toast.makeText(view.getContext(), "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
        else {
            event.setName(event_name.getText().toString());
            event.setNb_participants(Integer.parseInt(event_nb_participants.getText().toString()));
            event.setType(event_type.getText().toString());

            List<DocumentReference> partipantsliste = new ArrayList<DocumentReference>();

            DocumentReference userRef = database.collection("users").document("tTQCwrPyqVIIl8T0U1x4");
            partipantsliste.add(userRef);
            event.setParticipants(partipantsliste);

            database.collection("events").add(event)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            Toast.makeText(view.getContext(), "événement créé et stocké dans Firestore", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(view.getContext(), "Erreur lors de l'envoi des données à Firestore", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(view.getContext(), "Champs vidés", Toast.LENGTH_SHORT).show();
    }

    public void updateaffichagedate() {
        event.TabsetDate_start(debut);
        event.TabsetDate_end(debut);

        affichage_debut.setText("Début "+event.Date_startString());
        affichage_fin.setText("Fin "+event.Date_endString());
    }

    public void backmenu(View view) {
        Toast.makeText(view.getContext(), "Retour Main", Toast.LENGTH_SHORT).show();
        Intent startActivity = new Intent(view.getContext(), MainActivity.class);
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startActivity);
    }
}