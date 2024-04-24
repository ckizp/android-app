package uqac.dim.eventmatch.ui.fragments.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.models.User;

public class SearchFragment extends Fragment {
    private LinearLayout container;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private ArrayList<Event> eventList;
    private ArrayList<DocumentReference> eventListRef;

    private ArrayList<String> bannerTexts;
    private View rootView;
    private String selected;
    private HashMap<String, Integer> tagBackgrounds;
    private HashMap<String, Integer> tagDrawables;
    private FirebaseUser user;

    private TextView bannerTextView;
    private int currentBannerIndex = 0;
    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        this.container = rootView.findViewById(R.id.container);
        eventList = new ArrayList<>();
        eventListRef = new ArrayList<>();

        bannerTextView = rootView.findViewById(R.id.banner_text_view);
        bannerTexts = new ArrayList<>();

        // Initialise la HashMap pour associer chaque tag à son drawable de fond
        tagBackgrounds = new HashMap<>();
        tagBackgrounds.put("sport", R.drawable.card_background_sport);
        tagBackgrounds.put("musique", R.drawable.card_background_musique);
        tagBackgrounds.put("cinéma", R.drawable.card_background_cinema);
        tagBackgrounds.put("jeux", R.drawable.card_background_jeux);
        tagBackgrounds.put("culture", R.drawable.card_background_culture);
        tagBackgrounds.put("art", R.drawable.card_background_art);
        tagBackgrounds.put("cuisine", R.drawable.card_background_cuisine);
        tagBackgrounds.put("réunion", R.drawable.card_background_rencontre);
        tagBackgrounds.put("autre", R.drawable.card_background_autre);

        // Initialise la HashMap pour associer chaque tag à son drawable
        tagDrawables = new HashMap<>();
        tagDrawables.put("sport", R.drawable.sport);
        tagDrawables.put("musique", R.drawable.musique);
        tagDrawables.put("cinéma", R.drawable.cinema);
        tagDrawables.put("jeux", R.drawable.jeux);
        tagDrawables.put("culture", R.drawable.culture);
        tagDrawables.put("art", R.drawable.art);
        tagDrawables.put("cuisine", R.drawable.cuisine);
        tagDrawables.put("réunion", R.drawable.rencontre);
        tagDrawables.put("autre", R.drawable.autre);


        Spinner filter = rootView.findViewById(R.id.spinner1);
        String[] items = new String[]{"aucun", "sport", "musique", "cinéma", "jeux", "culture", "art", "cuisine", "réunion", "autre"};
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
                        GeoPoint location = new GeoPoint(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                        String description = document.getString("description");

                        Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl, owner, location, description);
                        event.referenceset(document.getReference());
                        eventList.add(event);
                        eventListRef.add(document.getReference());
                    }

                    filterEvents(selected);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        // Récupération des textes de la bannière depuis Firestore
        database.collection("feedbacks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("DIM", document.getId() + " => " + document.getData());

                        // Récupérer le texte de la bannière
                        String text = document.getString("feedback");
                        bannerTexts.add(text);
                    }

                    // Passer les textes de la bannière au fragment de la bannière
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("bannerTexts", bannerTexts);
                    BannerFragment bannerFragment = new BannerFragment();
                    bannerFragment.setArguments(bundle);

                    // Remplacer le fragment de la bannière dans le layout
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.banner_container, bannerFragment);
                    transaction.commit();

                } else {
                    Log.d("DIM", "Error getting documents: ", task.getException());
                }
            }
        });
        return rootView;
    }

    private void filterEvents(String string_filter) {
        container.removeAllViews();
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            if (string_filter ==  null || string_filter.equals("aucun") || event.getTags().contains(string_filter)) { //TODO y'a une NullPointerException à des moments ça crash je crois c'est fix mais pas sur
                View eventView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.event_item, container, false);

                // Récupération des vues
                ImageView imageView = eventView.findViewById(R.id.image_view);
                TextView eventNameTextView = eventView.findViewById(R.id.text_event_name);
                TextView participantsCountTextView = eventView.findViewById(R.id.text_participants_count);
                ImageView tagsImageView = eventView.findViewById(R.id.layout_tags);
                TextView startDateTextView = eventView.findViewById(R.id.start_date);

                Button detailsButton = eventView.findViewById(R.id.button_details);
                Button favButton = eventView.findViewById(R.id.button2); //TODO : rename en fonction de ce qu'on fait

                // Remplissage des vues avec les données de l'événement
                eventNameTextView.setText(event.getName());
                participantsCountTextView.setText("Participant : "+String.valueOf(event.getParticipantsCount()));

                // Conversion du timestamp en format de date lisible
                String startDate = DateFormat.format("dd/MM/yyyy", event.getStartDate().toDate()).toString();
                startDateTextView.setText("Début de l'évènement : "+startDate);


                // Chargement du fond en fonction du tag de l'événement
                if (tagBackgrounds.containsKey(event.getTags().toLowerCase())) {
                    eventView.setBackgroundResource(tagBackgrounds.get(event.getTags().toLowerCase()));
                } else {
                    // Si le tag n'est pas trouvé, utilise le fond par défaut
                    eventView.setBackgroundResource(R.drawable.card_background);
                }

                // Chargement de l'image de l'événement
                event.load_image(imageView, storage);

                // Chargement de l'image associée au tag
                if (tagDrawables.containsKey(event.getTags().toLowerCase())) {
                    tagsImageView.setImageResource(tagDrawables.get(event.getTags().toLowerCase()));
                } else {
                    // Si le tag n'est pas trouvé, utilise l'image par défaut
                    tagsImageView.setImageResource(R.drawable.default_image);
                }

                //Listener des boutons
                detailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DIM", "Selected event: " + event.getName());
                        Fragment fragment = new EventDetailsFragment(event);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment).commit();
                    }
                });

                int finalI = i;
                favButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DIM", "Selected event: " + event.getName());
                        //add to firestore
                        Toast.makeText(getContext(), "Event added to favorites", Toast.LENGTH_SHORT).show();

                        DocumentReference doc = eventListRef.get(finalI);
                        final ArrayList<DocumentReference> userFav = new ArrayList();

                        database.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    for (DocumentReference ref : (List<DocumentReference>) document.get("favorites")) {
                                        userFav.add(ref);
                                    }
                                    if(!userFav.contains(doc)){
                                        userFav.add(doc);
                                    }
                                    database.collection("users").document(user.getUid()).update("favorites", userFav);
                                }
                            }
                        });




                    }
                });

                container.addView(eventView);

                /*
                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DIM", "Selected event: " + event.getName());

                        Fragment fragment = new EventDetailsFragment(event);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment).commit();
                    }
                });*/
            }
        }
    }
}
