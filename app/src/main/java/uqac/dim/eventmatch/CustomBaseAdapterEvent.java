package uqac.dim.eventmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.models.User;

public class CustomBaseAdapterEvent extends BaseAdapter {


    Context context;
    LayoutInflater inflater;
    ArrayList<Event> eventlist;
    FirebaseFirestore db;


    public CustomBaseAdapterEvent(Context ctx, ArrayList<Event> l){
        context = ctx;
        eventlist = l;
        inflater = LayoutInflater.from(ctx);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return eventlist.size();
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

        Event currentevent = eventlist.get(position);

        TextView TxtViewNom = (TextView) convertView.findViewById(R.id.liste_nom);
        TextView TxtViewDebut = (TextView) convertView.findViewById(R.id.liste_debut);
        TextView TxtViewFin = (TextView) convertView.findViewById(R.id.liste_fin);
        TextView TxtViewNB = (TextView) convertView.findViewById(R.id.liste_nb);
        TextView TxtViewType= (TextView) convertView.findViewById(R.id.liste_type);
        ListView LstViewListe = (ListView) convertView.findViewById(R.id.liste_partlist);

        TxtViewNom.setText(eventlist.get(position).getName());
        TxtViewDebut.setText(" "+eventlist.get(position).Date_startString());
        TxtViewFin.setText(" "+eventlist.get(position).Date_endString());
        TxtViewNB.setText(eventlist.get(position).Nb_paricipantsString()+" ");
        TxtViewType.setText(" "+ eventlist.get(position).getType());

        List<User> users = currentevent.userlist();

        CustomBaseAdapterUser customBaseAdapter = new CustomBaseAdapterUser(context, users);
        LstViewListe.setAdapter(customBaseAdapter);

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
