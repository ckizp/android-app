package uqac.dim.eventmatch.ui.fragments.profile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

/*/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {

    private View rootView;
    private Event event_db;
    private Event event_modif;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private EditText eventName;
    private EditText participantsCount;
    private EditText eventType;
    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private int[] debut;
    private int[] fin;
    private TextView startTextView;
    private TextView endTextView;
    private Button saveButton;
    private Button reinitButton;

    public EditEventFragment() {
        // Required empty public constructor
    }

    public EditEventFragment(Event e){
        event_db = e;
        event_modif = e;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);


        database = FirebaseFirestore.getInstance();
        /*storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();*/

        eventName = rootView.findViewById(R.id.editevent_name);
        participantsCount = rootView.findViewById(R.id.editevent_nb_participants);
        eventType = rootView.findViewById(R.id.editevent_type);

        startDateButton = rootView.findViewById(R.id.editevent_choix_date_debut);
        endDateButton = rootView.findViewById(R.id.editevent_choix_date_fin);
        startTimeButton = rootView.findViewById(R.id.editevent_choix_heure_debut);
        endTimeButton = rootView.findViewById(R.id.editevent_choix_heure_fin);
        startTextView = rootView.findViewById(R.id.editevent_affichage_debut);
        endTextView = rootView.findViewById(R.id.editevent_affichage_fin);

        update_all_editview(event_db);

        saveButton = rootView.findViewById(R.id.editevent_button_save);
        reinitButton = rootView.findViewById(R.id.editevent_button_reinit);

        debut = timestamp_to_tab(event_db.getStartDate());
        fin = timestamp_to_tab(event_db.getEndDate());




        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("date","debut");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("date","fin");
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("heure","debut");
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("heure","fin");
            }
        });

        updateDate();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick(saveButton);
            }
        });

        reinitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinitEvent();
            }
        });

        return rootView;
    }


    private void openDialog(String type, String quand){
        if (type == "date"){
            DatePickerDialog dialog = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if (quand == "debut"){
                        debut[0] = year;
                        debut[1] = month+1;
                        debut[2] = dayOfMonth;
                    } else if (quand == "fin") {
                        fin[0] = year;
                        fin[1] = month+1;
                        fin[2] = dayOfMonth;
                    }
                    updateDate();
                    Timestamp test;
                }
            }, 2024, 3, 1);
            dialog.show();
        } else if (type == "heure") {
            TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                    updateDate();
                }
            }, 0,0,true);
            dialog.show();
        }
    }

    public void onSaveButtonClick(View view) {
        Log.i("DIM", "saving event");

        Log.w("DIM", "nom : " + eventName.getText().toString());
        Log.w("DIM", "nb : " + participantsCount.getText().toString());
        Log.w("DIM", "type : " + eventType.getText().toString());

        if (eventName.getText().toString().equals("") || participantsCount.getText().toString().equals("") || eventType.getText().toString().equals("")) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(view.getContext(), "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        } else {
            event_modif.setName(eventName.getText().toString());
            event_modif.setParticipantsCount(Integer.parseInt(participantsCount.getText().toString()));
            event_modif.setTags(eventType.getText().toString());


            DocumentReference eventRef = database.document(event_modif.reference.getPath());

            // Mettez à jour le document avec les nouvelles données
            eventRef.set(event_modif)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // La mise à jour a réussi
                            Toast.makeText(view.getContext(), "Événement mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // La mise à jour a échoué
                            Toast.makeText(view.getContext(), "Erreur lors de la mise à jour de l'événement", Toast.LENGTH_SHORT).show();
                            Log.e("DIM", "Erreur lors de la mise à jour de l'événement", e);
                        }
                    });

            //TODO : modifier la bd plutot
            /*database.collection("events").add(event_modif)
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
                    });*/
        }
    }

    public void updateDate() {
        event_modif.StartDateTabSet(debut);
        event_modif.EndDateTabSet(fin);

        startTextView.setText("Début " + event_modif.startDateToString());
        endTextView.setText("Fin " + event_modif.endDateToString());
    }

    private void update_all_editview(Event event)
    {
        eventName.setText(event.getName());
        participantsCount.setText(String.valueOf(event.getParticipantsCount()));
        eventType.setText(event.getTags());
        startTextView.setText(event.startDateToString());
        endTextView.setText(event.endDateToString());
    }

    private void reinitEvent()
    {
        update_all_editview(event_db);
        updateDate();
        event_modif = event_db;

    }

    private int[] timestamp_to_tab(Timestamp timestamp)
    {
        Date date = timestamp.toDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int[] res = new int[] {2024, 4, 1, 0, 0, 0};
        res[0] = calendar.get(Calendar.YEAR); // Année
        res[1] = calendar.get(Calendar.MONTH) + 1; // Mois (0-based, donc on ajoute 1)
        res[2] = calendar.get(Calendar.DAY_OF_MONTH); // Jour du mois
        res[3] = calendar.get(Calendar.HOUR_OF_DAY); // Heure (format 24 heures)
        res[4] = calendar.get(Calendar.MINUTE); // Minute
        res[5] = calendar.get(Calendar.SECOND); // Seconde

        return res;

    }
}