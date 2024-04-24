// MyEventListAdapter.java
package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.ui.fragments.main.EventDetailsFragment;


public class FavoritesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> eventList;

    private FirebaseStorage storage;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public FavoritesAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        Event event = eventList.get(position);

        // Récupération des vues
        TextView eventNameTextView = convertView.findViewById(R.id.text_event_name);
        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView eventDateTextView = convertView.findViewById(R.id.start_date);
        TextView participantsTextView = convertView.findViewById(R.id.text_participants_count);
        Button DetailsButton = convertView.findViewById(R.id.button_details);
        Button EditButton = convertView.findViewById(R.id.button2);
        EditButton.setText("Enlever");





        event.load_image(imageView,storage);
        eventNameTextView.setText(event.getName());
        String startDate = DateFormat.format("dd/MM/yyyy", event.getStartDate().toDate()).toString();
        eventDateTextView.setText("Début de l'évènement : "+startDate);

        participantsTextView.setText("Participant : "+ String.valueOf(event.getParticipantsCount()));
        //TODO : Changer le fond du truc

        DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EventDetailsFragment(event);
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference userReference = database.collection("users").document(user.getUid());
                userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            List<DocumentReference> favorites = (List<DocumentReference>) document.get("favorites");
                            favorites.remove(position);
                            eventList.remove(position);
                            userReference.update("favorites", favorites);
                        }
                    }
                });
            }



        });



        return convertView;
    }
}