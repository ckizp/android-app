package uqac.dim.eventmatch.ui.fragments.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.SupportErrorDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.UserListAdapter;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.models.User;

/*/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {
    private View rootView;
    private Event event;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private TextView TxtViewName;
    private TextView TxtViewDescription;
    private TextView TxtViewStratDate;
    private TextView TxtViewEndDate;
    private TextView TxtViewParticipantcount;
    private TextView TxtViewTags;
    private TextView TxtViewAlreadyInEvent;
    private LinearLayout LstViewParticipants;
    private ImageView ImgView;
    private Button ButtonJoin;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public EventDetailsFragment(Event e){
        event = e.Copy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_event_details, container, false);

        context = rootView.getContext();

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        TxtViewName = rootView.findViewById(R.id.eventdetails_name);
        TxtViewDescription = rootView.findViewById(R.id.eventdetails_description);
        TxtViewStratDate = rootView.findViewById(R.id.eventdetails_date_debut);
        TxtViewEndDate = rootView.findViewById(R.id.eventdetails_date_fin);
        TxtViewParticipantcount = rootView.findViewById(R.id.eventdetails_participants);
        TxtViewTags = rootView.findViewById(R.id.eventdetails_tag);
        TxtViewAlreadyInEvent = rootView.findViewById(R.id.eventdetails_alreadyin);
        LstViewParticipants = rootView.findViewById(R.id.participants_list);
        ImgView = rootView.findViewById(R.id.eventdetails_image);
        ButtonJoin = rootView.findViewById(R.id.eventdetails_button_participate);

        update_participation();
        update_all();


        return rootView;
    }

    private void update_participation()
    {
        boolean In = false;
        String userstring = "users/" + user.getUid();
        for (DocumentReference useref : event.getParticipants()) {
            if (userstring.equals(useref.getPath()))
            {
                In = true;
            }
        }
        if (In)
        {

            TxtViewAlreadyInEvent.setVisibility(View.VISIBLE);
            ButtonJoin.setVisibility(View.GONE);
            ButtonJoin.setOnClickListener(null);
        }
        else
        {
            DocumentReference userRef = database.document(userstring);

            TxtViewAlreadyInEvent.setVisibility(View.GONE);
            ButtonJoin.setVisibility(View.VISIBLE);
            ButtonJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RejoindreEvent(userRef);
                }
            });
        }

    }

    private void RejoindreEvent(DocumentReference userRef) {
        List<DocumentReference> participantslist = new ArrayList<DocumentReference>() {};
        for (DocumentReference docref: (event.getParticipants())) {
            participantslist.add(docref);
        }
        participantslist.add(userRef);
        event.setParticipants(participantslist);
        // Mettez à jour le document avec les nouvelles données
        event.referenceOfthisEvent().set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La mise à jour a réussi
                        Toast.makeText(getContext(),getString(R.string.toast_join_success_event),Toast.LENGTH_SHORT).show();
                        update_participation();
                        update_list_view(event);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La mise à jour a échoué
                        Toast.makeText(getContext(), getString(R.string.toast_modif_failure_event), Toast.LENGTH_SHORT).show();
                        Log.e("DIM", "Erreur lors de la mise à jour de l'événement", e);
                    }
                });

    }

    private void update_list_view(Event event) {
        Fragment frag = this;
        event.generateUserList(new Event.UserListCallback() {
            @Override
            public void onUserListReady(ArrayList<User> userList) {
                LstViewParticipants.removeAllViews(); // Supprimer les vues précédentes

                for (User user : userList) {
                    TextView textView = new TextView(context);
                    textView.setText(user.getUsername()+" ");
                    LstViewParticipants.addView(textView); // Ajouter le TextView à la LinearLayout
                }
                TxtViewParticipantcount.setText("Participants ("+String.valueOf(userList.size())+"/"+String.valueOf(event.getParticipantsCount()) + ") :  ");
            }
        });
    }

    void update_image_view()
    {
        event.load_image(ImgView,storage);
    }
    void update_txt_view()
    {
        TxtViewName.setText(event.getName());
        TxtViewDescription.setText(event.getDescription());
        TxtViewTags.setText(event.getTags());
        TxtViewStratDate.setText(event.startDateToString());
        TxtViewEndDate.setText(event.endDateToString());
    }
    void update_all()
    {
        update_txt_view();
        update_image_view();
        update_list_view(event);
    }
}
