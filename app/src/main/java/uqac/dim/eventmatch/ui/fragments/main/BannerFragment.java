package uqac.dim.eventmatch.ui.fragments.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import uqac.dim.eventmatch.R;

public class BannerFragment extends Fragment {
    private TextView bannerTextView;
    private int currentBannerIndex = 0;
    private Handler bannerHandler;
    private ArrayList<String> bannerTexts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner, container, false);
        bannerTextView = rootView.findViewById(R.id.banner_text_view);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bannerTexts = getArguments().getStringArrayList("bannerTexts");
        startBannerScroll();
    }

    private void startBannerScroll() {
        // Vérifiez si la liste des messages de la bannière est vide
        if (bannerTexts == null || bannerTexts.isEmpty()) {
            return;
        }

        // Initialisez le handler
        bannerHandler = new Handler(Looper.getMainLooper());

        // Démarrez le défilement de la bannière
        bannerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Vérifier si le fragment est encore attaché à l'activité
                if (isAdded()) {
                    // Mettez à jour le texte de la bannière
                    bannerTextView.setText(bannerTexts.get(currentBannerIndex));


                    // Mesurer la largeur du texte de la bannière
                    int textWidth = (int) bannerTextView.getPaint().measureText(bannerTexts.get(currentBannerIndex)) + 50;

                    // Réinitialisez la position de la bannière
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    int screenWidth = displayMetrics.widthPixels;

                    bannerTextView.setTranslationX(screenWidth);
                    // Déplacez la bannière
                    bannerTextView.animate()
                            .translationX(-textWidth)
                            .setDuration(Math.max(1, (long) ((textWidth + screenWidth) / displayMetrics.density) * 7))
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Augmentez l'index du message de la bannière
                                    currentBannerIndex++;
                                    if (currentBannerIndex == bannerTexts.size()) {
                                        currentBannerIndex = 0;
                                    }

                                    // Relancez le défilement de la bannière
                                    startBannerScroll(); // Appel récursif pour déplacer le prochain message de la bannière
                                }
                            })
                            .start();
                }
            }
        }, 500); // Démarrer le défilement après un délai de 2 secondes
    }
}
