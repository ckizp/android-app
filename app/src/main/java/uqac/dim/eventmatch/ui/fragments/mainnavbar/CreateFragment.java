package uqac.dim.eventmatch.ui.fragments.mainnavbar;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uqac.dim.eventmatch.ui.activities.MainActivity;
import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot, Ibraguim Temirkhaev
 */
public class CreateFragment extends Fragment {
    private FirebaseFirestore database;
    private Event event;
    private EditText eventName;
    private EditText participantsCount;
    private EditText eventType;
    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private ImageView eventImageView;
    private Button imageButton;
    private int[] debut;
    private int[] fin;
    private TextView startTextView;
    private TextView endTextView;
    private View view;
    private Button button_save;
    private Button button_clear;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    public CreateFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create, container, false);
        super.onCreate(savedInstanceState);

        database = FirebaseFirestore.getInstance();
        event = new Event();

        eventName = view.findViewById(R.id.event_name);
        participantsCount = view.findViewById(R.id.event_nb_participants);
        eventType = view.findViewById(R.id.event_type);

        startDateButton = view.findViewById(R.id.event_choix_date_debut);
        endDateButton = view.findViewById(R.id.event_choix_date_fin);
        startTimeButton = view.findViewById(R.id.event_choix_heure_debut);
        endTimeButton = view.findViewById(R.id.event_choix_heure_fin);
        startTextView = view.findViewById(R.id.affichage_debut);
        endTextView = view.findViewById(R.id.affichage_fin);
        imageButton = view.findViewById(R.id.btn_import_image);
        eventImageView = view.findViewById(R.id.image_view);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Sélectionner une image"),
                        PICK_IMAGE_REQUEST);
            }
        });

        button_save = view.findViewById(R.id.event_button_save);
        button_clear = view.findViewById(R.id.event_button_clear);

        debut = new int[] {2024, 4, 1, 0, 0, 0};
        fin = new int[] {2024, 4, 1, 0, 0, 0};

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

    private void openDialog(String type, String quand){
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
                    updateDate();
                    Timestamp test;
                }
            }, 2024, 3, 1);
            dialog.show();
        } else if (type == "heure") {
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
                    updateDate();
                }
            }, 0,0,true);
            dialog.show();
        }
    }

    public void saveEvent(View view) {
        Log.i("DIM", "saving event");

        Log.w("DIM", "nom : " + eventName.getText().toString());
        Log.w("DIM", "nb : " + participantsCount.getText().toString());
        Log.w("DIM", "type : " + eventType.getText().toString());

        if (eventName.getText().toString().equals("") || participantsCount.getText().toString().equals("") || eventType.getText().toString().equals("")) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(view.getContext(), "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
        else {
            event.setName(eventName.getText().toString());
            event.setParticipantsCount(Integer.parseInt(participantsCount.getText().toString()));
            event.setTags(eventType.getText().toString());

            BitmapDrawable drawable = (BitmapDrawable) eventImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            event.setImageData(imageData);
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

    public void clearEvent(View view) {
        Log.i("DIM", "Clear des entrées utilisateurs");

        eventName.setText("");
        participantsCount.setText("");
        eventType.setText("");
        debut = new int[] {2024, 4, 1, 0, 0, 0};
        fin = new int[] {2024, 4, 1, 0, 0, 0};
        updateDate();

        Log.w("DIM", "Champs vidés");
        Toast.makeText(view.getContext(), "Champs vidés", Toast.LENGTH_SHORT).show();
    }

    public void updateDate() {
        event.TabsetDate_start(debut);
        event.TabsetDate_end(debut);

        startTextView.setText("Début " + event.Date_startString());
        endTextView.setText("Fin " + event.Date_endString());
    }

    public void backMenu(View view) {
        Toast.makeText(view.getContext(), "Retour Main", Toast.LENGTH_SHORT).show();
        Intent startActivity = new Intent(view.getContext(), MainActivity.class);
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startActivity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                        getContext().getContentResolver(),
                                filePath);
                eventImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}