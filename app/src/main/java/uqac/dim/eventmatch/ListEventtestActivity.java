package uqac.dim.eventmatch;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListEventtestActivity extends AppCompatActivity {
    ListView Liste;
    Button RetourMenu;
    FirebaseFirestore database;
    ArrayList<Event> EventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event_test);




        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        database = FirebaseFirestore.getInstance();

        Liste = findViewById(R.id.eventlist_liste);
        RetourMenu = findViewById(R.id.eventlist_button_main);


        RetourMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListEventtestActivity.this, "Retour Main", Toast.LENGTH_SHORT).show();
                Intent startActivity = new Intent(ListEventtestActivity.this, MainActivity.class);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startActivity);
            }
        });

        EventList = new ArrayList<Event>();
        EventList.add(new Event("Test",new Timestamp(1711163400,0),new Timestamp(1711336200,0),10,"sport"));
        EventList.add(new Event("Match",new Timestamp(1711163400,0),new Timestamp(1711336200,0),1,"rassemblement"));
        EventList.add(new Event("Test3",new Timestamp(1711163400,0),new Timestamp(1711336200,0),1000,"soirée"));
        EventList.add(new Event("Test4",new Timestamp(1711163400,0),new Timestamp(1711336200,0),5,"musique"));

        database.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DIM", document.getId() + " => " + document.getData());
                                String name = document.getString("name");
                                Timestamp debut = document.getTimestamp("date_start");
                                Timestamp fin = document.getTimestamp("date_end");
                                int nb = document.getDouble("nb_participants").intValue();
                                String type = document.getString("type");

                                Event event = new Event(name,fin,debut,nb,type);
                                EventList.add(event);
                            }
                            CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(),EventList);
                            Liste.setAdapter(customBaseAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

}

