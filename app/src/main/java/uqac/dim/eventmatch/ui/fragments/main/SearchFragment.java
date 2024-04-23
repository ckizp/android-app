// SearchFragment.java
package uqac.dim.eventmatch.ui.fragments.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.ui.fragments.profile.EditEventFragment;

public class SearchFragment extends Fragment {
    private LinearLayout container;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private ArrayList<Event> eventList;
    private View rootView;
    private String selected;
    private HashMap<String, Integer> tagBackgrounds;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        this.container = rootView.findViewById(R.id.container);
        eventList = new ArrayList<>();

        // Initialise la HashMap pour associer chaque tag à son drawable de fond
        tagBackgrounds = new HashMap<>();
        tagBackgrounds.put("sport", R.drawable.card_background_sport);
        tagBackgrounds.put("musique", R.drawable.card_background_musique);
        tagBackgrounds.put("cinéma", R.drawable.card_background_cinema);
        tagBackgrounds.put("jeux vidéo", R.drawable.card_background_jeux_video);
        tagBackgrounds.put("culture", R.drawable.card_background_culture);
        tagBackgrounds.put("art", R.drawable.card_background_art);
        tagBackgrounds.put("cuisine", R.drawable.card_background_cuisine);
        tagBackgrounds.put("réunion et rencontre", R.drawable.card_background_reunion_et_rencontre);
        tagBackgrounds.put("autre", R.drawable.card_background_autre);

        Spinner filter = rootView.findViewById(R.id.spinner1);
        String[] items = new String[]{"aucun", "sport", "musique", "cinéma", "jeux vidéo", "culture", "art", "cuisine", "réunion et rencontre", "autre"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_spinner_dropdown_item, items);

        filter.setAdapter(adapter);

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selected = filter.getSelectedItem().toString();
                filterEvents(selected);
                Log.d("DIM", "Selected: " + selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // Récupération des événements depuis Firestore
        database.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("DIM", document.getId() + " => " + document.getData());

                        // Récupérer les informations concernant l'événement courant
                        String name = document.getString("name");
                        Timestamp startDate = document.getTimestamp("startDate");
                        Timestamp endDate = document.getTimestamp("endDate");
                        int participantsCount = document.getDouble("participantsCount").intValue();
                        String tags = document.getString("tags");
                        List<DocumentReference> partlist = (List<DocumentReference>) document.get("participants");
                        String imageUrl = document.getString("imageDataUrl");
                        DocumentReference owner = document.getDocumentReference("owner");
                        LatLng location = new LatLng(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                        String description = document.getString("description");

                        Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl, owner, location, description);
                        eventList.add(event);
                    }

                    filterEvents(selected);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return rootView;
    }

    private void filterEvents(String filter) {
        container.removeAllViews();
        for (Event event : eventList) {
            if (filter.equals("aucun") || event.getTags().contains(filter)) {
                View eventView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.event_item, container, false);

                // Récupération des vues
                ImageView imageView = eventView.findViewById(R.id.image_view);
                TextView eventNameTextView = eventView.findViewById(R.id.text_event_name);
                TextView participantsCountTextView = eventView.findViewById(R.id.text_participants_count);
                TextView tagsTextView = eventView.findViewById(R.id.layout_tags);

                // Remplissage des vues avec les données de l'événement
                eventNameTextView.setText(event.getName());
                participantsCountTextView.setText("Participant : "+String.valueOf(event.getParticipantsCount()));
                tagsTextView.setText("Type d'évènement : "+event.getTags());

                // Chargement du fond en fonction du tag de l'événement
                if (tagBackgrounds.containsKey(event.getTags().toLowerCase())) {
                    eventView.setBackgroundResource(tagBackgrounds.get(event.getTags().toLowerCase()));
                } else {
                    // Si le tag n'est pas trouvé, utilise le fond par défaut
                    eventView.setBackgroundResource(R.drawable.card_background);
                }

                // Chargement de l'image de l'événement
                event.load_image(imageView, storage);

                container.addView(eventView);

                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DIM", "Selected event: " + event.getName());

                        Fragment fragment = new EventDetailsFragment(event);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment).commit();
                    }
                });
            }
        }
    }
}
