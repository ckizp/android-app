package uqac.dim.eventmatch.ui.fragments.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.MyEventListAdapter;
import uqac.dim.eventmatch.models.Event;

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
                                    GeoPoint location = new GeoPoint(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                                    String description = document.getString("description");
                                    Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl,owner, location, description);

                                    event.referenceset(document.getReference());
                                    eventList.add(event);
                                }
                            }
                            MyEventListAdapter customBaseAdapter = new MyEventListAdapter(rootView.getContext(), eventList);
                            eventListView.setAdapter(customBaseAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        
        return rootView;
    }
}