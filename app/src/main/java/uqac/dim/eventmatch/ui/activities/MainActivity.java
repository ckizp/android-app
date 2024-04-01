package uqac.dim.eventmatch.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.ui.fragments.main.CreateFragment;
import uqac.dim.eventmatch.ui.fragments.main.FavoritesFragment;
import uqac.dim.eventmatch.ui.fragments.main.MessagesFragment;
import uqac.dim.eventmatch.ui.fragments.main.ProfileFragment;
import uqac.dim.eventmatch.ui.fragments.main.SearchFragment;

/**
 *
 * @version 1.0 30 Mar 2024
 * @author Ibraguim Temirkhaev
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        currentPosition = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SearchFragment())
                .commit();
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment;
        int newPosition;

        if (itemId == R.id.menu_search) {
            fragment = new SearchFragment();
            newPosition = 0;
        } else if (itemId == R.id.menu_favorites) {
            fragment = new FavoritesFragment();
            newPosition = 1;
        } else if (itemId == R.id.menu_create) {
            fragment = new CreateFragment();
            newPosition = 2;
        } else if (itemId == R.id.menu_messages) {
            fragment = new MessagesFragment();
            newPosition = 3;
        } else if (itemId == R.id.menu_profile) {
            fragment = new ProfileFragment();
            newPosition = 4;
        } else {
            return false;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currentPosition > newPosition)
            transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        else if (currentPosition < newPosition)
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

        currentPosition = newPosition;
        transaction.replace(R.id.frame_layout, fragment).commit();
        return true;
    }

    public void test(View view) {
    }
}