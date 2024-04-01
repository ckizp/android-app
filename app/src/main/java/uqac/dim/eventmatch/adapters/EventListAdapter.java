package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.models.User;

/**
 *
 * @version 1.0 1 Apr 2024
 * @author Kyllian Hot
 */
public class EventListAdapter extends BaseAdapter {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> eventList;
    private FirebaseFirestore database;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public EventListAdapter(Context ctx, ArrayList<Event> eventList){
        context = ctx;
        this.eventList = eventList;
        inflater = LayoutInflater.from(ctx);
        database = FirebaseFirestore.getInstance();
    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view,null);

        Event currentEvent = eventList.get(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.liste_nom);
        TextView startDateTextView = (TextView) convertView.findViewById(R.id.liste_debut);
        TextView endDateTextView = (TextView) convertView.findViewById(R.id.liste_fin);
        TextView partsCountTextView = (TextView) convertView.findViewById(R.id.liste_nb);
        TextView tagsTextView = (TextView) convertView.findViewById(R.id.liste_type);
        ListView participantsListVIew = (ListView) convertView.findViewById(R.id.liste_partlist);

        nameTextView.setText(currentEvent.getName());
        startDateTextView.setText(currentEvent.startDateToString());
        endDateTextView.setText(currentEvent.endDateToString());
        partsCountTextView.setText(String.valueOf(currentEvent.getParticipantsCount()));
        tagsTextView.setText(currentEvent.getTags());

        List<User> users = currentEvent.getUserList();
        UserListAdapter customBaseAdapter = new UserListAdapter(context, users);
        participantsListVIew.setAdapter(customBaseAdapter);

        /*eventlist.get(position).participants_name(db, new Event.ParticipantsNameCallback() {
            @Override
            public void onParticipantsNameReady(List<String> participantNames) {
                // Faites quelque chose avec la liste des noms des participants
                // Par exemple, mettez à jour l'interface utilisateur
                if (participantNames.size() == eventlist.get(position).getParticipants().size())
                {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, participantNames);
                    LstViewListe.setAdapter(arrayAdapter);
                }

            }
        });*/

        /*
        List<String> participants = eventlist.get(position).participants_name(db);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,R.layout.activity_user_list_view,R.id.list_user, participants);
        LstViewListe.setAdapter(arrayAdapter);*/

        return convertView;
    }
}