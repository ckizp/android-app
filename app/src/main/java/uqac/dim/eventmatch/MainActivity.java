package uqac.dim.eventmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Ibraguim Temirkhaev
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SearchFragment())
                .commit();
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_search) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new SearchFragment())
                    .commit();
            return true;
        } else if (itemId == R.id.menu_favorites) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new FavoritesFragment())
                    .commit();
            return true;
        } else if (itemId == R.id.menu_create) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new CreateFragment())
                    .commit();
            return true;
        } else if (itemId == R.id.menu_messages) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new MessagesFragment())
                    .commit();
            return true;
        } else if (itemId == R.id.menu_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new ProfileFragment())
                    .commit();
            return true;
        }
        return false;
    }
}