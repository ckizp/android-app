// MyEventListAdapter.java
package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.ui.fragments.profile.EditEventFragment;

public class MyEventListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> eventList;

    private FirebaseStorage storage;

    public MyEventListAdapter(Context context, ArrayList<Event> eventList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item_myevents, parent, false);
        }

        Event event = eventList.get(position);

        // Récupération des vues
        ImageView imageView = convertView.findViewById(R.id.myeventimage_view);
        TextView eventNameTextView = convertView.findViewById(R.id.myevent_event_name);
        Button DetailsButton = convertView.findViewById(R.id.myevent_button_details);
        Button EditButton = convertView.findViewById(R.id.myevent_button_edit);



        event.load_image(imageView,storage);
        eventNameTextView.setText(event.getName());
        //TODO : Changer le fond du truc

        DetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Lancer le fragment des détails
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditEventFragment(event);
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment).commit();
            }
        });

        return convertView;
    }
}