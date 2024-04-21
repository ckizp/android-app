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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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
    private TextView TxtViewStratDate;
    private TextView TxtViewEndDate;
    private TextView TxtViewParticipantcount;
    private TextView TxtViewTags;
    private TextView TxtViewAlreadyInEvent;
    private ListView LstViewParticipants;
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
        /*user = FirebaseAuth.getInstance().getCurrentUser();*/
        TxtViewName = rootView.findViewById(R.id.eventdetails_name);
        TxtViewStratDate = rootView.findViewById(R.id.eventdetails_date_debut);
        TxtViewEndDate = rootView.findViewById(R.id.eventdetails_date_fin);
        TxtViewParticipantcount = rootView.findViewById(R.id.eventdetails_nbparticipants);
        TxtViewTags = rootView.findViewById(R.id.eventdetails_tag);
        TxtViewAlreadyInEvent = rootView.findViewById(R.id.eventdetails_alreadyin);
        LstViewParticipants = rootView.findViewById(R.id.eventdetails_participantslist);
        ImgView = rootView.findViewById(R.id.eventdetails_image);
        ButtonJoin = rootView.findViewById(R.id.eventdetails_button_participate);



        update_all();


        return rootView;
    }


    private void update_list_view(Event event)
    {
        Fragment frag = this;
        event.generateUserList(new Event.UserListCallback() {
            @Override
            public void onUserListReady(ArrayList<User> userList) {

                for (User user : userList) {
                    Log.d("DIM", "dans l'adapter, User: " + user.getEmail());
                }
                UserListAdapter customBaseAdapter = new UserListAdapter(context, userList, frag,"EventDetailsFragment");
                LstViewParticipants.setAdapter(customBaseAdapter);

                int itemHeightInSp = 50; // Taille d'un élément en SP
                int itemCount = customBaseAdapter.getCount(); // Nombre d'éléments dans la liste
                float density = context.getResources().getDisplayMetrics().density; // Obtenez la densité de l'écran en DPI
                int itemHeightInPx = (int) (itemHeightInSp * density); // Convertissez la taille de l'élément en SP en pixels
                int totalHeightInPx = itemCount * itemHeightInPx; // Calculez la hauteur totale en pixels

                ViewGroup.LayoutParams params = LstViewParticipants.getLayoutParams();
                params.height = totalHeightInPx;
                LstViewParticipants.setLayoutParams(params);


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
        TxtViewParticipantcount.setText(String.valueOf(event.getParticipantsCount()) + " participants");
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
