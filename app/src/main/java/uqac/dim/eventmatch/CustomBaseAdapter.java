package uqac.dim.eventmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {


    Context context;
    LayoutInflater inflater;
    ArrayList<Event> eventlist;


    public CustomBaseAdapter(Context ctx,ArrayList<Event> l){
        context = ctx;
        eventlist = l;
        inflater = LayoutInflater.from(ctx);
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
        TextView TxtViewNom = (TextView) convertView.findViewById(R.id.liste_nom);
        TextView TxtViewDebut = (TextView) convertView.findViewById(R.id.liste_debut);
        TextView TxtViewFin = (TextView) convertView.findViewById(R.id.liste_fin);
        TextView TxtViewNB = (TextView) convertView.findViewById(R.id.liste_nb);
        TextView TxtViewType= (TextView) convertView.findViewById(R.id.liste_type);

        TxtViewNom.setText(eventlist.get(position).getName());
        TxtViewDebut.setText(" "+eventlist.get(position).Date_startString());
        TxtViewFin.setText(" "+eventlist.get(position).Date_endString());
        TxtViewNB.setText(eventlist.get(position).Nb_paricipantsString()+" ");
        TxtViewType.setText(" "+ eventlist.get(position).getType());

        return convertView;
    }
}
