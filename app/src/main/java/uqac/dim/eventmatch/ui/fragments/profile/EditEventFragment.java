package uqac.dim.eventmatch.ui.fragments.profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {

    private View rootView;
    private Event event;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private EditText eventName;
    private EditText participantsCount;
    private EditText eventType;
    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private int[] debut;
    private int[] fin;
    private TextView startTextView;
    private TextView endTextView;
    private Button saveButton;
    private Button clearButton;

    public EditEventFragment() {
        // Required empty public constructor
    }

    public EditEventFragment(Event e){
        event = e;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);

        /*
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();*/

        eventName = rootView.findViewById(R.id.editevent_name);
        participantsCount = rootView.findViewById(R.id.editevent_nb_participants);
        eventType = rootView.findViewById(R.id.editevent_type);

        startDateButton = rootView.findViewById(R.id.editevent_choix_date_debut);
        endDateButton = rootView.findViewById(R.id.editevent_choix_date_fin);
        startTimeButton = rootView.findViewById(R.id.editevent_choix_heure_debut);
        endTimeButton = rootView.findViewById(R.id.editevent_choix_heure_fin);
        startTextView = rootView.findViewById(R.id.editevent_affichage_debut);
        endTextView = rootView.findViewById(R.id.editevent_affichage_fin);

        update_all_editview();



        return rootView;
    }

    private void update_all_editview()
    {
        eventName.setText(event.getName());
        participantsCount.setText(String.valueOf(event.getParticipantsCount()));
        eventType.setText(event.getTags());
        startTextView.setText(event.startDateToString());
        endTextView.setText(event.endDateToString());
    }
}