package uqac.dim.eventmatch.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    private FirebaseStorage storage;

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
        storage = FirebaseStorage.getInstance();
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
        convertView = inflater.inflate(R.layout.fragment_event,null);
        Event currentEvent = eventList.get(position);

        // Recupération des View
        /*
        TextView nameTextView = (TextView) convertView.findViewById(R.id.liste_nom);
        TextView startDateTextView = (TextView) convertView.findViewById(R.id.liste_debut);
        TextView endDateTextView = (TextView) convertView.findViewById(R.id.liste_fin);
        TextView partsCountTextView = (TextView) convertView.findViewById(R.id.liste_nb);
        TextView tagsTextView = (TextView) convertView.findViewById(R.id.liste_type);
        ListView participantsListView = (ListView) convertView.findViewById(R.id.liste_partlist);
        ImageView eventImageView = (ImageView) convertView.findViewById(R.id.liste_image);
         */

        TextView eventNameTextView = (TextView) convertView.findViewById(R.id.text_event_name);
        TextView partsCountTextView = (TextView) convertView.findViewById(R.id.text_participants_count);
        LinearLayout tagsLinearLayout = (LinearLayout) convertView.findViewById(R.id.layout_tags);
        ImageView eventImageView = (ImageView) convertView.findViewById(R.id.image_event);

        // GESTION IMAGE
        // Obtention d'une référence à l'image dans Firebase Storage
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(currentEvent.getImageDataUrl()); // Chemin vers votre image
        // Téléchargement de l'image dans un fichier temporaire local
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File finalLocalFile = localFile;
        imageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    eventImageView.setImageBitmap(bitmap);
                })
                .addOnFailureListener(exception -> {
                    // Échec du téléchargement de l'image
                    Log.e("TAG", "Erreur lors du téléchargement de l'image : " + exception.getMessage());
                });

        //Affichage des autres Informations
        /*nameTextView.setText(currentEvent.getName());
        startDateTextView.setText(" " +currentEvent.startDateToString());
        endDateTextView.setText(" " +currentEvent.endDateToString());
        partsCountTextView.setText(String.valueOf(currentEvent.getParticipantsCount())+" ");
        tagsTextView.setText(" " +currentEvent.getTags());
        */
        eventNameTextView.setText(currentEvent.getName());
        partsCountTextView.setText(String.valueOf(currentEvent.getParticipantsCount()));

        TextView tagTextView = new TextView(convertView.getContext());
        tagTextView.setText(currentEvent.getTags());
        tagTextView.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.white));
        tagTextView.setShadowLayer(20, 0, 0, ContextCompat.getColor(convertView.getContext(), R.color.black));
        tagTextView.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.default_background));
        tagsLinearLayout.addView(tagTextView);

        /*currentEvent.generateUserList(new Event.UserListCallback() {
            @Override
            public void onUserListReady(ArrayList<User> userList) {

                for (User user : userList) {
                    Log.d("DIM", "dans l'adapter, User: " + user.getEmail());
                }
                UserListAdapter customBaseAdapter = new UserListAdapter(context, userList);
                participantsListView.setAdapter(customBaseAdapter);

                int itemHeightInSp = 25; // Taille d'un élément en SP
                int itemCount = customBaseAdapter.getCount(); // Nombre d'éléments dans la liste
                float density = context.getResources().getDisplayMetrics().density; // Obtenez la densité de l'écran en DPI
                int itemHeightInPx = (int) (itemHeightInSp * density); // Convertissez la taille de l'élément en SP en pixels
                int totalHeightInPx = itemCount * itemHeightInPx; // Calculez la hauteur totale en pixels

                ViewGroup.LayoutParams params = participantsListView.getLayoutParams();
                params.height = totalHeightInPx;
                participantsListView.setLayoutParams(params);


            }
        });*/




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