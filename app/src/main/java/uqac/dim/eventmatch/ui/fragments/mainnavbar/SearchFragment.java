package uqac.dim.eventmatch.ui.fragments.mainnavbar;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import uqac.dim.eventmatch.adapters.EventListAdapter;
import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot, Ibraguim Temirkhaev
 */
public class SearchFragment extends Fragment {
    private ListView eventListView;
    private FirebaseFirestore database;
    private ArrayList<Event> eventList;
    private View rootView;

    public SearchFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_search, container, false);


        database = FirebaseFirestore.getInstance();

        eventListView = rootView.findViewById(R.id.list_events);
        eventList = new ArrayList<Event>();

        database.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DIM", document.getId() + " => " + document.getData());

                                // On récupère les informations concernant l'événement courant.
                                String name = document.getString("name");
                                Timestamp startDate = document.getTimestamp("date_start");
                                Timestamp endDate = document.getTimestamp("date_end");
                                int participantsCount = document.getDouble("nb_participants").intValue();
                                String type = document.getString("type");


                                Event event = new Event(name, endDate, startDate, participantsCount, type, null);
                                eventList.add(event);
                            }
                            EventListAdapter eventListAdapter = new EventListAdapter(rootView.getContext(), eventList);
                            eventListView.setAdapter(eventListAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        return rootView;
    }
}