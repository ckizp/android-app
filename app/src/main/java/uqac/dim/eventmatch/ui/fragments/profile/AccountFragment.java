package uqac.dim.eventmatch.ui.fragments.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.UserDataReader;

import java.text.SimpleDateFormat;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.ui.activities.LoginActivity;
import uqac.dim.eventmatch.ui.fragments.main.ProfileFragment;

public class AccountFragment extends Fragment {
    private FirebaseFirestore database;
    private View rootView;

    private ImageView backImageView;
    private TextView usernameTextView;
    private TextView lastnameTextView;
    private TextView firstnameTextView;
    private TextView birthdateTextView;
    private TextView addressTextView;
    private TextView emailTextView;
    private TextView passwordTextView;
    private Button deleteButton;

    public AccountFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);
        database = FirebaseFirestore.getInstance();

        backImageView = rootView.findViewById(R.id.image_view_back);
        backImageView.setOnClickListener(v -> ProfileFragment.backToProfileFragment(requireActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return rootView;
        String userId = user.getUid();
        DocumentReference userReference = database.collection("users").document(userId);

        usernameTextView = rootView.findViewById(R.id.text_username);
        lastnameTextView = rootView.findViewById(R.id.text_lastname);
        firstnameTextView = rootView.findViewById(R.id.text_firstname);
        birthdateTextView = rootView.findViewById(R.id.text_birthdate);
        addressTextView = rootView.findViewById(R.id.text_address);
        emailTextView = rootView.findViewById(R.id.text_email);
        passwordTextView = rootView.findViewById(R.id.text_password);

        userReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("username");
                String lastname = documentSnapshot.getString("lastname");
                String firstname = documentSnapshot.getString("firstname");

                Timestamp birthdateTimestamp = documentSnapshot.getTimestamp("birthdate");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String birthdate = dateFormat.format(birthdateTimestamp.toDate());

                String address = documentSnapshot.getString("address");
                String city = documentSnapshot.getString("city");
                String email = documentSnapshot.getString("email");
                String password = documentSnapshot.getString("password");
                password = password.replaceAll(".", "*");

                usernameTextView.setText(username);
                lastnameTextView.setText(lastname);
                firstnameTextView.setText(firstname);
                birthdateTextView.setText(birthdate);
                addressTextView.setText(address + ", " + city);
                emailTextView.setText(email);
                passwordTextView.setText(password);

                Log.d("DIM", "Récupération des données de l'utilisateur réussie.");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DIM", "Erreur lors de la récupération des données de l'utilisateur.");
                    }
                });

        deleteButton = rootView.findViewById(R.id.button_delete_account);
        deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Confirmation")
                    .setMessage("Êtes-vous sûr de vouloir supprimer votre compte ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUserAccount();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();

            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // ou toute autre couleur de votre choix
        });

        return rootView;
    }

    private void deleteUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;
        user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DIM", "Compte de l'utilisateur supprimé avec succès.");
                FirebaseAuth.getInstance().signOut();
                // Retour a la page de connexion LoginActivity.
                Intent signup = new Intent(requireActivity(), LoginActivity.class);
                signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signup);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DIM", "Erreur lors de la suppression du compte de l'utilisateur.");
                    }
                });
    }
}