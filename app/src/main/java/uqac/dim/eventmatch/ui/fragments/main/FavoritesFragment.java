package uqac.dim.eventmatch.ui.fragments.main;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.List;
import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.FavoritesAdapter;
import uqac.dim.eventmatch.adapters.MyEventListAdapter;
import uqac.dim.eventmatch.models.Event;


public class FavoritesFragment extends Fragment {
    private ArrayList<DocumentReference> favoritesList;
    private FirebaseFirestore database;
    private FirebaseUser user;
    private ListView eventListView;
    private ArrayList<Event> eventList;
    private View rootView;
    public FavoritesFragment() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        favoritesList = new ArrayList<>();



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        eventListView = rootView.findViewById(R.id.my_favorits);

        eventList = new ArrayList<Event>();

        database.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    for (DocumentReference ref : (List<DocumentReference>) document.get("favorites")) {
                        favoritesList.add(ref);
                    }
                }
                if(!favoritesList.isEmpty()) {
                    for(DocumentReference ref : favoritesList){
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    String name = document.getString("name");
                                    Timestamp startDate = document.getTimestamp("startDate");
                                    Timestamp endDate = document.getTimestamp("endDate");
                                    int participantsCount = document.getDouble("participantsCount").intValue();
                                    String tags = document.getString("tags");
                                    List<DocumentReference> partlist = (List<DocumentReference>) document.get("participants");
                                    String imageUrl = document.getString("imageDataUrl");
                                    GeoPoint location = new GeoPoint(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());
                                    String description = document.getString("description");
                                    DocumentReference owner = document.getDocumentReference("owner");
                                    Event event = new Event(name, endDate, startDate, participantsCount, tags, partlist, imageUrl,owner, location, description);

                                    eventList.add(event);
                                }
                                FavoritesAdapter customBaseAdapter = new FavoritesAdapter(rootView.getContext(), eventList);
                                Log.i("FAVORIS", "eventList size : " + eventList.size());
                                eventListView.setAdapter(customBaseAdapter);
                                Log.i("FAVORIS", "eventListView size : " + eventListView.getCount());
                            }
                        });
                    }

                }

            }
        });

        return rootView;
    }
}