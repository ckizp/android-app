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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.SpinnerAdapter;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.models.SpinnerItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private SupportMapFragment mapFragment;
    private GeoPoint lastClickedLocation;
    private TextView description;
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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        description = view.findViewById(R.id.event_description);

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

        debut = dateTransform(new Date());
        fin = dateTransform(new Date());

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
        List<SpinnerItem> spinnerItems = new ArrayList<>();
        // Ajoutez les autres éléments ici
        spinnerItems.add(new SpinnerItem(R.drawable.other, "autre"));
        spinnerItems.add(new SpinnerItem(R.drawable.sport, "sport"));
        spinnerItems.add(new SpinnerItem(R.drawable.music, "musique"));
        spinnerItems.add(new SpinnerItem(R.drawable.movie, "cinéma"));
        spinnerItems.add(new SpinnerItem(R.drawable.game, "jeux vidéo"));
        spinnerItems.add(new SpinnerItem(R.drawable.culture, "culture"));
        spinnerItems.add(new SpinnerItem(R.drawable.art, "art"));
        spinnerItems.add(new SpinnerItem(R.drawable.cooking, "cuisine"));
        spinnerItems.add(new SpinnerItem(R.drawable.meetup, "réunion et rencontre"));

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), spinnerItems);
        eventType.setAdapter(adapter);

        // Initialisation de la carte
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            // Suppression de l'ancien marqueur
                            googleMap.clear();
                            // Ajout d'un marqueur à la position cliquée
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            // Caméra sur la position cliquée
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            // Enregistrement de la position cliquée
                            lastClickedLocation = new GeoPoint(latLng.latitude, latLng.longitude);
                        }
                    });
                }
            });
        } else {
            Log.e(TAG, "Error: Map Fragment is null");
        }

        return view;
    }

    // Méthode pour transformer la date en liste d'entiers
    public int[] dateTransform(Date date){
        int[] dateTab = new int[6];
        dateTab[0] = date.getYear() + 1900;
        dateTab[1] = date.getMonth() + 1;
        dateTab[2] = date.getDate();
        dateTab[3] = date.getHours();
        dateTab[4] = date.getMinutes();
        dateTab[5] = date.getSeconds();
        return dateTab;
    }

    private void openDialog(String type, String quand){
        Calendar cal = Calendar.getInstance();
        if (type.equals("date")){
            DatePickerDialog dialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if (quand.equals("debut")){
                        debut[0] = year;
                        debut[1] = month + 1;
                        debut[2] = dayOfMonth;
                    } else if (quand.equals("fin")) {
                        if (new GregorianCalendar(year, month, dayOfMonth).before(new GregorianCalendar(debut[0], debut[1] - 1, debut[2]))) {
                            Toast.makeText(view.getContext(), "La date de fin ne peut pas être antérieure à la date de début.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fin[0] = year;
                        fin[1] = month + 1;
                        fin[2] = dayOfMonth;
                    }
                    updateDate();
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            // Set minimum date to current date
            dialog.getDatePicker().setMinDate(cal.getTimeInMillis());

            dialog.show();
        } else if (type.equals("heure")) {
            TimePickerDialog dialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (quand.equals("debut")){
                        debut[3] = hourOfDay;
                        debut[4] = minute;
                    } else if (quand.equals("fin")) {
                        if (new GregorianCalendar(fin[0], fin[1] - 1, fin[2], hourOfDay, minute).before(new GregorianCalendar(debut[0], debut[1] - 1, debut[2], debut[3], debut[4]))) {
                            Toast.makeText(view.getContext(), "L'heure de fin ne peut pas être antérieure à l'heure de début.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fin[3] = hourOfDay;
                        fin[4] = minute;
                    }
                    updateDate();
                }
            }, 0, 0, true);
            dialog.show();
        }
    }


    public void onSaveButtonClick(View view) {
        Log.i("DIM", "saving event");

        Log.w("DIM", "nom : " + eventName.getText().toString());
        Log.w("DIM", "nb : " + participantsCount.getText().toString());
        Log.w("DIM", "type : " + eventType.getSelectedItem().toString());

        if (eventName.getText().toString().equals("") || participantsCount.getText().toString().equals("") || eventType.getSelectedItem().toString().equals("") || eventImageView.getDrawable() == null) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(view.getContext(), "Erreur, merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
        // vérification que l'heure de fin est bien après l'heure de début
        else if (new GregorianCalendar(fin[0], fin[1] - 1, fin[2], fin[3], fin[4]).before(new GregorianCalendar(debut[0], debut[1] - 1, debut[2], debut[3], debut[4]))){
            Log.w("DIM", "L'heure de fin ne peut pas être antérieure à l'heure de début");
            Toast.makeText(view.getContext(), "La date de fin ne peut pas être antérieure à la date de début", Toast.LENGTH_SHORT).show();
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

            event.setDescription(description.getText().toString());

            if (lastClickedLocation != null) {
                event.setLocation(new GeoPoint(lastClickedLocation.getLatitude(), lastClickedLocation.getLongitude()));
            } else {
                event.setLocation(new GeoPoint(0, 0));
            }


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
            clearAllFields();
        }
    }

    public void onClearButtonClick(View view) {
        Log.i("DIM", "Clear des entrées utilisateurs");
        clearAllFields();
        Log.w("DIM", "Champs vidés");
        Toast.makeText(view.getContext(), "Champs vidés", Toast.LENGTH_SHORT).show();
    }

    // Méthode pour vider les champs
    public void clearAllFields() {
        eventName.setText("");
        participantsCount.setText("");
        eventImageView.setImageResource(0);
        eventType.setSelection(0);
        debut = dateTransform(new Date());
        fin = dateTransform(new Date());
        updateDate();
        lastClickedLocation = null;
        description.setText("");
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

