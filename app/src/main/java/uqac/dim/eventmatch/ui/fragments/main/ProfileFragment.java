package uqac.dim.eventmatch.ui.fragments.main;

import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.ui.activities.LoginActivity;
import uqac.dim.eventmatch.ui.fragments.profile.AccountFragment;
import uqac.dim.eventmatch.ui.fragments.profile.AdminFragment;
import uqac.dim.eventmatch.ui.fragments.profile.FeedbackFragment;
import uqac.dim.eventmatch.ui.fragments.profile.MyEventsFragment;
import uqac.dim.eventmatch.ui.fragments.profile.NotificationsFragment;

/**
 *
 * @version 1.0 31 Mar 2024
 * @author Ibraguim Temirkhaev
 */
public class ProfileFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore database;
    private TextView greetingTextView;
    boolean adminPermission;

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        NavigationView navigationView = view.findViewById(R.id.profile_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        database = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DocumentReference userReference = database.collection("users").document(userId);

        greetingTextView = view.findViewById(R.id.text_greeting);

        userReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String greeting = "Bonjour " + documentSnapshot.getString("username") + " !";
                        greetingTextView.setText(greeting);

                        // Récupération des permissions admin
                        adminPermission = Boolean.TRUE.equals(documentSnapshot.getBoolean("isAdmin"));
                    }
                });

        //Ne pas afficher le menu admin si l'utilisateur n'est pas admin
        if(!adminPermission) {
            navigationView.getMenu().findItem(R.id.menu_admin).setVisible(false);
        }

        Log.d("isAdmin", String.valueOf(adminPermission));
        return view;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        final Fragment[] fragment = new Fragment[1];
        //Affichage du fragment admin seulement si l'utilisateur est admin
        if(itemId == R.id.menu_admin && adminPermission) {
            fragment[0] = new AdminFragment();
        } else if (itemId == R.id.menu_events) {
            fragment[0] = new MyEventsFragment();
        } else if (itemId == R.id.menu_account) {
            fragment[0] = new AccountFragment();
        } else if (itemId == R.id.menu_notifications) {
            fragment = new NotificationsFragment();
        } else if (itemId == R.id.menu_rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eventmatch.name"));

            // On vérifie si l'application Google Play Store est installée
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // On ouvre la page de notation sur le Google Play Store
                startActivity(intent);
            } else {
                // Si l'application Google Play Store n'est pas installée, ouvrir la page dans le navigateur
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=eventmatch.name"));
                startActivity(intent);
            }

            return true;
        } else if (itemId == R.id.menu_feedback) {
            fragment[0] = new FeedbackFragment();
        } else if (itemId == R.id.menu_disconnect) {
            FirebaseAuth.getInstance().signOut();
            // Retour a la page de connexion LoginActivity
            Intent signup = new Intent(requireActivity(), LoginActivity.class);
            signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signup);
            return true;
        } else {
            return false;
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .replace(R.id.frame_layout, fragment[0])
                .addToBackStack(null)
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