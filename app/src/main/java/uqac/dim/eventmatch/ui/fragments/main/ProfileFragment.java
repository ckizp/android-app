package uqac.dim.eventmatch.ui.fragments.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.ui.fragments.profile.AccountFragment;
import uqac.dim.eventmatch.ui.fragments.profile.FeedbackFragment;
import uqac.dim.eventmatch.ui.fragments.profile.MyEventsFragment;
import uqac.dim.eventmatch.ui.fragments.profile.NotificationsFragment;
import uqac.dim.eventmatch.ui.fragments.profile.SecurityFragment;

/**
 *
 * @version 1.0 31 Mar 2024
 * @author Ibraguim Temirkhaev
 */
public class ProfileFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        NavigationView navigationView = view.findViewById(R.id.profile_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        TextView TxtProfileName = view.findViewById(R.id.nom_profile);
        TxtProfileName.setText (FirebaseAuth.getInstance().getCurrentUser().getDisplayName()); //TODO : à fix pour avoir vrmt l'user

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment;

        if (itemId == R.id.menu_events) {
            fragment = new MyEventsFragment();
        } else if (itemId == R.id.menu_account) {
            fragment = new AccountFragment();
        } else if (itemId == R.id.menu_notifications) {
            fragment = new NotificationsFragment();
        } else if (itemId == R.id.menu_security) {
            fragment = new SecurityFragment();
        } else if (itemId == R.id.menu_rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eventmatch.name"));

            // On vérifie si l'application Google Play Store est installée
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Ouvrir la page de notation sur le Google Play Store
                startActivity(intent);
            } else {
                // Si l'application Google Play Store n'est pas installée, ouvrir la page dans le navigateur
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=eventmatch.name"));
                startActivity(intent);
            }

            return true;
        } else if (itemId == R.id.menu_feedback) {
            fragment = new FeedbackFragment();
        } else if (itemId == R.id.menu_disconnect) {
            Firestora
            return true;
        } else {
            return false;
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .replace(R.id.frame_layout, fragment)
                .commit();
        return true;
    }

    public static void backToProfileFragment(FragmentActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                .replace(R.id.frame_layout, new ProfileFragment())
                .commit();
    }
}