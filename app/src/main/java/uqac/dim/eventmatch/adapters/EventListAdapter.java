package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

public class EventListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Event> eventList;

    public EventListAdapter(Context ctx, ArrayList<Event> l){
        context = ctx;
        eventList = l;
        inflater = LayoutInflater.from(ctx);
    }

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

        TextView TxtViewNom = (TextView) convertView.findViewById(R.id.liste_nom);
        TextView TxtViewDebut = (TextView) convertView.findViewById(R.id.liste_debut);
        TextView TxtViewFin = (TextView) convertView.findViewById(R.id.liste_fin);
        TextView TxtViewNB = (TextView) convertView.findViewById(R.id.liste_nb);
        TextView TxtViewType= (TextView) convertView.findViewById(R.id.liste_type);

        TxtViewNom.setText(eventList.get(position).getName());
        TxtViewDebut.setText(" " + eventList.get(position).Date_startString());
        TxtViewFin.setText(" " + eventList.get(position).Date_endString());
        TxtViewNB.setText(eventList.get(position).Nb_paricipantsString() + " ");
        TxtViewType.setText(" " + eventList.get(position).getTags());

        return convertView;
    }
}