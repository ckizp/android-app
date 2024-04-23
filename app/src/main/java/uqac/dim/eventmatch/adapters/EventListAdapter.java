// EventListAdapter.java
package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;

public class EventListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> eventList;

    public EventListAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
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

        // Récupération de l'événement à cette position
        Event event = eventList.get(position);

        // Récupération des vues
        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView eventNameTextView = convertView.findViewById(R.id.text_event_name);
        TextView participantsCountTextView = convertView.findViewById(R.id.text_participants_count);
        TextView tagsTextView = convertView.findViewById(R.id.layout_tags);

        // Remplissage des vues avec les données de l'événement
        // Ici, tu devras remplacer les textes par les données de l'événement
        eventNameTextView.setText(event.getName());
        participantsCountTextView.setText(String.valueOf(event.getParticipantsCount()));
        tagsTextView.setText(event.getTags());

        // Tu devras également charger l'image ici, en utilisant la méthode load_image de la classe Event

        return convertView;
    }
}
