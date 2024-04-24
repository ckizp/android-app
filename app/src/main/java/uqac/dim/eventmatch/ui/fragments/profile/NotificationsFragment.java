package uqac.dim.eventmatch.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.ui.fragments.main.ProfileFragment;

public class NotificationsFragment extends Fragment {
    private FirebaseFirestore database;
    private View rootView;
    private ImageView backImageView;

    public NotificationsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        database = FirebaseFirestore.getInstance();

        backImageView = rootView.findViewById(R.id.image_view_back);
        backImageView.setOnClickListener(v -> ProfileFragment.backToProfileFragment(requireActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        DocumentReference userReference = database.collection("users").document(userId);

        return rootView;
    }
}