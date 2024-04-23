package uqac.dim.eventmatch.ui.fragments.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.EventListAdapter;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.ui.fragments.main.ProfileFragment;

public class MyEventsFragment extends Fragment {

    private ListView eventListView;
    private FirebaseFirestore database;
    private ArrayList<Event> eventList;
    private View rootView;
    private FirebaseUser user;


    public MyEventsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();


        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_my_events, container, false);

        database = FirebaseFirestore.getInstance();

        eventListView = rootView.findViewById(R.id.my_events_list);
        eventList = new ArrayList<Event>();

        database.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DIM", document.getId() + " => " + document.getData());

                                String userID = user.getUid();
                                DocumentReference userRef = database.collection("users").document(userID);
                                DocumentReference owner = document.getDocumentReference("owner");
                                Log.d("DIM", owner.getPath() + " == " + userRef.getPath());

                                if (owner.getPath().equals(userRef.getPath()))
                                {
                                    Log.d("DIM", owner.getPath() + " successful owner ");
                                    // On récupère les informations concernant l'événement courant.
                                    String name = document.getString("name");
                                    Timestamp startDate = document.getTimestamp("startDate");
                                    Timestamp endDate = document.getTimestamp("endDate");
                                    int participantsCount = document.getDouble("participantsCount").intValue();
                                    String tags = document.getString("tags");
                                    List<DocumentReference> partlist = (List<DocumentReference>) document.get("participants");
                                    String imageUrl = document.getString("imageDataUrl");
                                    LatLng location = new LatLng(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                                    String description = document.getString("description");
                                    Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl,owner, location, description);

                                    event.referenceset(document.getReference());
                                    eventList.add(event);
                                }
                            }
                            EventListAdapter customBaseAdapter = new EventListAdapter(rootView.getContext(), eventList);
                            eventListView.setAdapter(customBaseAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'événement sélectionné à partir de la position dans la liste
                Event selectedEvent = eventList.get(position);

                Log.d("DIM", "Selected event: " + selectedEvent.getName());

                Fragment fragment = new EditEventFragment(selectedEvent);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment).commit();

                // Passer à l'écran de modification de l'événement en transmettant les données de l'événement
                // Vous pouvez utiliser une interface pour communiquer les données à l'activité contenant le fragment
                // ou vous pouvez démarrer directement une nouvelle activité pour l'édition de l'événement.
                // Dans cet exemple, nous supposerons l'utilisation d'une interface.

                // Si vous utilisez une interface, vous pouvez appeler une méthode de l'activité contenant le fragment
                // pour démarrer l'écran de modification en passant l'événement sélectionné.
                /*if (getActivity() instanceof OnEventEditListener) {
                    ((OnEventEditListener) getActivity()).onEditEvent(selectedEvent);
                }*/
            }
        });



        return rootView;
    }
}