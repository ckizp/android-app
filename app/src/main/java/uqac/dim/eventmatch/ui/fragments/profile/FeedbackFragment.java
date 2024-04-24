package uqac.dim.eventmatch.ui.fragments.profile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.Feedback;
import uqac.dim.eventmatch.ui.fragments.main.ProfileFragment;

public class FeedbackFragment extends Fragment {
    private FirebaseFirestore database;
    private View rootView;
    private Button feedbackButton;
    private ImageView backImageView;
    private EditText feedbackEditText;

    public FeedbackFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        database = FirebaseFirestore.getInstance();

        backImageView = rootView.findViewById(R.id.image_view_back);
        backImageView.setOnClickListener(v ->
            ProfileFragment.backToProfileFragment(requireActivity())
        );

        feedbackButton = rootView.findViewById(R.id.button_feedback);
        feedbackButton.setOnClickListener(this::onFeedbackButtonClick);

        return rootView;
    }

    public void onFeedbackButtonClick(View v) {
        feedbackEditText = rootView.findViewById(R.id.edit_text_feedback);

        String feedbackText = feedbackEditText.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DocumentReference userReference = database.collection("feedbacks").document(userId);

        Feedback feedback = new Feedback(userReference, feedbackText);

        database.collection("feedbacks").add(feedback)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(rootView.getContext(), "Feedback créé et stocké dans Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(rootView.getContext(), "Erreur lors de l'envoi des données à Firestore", Toast.LENGTH_SHORT).show();
                    }
                });

        ProfileFragment.backToProfileFragment(requireActivity());
        feedbackEditText.setText("");
    }
}