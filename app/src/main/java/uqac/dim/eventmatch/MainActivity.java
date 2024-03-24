package uqac.dim.eventmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button CreationEvent;
    Button ListeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CreationEvent = findViewById(R.id.creation_event);
        ListeEvent = findViewById(R.id.liste_event);

        CreationEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Accès page création d'event", Toast.LENGTH_SHORT).show();
                Intent startActivity = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startActivity);
            }
        });
        ListeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Accès page liste d'event", Toast.LENGTH_SHORT).show();
                Intent startActivity = new Intent(MainActivity.this, ListEventtestActivity.class);
                startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startActivity);
            }
        });
    };
}
