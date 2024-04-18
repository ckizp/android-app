package uqac.dim.eventmatch.ui.fragments.main;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot, Ibraguim Temirkhaev
 */
public class CreateFragment extends Fragment {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private Event event;
    private EditText eventName;
    private EditText participantsCount;
    private Spinner eventType;
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
    private Button saveButton;
    private Button clearButton;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public CreateFragment() {

    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_create, container, false);

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
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

        eventImageView.setVisibility(View.INVISIBLE);

        eventImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                eventImageView.setVisibility(View.VISIBLE);
            }
        });

        saveButton = view.findViewById(R.id.event_button_save);
        clearButton = view.findViewById(R.id.event_button_clear);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick(saveButton);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearButtonClick(clearButton);
            }
        });

        //Choix du type d'événement
        eventType = view.findViewById(R.id.event_type);
        String[] items = new String[]{"autre", "sport", "musique", "cinéma", "jeux vidéo", "culture", "art", "cuisine", "réunion et rencontre"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        //événement par défaut
        eventType.setSelection(0);
        eventType.setAdapter(adapter);


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

    public void onSaveButtonClick(View view) {
        Log.i("DIM", "saving event");

        Log.w("DIM", "nom : " + eventName.getText().toString());
        Log.w("DIM", "nb : " + participantsCount.getText().toString());
        Log.w("DIM", "type : " + eventType.getSelectedItem().toString());

        if (eventName.getText().toString().equals("") || participantsCount.getText().toString().equals("") || eventType.getSelectedItem().toString().equals("")) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(view.getContext(), "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        } else {
            event.setName(eventName.getText().toString());
            event.setParticipantsCount(Integer.parseInt(participantsCount.getText().toString()));
            event.setTags(eventType.getSelectedItem().toString());



            BitmapDrawable drawable = (BitmapDrawable) eventImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();


            StorageReference storageRef = storage.getReference();
            String path = "events_images/"+ UUID.randomUUID().toString()+".jpg";
            StorageReference imagesRef = storageRef.child(path);
            UploadTask uploadTask = imagesRef.putFile(filePath);

            // Gestion du succès ou de l'échec du téléversement
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d("DIM","envois de l'image réussi");
                // Vous pouvez récupérer l'URL de téléchargement ici et l'enregistrer dans Firestore si nécessaire
            }).addOnFailureListener(exception -> {
                // Erreur lors du téléversement du fichier
                Log.d("DIM","envois de l'image raté");
            });
            //TODO : Gérer les cas où il n'y a pas d'image ou bien si ell ne s'envois pas.


            event.setImageDataUrl(path);


            event.setName(eventName.getText().toString());
            event.setParticipantsCount(Integer.parseInt(participantsCount.getText().toString()));
            event.setTags(eventType.getSelectedItem().toString());

            List<DocumentReference> partipantsliste = new ArrayList<DocumentReference>();

            String userID = user.getUid();
            DocumentReference userRef = database.collection("users").document(userID);
            partipantsliste.add(userRef);
            event.setParticipants(partipantsliste);
            event.setOwner(userRef);

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

    public void onClearButtonClick(View view) {
        Log.i("DIM", "Clear des entrées utilisateurs");

        eventName.setText("");
        participantsCount.setText("");
        eventType.setSelection(0);
        debut = new int[] {2024, 4, 1, 0, 0, 0};
        fin = new int[] {2024, 4, 1, 0, 0, 0};
        updateDate();

        Log.w("DIM", "Champs vidés");
        Toast.makeText(view.getContext(), "Champs vidés", Toast.LENGTH_SHORT).show();
    }

    public void updateDate() {
        event.StartDateTabSet(debut);
        event.EndDateTabSet(fin);

        startTextView.setText("Début " + event.startDateToString());
        endTextView.setText("Fin " + event.endDateToString());
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