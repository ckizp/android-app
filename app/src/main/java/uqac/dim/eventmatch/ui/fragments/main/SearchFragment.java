package uqac.dim.eventmatch.ui.fragments.main;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.adapters.EventListAdapter;
import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot, Ibraguim Temirkhaev
 */
public class SearchFragment extends Fragment {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private ListView eventListView;
    private FirebaseFirestore database;
    private ArrayList<Event> eventList;
    private View rootView;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public SearchFragment() {

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
                                Timestamp startDate = document.getTimestamp("startDate");
                                Timestamp endDate = document.getTimestamp("endDate");
                                int participantsCount = document.getDouble("participantsCount").intValue();
                                String tags = document.getString("tags");
                                List<DocumentReference> partlist = (List<DocumentReference>) document.get("participants");
                                String imageUrl = document.getString("imageDataUrl");

                                Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl);
                                eventList.add(event);
                            }
                            EventListAdapter customBaseAdapter = new EventListAdapter(rootView.getContext(), eventList);
                            eventListView.setAdapter(customBaseAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        return rootView;
    }
}