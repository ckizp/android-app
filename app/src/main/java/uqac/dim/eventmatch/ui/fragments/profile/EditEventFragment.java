package uqac.dim.eventmatch.ui.fragments.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.adapters.SpinnerAdapter;
import uqac.dim.eventmatch.adapters.UserListAdapter;
import uqac.dim.eventmatch.models.Event;
import uqac.dim.eventmatch.models.SpinnerItem;
import uqac.dim.eventmatch.models.User;

/*/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {

    private View rootView;
    private Event event_db;
    private Event event_modif;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private TextView eventName;
    private EditText participantsCount;
    private Spinner eventType;
    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private int[] debut;
    private int[] fin;
    private TextView startTextView;
    private TextView endTextView;
    private Button saveButton;
    private Button reinitButton;
    private ListView participantLstView;
    private Button deleteeventButton;
    private Button debugbutton;
    private boolean confirmationDialogShown = false;


    public EditEventFragment() {
        // Required empty public constructor
    }

    public EditEventFragment(Event e){
        event_db = e.Copy();
        event_modif = e.Copy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);

        context = rootView.getContext();
        database = FirebaseFirestore.getInstance();
        /*storage = FirebaseStorage.getInstance();
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

        participantLstView = rootView.findViewById(R.id.editevent_userlist);


        saveButton = rootView.findViewById(R.id.editevent_button_save);
        reinitButton = rootView.findViewById(R.id.editevent_button_reinit);
        deleteeventButton = rootView.findViewById(R.id.edit_event_button_delete);

        debut = timestamp_to_tab(event_db.getStartDate());
        fin = timestamp_to_tab(event_db.getEndDate());

        affiche_listuser(event_db);

        //Choix du type d'événement
        List<SpinnerItem> spinnerItems = new ArrayList<>();
        // Ajoutez les autres éléments ici
        spinnerItems.add(new SpinnerItem(R.drawable.autre, "autre"));
        spinnerItems.add(new SpinnerItem(R.drawable.sport, "sport"));
        spinnerItems.add(new SpinnerItem(R.drawable.musique, "musique"));
        spinnerItems.add(new SpinnerItem(R.drawable.cinema, "cinéma"));
        spinnerItems.add(new SpinnerItem(R.drawable.jeux, "jeux"));
        spinnerItems.add(new SpinnerItem(R.drawable.culture, "culture"));
        spinnerItems.add(new SpinnerItem(R.drawable.art, "art"));
        spinnerItems.add(new SpinnerItem(R.drawable.cuisine, "cuisine"));
        spinnerItems.add(new SpinnerItem(R.drawable.rencontre, "réunion"));

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), spinnerItems);
        eventType.setAdapter(adapter);

        // récupération des dates de début et de fin de l'événement
        debut = timestamp_to_tab(event_db.getStartDate());
        fin = timestamp_to_tab(event_db.getEndDate());

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("date","debut");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("date","fin");
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("heure","debut");
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("heure","fin");
            }
        });

        updateDate();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick(saveButton);
            }
        });

        reinitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinitEvent();
            }
        });

        deleteeventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_event();
            }
        });

        debugbutton = rootView.findViewById(R.id.debug);
        debugbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });

        /* TODO : Trouver une solution pour que cela marche
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (confirmationDialogShown)
                {
                    confirmationDialogShown = false;
                }

                else
                    showExitConfirmationDialog();
            }
        };

        // Ajoutez le rappel à l'activité
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        */

        update_all_editview(event_db);

        return rootView;
    }



    private void openDialog(String type, String quand){
        Calendar cal = Calendar.getInstance();
        if (type.equals("date")){
            DatePickerDialog dialog = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if (quand.equals("debut")){
                        debut[0] = year;
                        debut[1] = month + 1;
                        debut[2] = dayOfMonth;
                    } else if (quand.equals("fin")) {
                        if (new GregorianCalendar(year, month, dayOfMonth).before(new GregorianCalendar(debut[0], debut[1] - 1, debut[2]))) {
                            Toast.makeText(view.getContext(), "La date de fin ne peut pas être antérieure à la date de début.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fin[0] = year;
                        fin[1] = month + 1;
                        fin[2] = dayOfMonth;
                    }
                    updateDate();
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            // Set minimum date to current date
            dialog.getDatePicker().setMinDate(cal.getTimeInMillis());

            dialog.show();
        } else if (type.equals("heure")) {
            TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (quand.equals("debut")){
                        debut[3] = hourOfDay;
                        debut[4] = minute;
                    } else if (quand.equals("fin")) {
                        if (new GregorianCalendar(fin[0], fin[1] - 1, fin[2], hourOfDay, minute).before(new GregorianCalendar(debut[0], debut[1] - 1, debut[2], debut[3], debut[4]))) {
                            Toast.makeText(view.getContext(), "L'heure de fin ne peut pas être antérieure à l'heure de début.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fin[3] = hourOfDay;
                        fin[4] = minute;
                    }
                    updateDate();
                }
            }, 0, 0, true);
            dialog.show();
        }
    }

    public void onSaveButtonClick(View view) {
        Log.i("DIM", "saving event");

        Log.w("DIM", "nom : " + eventName.getText().toString());
        Log.w("DIM", "nb : " + participantsCount.getText().toString());
        Log.w("DIM", "type : " + eventType.getSelectedItem().toString());

        if (eventName.getText().toString().isEmpty() || participantsCount.getText().toString().isEmpty() || eventType.getSelectedItem().toString().isEmpty()) {
            Log.w("DIM", "tous les champs ne sont pas remplis");
            Toast.makeText(view.getContext(), getString(R.string.toast_remplir_champs), Toast.LENGTH_SHORT).show();
        } else {
            event_modif.setName(eventName.getText().toString());
            event_modif.setParticipantsCount(Integer.parseInt(participantsCount.getText().toString()));

            SpinnerItem item = (SpinnerItem) eventType.getSelectedItem();
            event_modif.setTags(item.getText());

            DocumentReference eventRef = database.document(event_modif.referenceOfthisEvent().getPath());

            // Mettez à jour le document avec les nouvelles données
            eventRef.set(event_modif)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // La mise à jour a réussi
                            Toast.makeText(view.getContext(), getString(R.string.toast_modif_success_event), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // La mise à jour a échoué
                            Toast.makeText(view.getContext(), getString(R.string.toast_modif_failure_event), Toast.LENGTH_SHORT).show();
                            Log.e("DIM", "Erreur lors de la mise à jour de l'événement", e);
                        }
                    });
            event_db = event_modif.Copy();
        }
    }

    public void updateDate() {
        event_modif.StartDateTabSet(debut);
        event_modif.EndDateTabSet(fin);

        startTextView.setText("Début " + event_modif.startDateToString()); //TODO : mettre dans string.xml
        endTextView.setText("Fin " + event_modif.endDateToString());
    }

    private void update_all_editview(Event event)
    {

        // je veux que le spinner eventType soir sur la bonne entrée
        int eventTypeIndex = getEventTypeIndex(event.getTags());
        eventType.setSelection(eventTypeIndex);
        eventName.setText(event.getName());
        participantsCount.setText(String.valueOf(event.getParticipantsCount()));
        startTextView.setText(event.startDateToString());
        endTextView.setText(event.endDateToString());
    }

    private int getEventTypeIndex(String eventTypestr) {
        SpinnerAdapter adapter = (SpinnerAdapter) eventType.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            SpinnerItem item = adapter.getItem(i);
            if (item.getText().equals(eventTypestr)) {
                return i;
            }
        }
        return 0; // Par défaut, revenir au premier élément si non trouvé
    }

    private void reinitEvent()
    {
        //TODO : Probleme dans l'ordre qui bouge à check (Surement Fix, pas sûr)
        event_modif = event_db.Copy();
        update_all_editview(event_modif);
        updateDate();
        affiche_listuser(event_modif);
    }

    private int[] timestamp_to_tab(Timestamp timestamp)
    {
        Date date = timestamp.toDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int[] res = new int[] {2024, 4, 1, 0, 0, 0};
        res[0] = calendar.get(Calendar.YEAR); // Année
        res[1] = calendar.get(Calendar.MONTH) + 1; // Mois (0-based, donc on ajoute 1)
        res[2] = calendar.get(Calendar.DAY_OF_MONTH); // Jour du mois
        res[3] = calendar.get(Calendar.HOUR_OF_DAY); // Heure (format 24 heures)
        res[4] = calendar.get(Calendar.MINUTE); // Minute
        res[5] = calendar.get(Calendar.SECOND); // Seconde

        return res;

    }

    // Méthode pour transformer la date en liste d'entiers
    public int[] dateTransform(Date date){
        int[] dateTab = new int[6];
        dateTab[0] = date.getYear() + 1900;
        dateTab[1] = date.getMonth() + 1;
        dateTab[2] = date.getDate();
        dateTab[3] = date.getHours();
        dateTab[4] = date.getMinutes();
        dateTab[5] = date.getSeconds();
        return dateTab;
    }

    private void affiche_listuser(Event event)
    {
        Fragment frag = this;
        event.generateUserList(new Event.UserListCallback() {
            @Override
            public void onUserListReady(ArrayList<User> userList) {

                for (User user : userList) {
                    Log.d("DIM", "dans l'adapter, User: " + user.getEmail());
                }
                UserListAdapter customBaseAdapter = new UserListAdapter(context, userList, frag,"EditEventFragment");
                participantLstView.setAdapter(customBaseAdapter);

                int itemHeightInSp = 50; // Taille d'un élément en SP
                int itemCount = customBaseAdapter.getCount(); // Nombre d'éléments dans la liste
                float density = context.getResources().getDisplayMetrics().density; // Obtenez la densité de l'écran en DPI
                int itemHeightInPx = (int) (itemHeightInSp * density); // Convertissez la taille de l'élément en SP en pixels
                int totalHeightInPx = itemCount * itemHeightInPx; // Calculez la hauteur totale en pixels

                ViewGroup.LayoutParams params = participantLstView.getLayoutParams();
                params.height = totalHeightInPx;
                participantLstView.setLayoutParams(params);


            }
        });
    }

    public void deleteuser(int pos, String username)
    {
        List<DocumentReference> participantslist = new ArrayList<DocumentReference>() {};
        for (DocumentReference docref: (event_modif.getParticipants())) {
            participantslist.add(docref);
        }
        participantslist.remove(pos);
        event_modif.setParticipants(participantslist);
        Toast.makeText(context, getString(R.string.toast_delete_user)+" " + username, Toast.LENGTH_SHORT).show();
        affiche_listuser(event_modif);
    }


    private void delete_event() {
        // Obtenir la référence du document de l'événement à supprimer
        DocumentReference eventRef = database.document(event_db.referenceOfthisEvent().getPath());
        showDeleteConfirmationDialog(eventRef);
    }

    private void debug()
    {
        Log.d("DIM", "dans modif : ");
        for (DocumentReference docref: (event_modif.getParticipants())) {
            Log.d("DIM", docref.getPath());
        }
        Log.d("DIM", "dans db : ");
        for (DocumentReference docref: (event_db.getParticipants())) {
            Log.d("DIM", docref.getPath());
        }
    }

    private void showExitConfirmationDialog() {
        confirmationDialogShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_text_quit));
        builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requireActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_no), null); // L'utilisateur ne veut pas quitter l'écran
        builder.show(); // Afficher la boîte de dialogue
    }

    private void showDeleteConfirmationDialog(DocumentReference eventRef) {
        confirmationDialogShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_text_delete));
        builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Supprimer le document de la base de données Firestore
                eventRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document supprimé avec succès
                                Toast.makeText(context, getString(R.string.toast_supp_success_event), Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed(); // Quitter

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Une erreur s'est produite lors de la suppression du document
                                Toast.makeText(context, getString(R.string.toast_supp_failure_event), Toast.LENGTH_SHORT).show();
                                Log.e("DIM", "Erreur lors de la suppression de l'événement", e);
                            }
                        });
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_no), null); // L'utilisateur ne veut pas quitter l'écran
        builder.show(); // Afficher la boîte de dialogue
    }

}