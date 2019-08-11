package com.multazamgsd.takin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.multazamgsd.takin.R;
import com.multazamgsd.takin.util.ShadowTransformer;

public class OneSlideshowFragment extends Fragment {

    private CardView cardView;

    public static Fragment getInstance(int position) {
        OneSlideshowFragment f = new OneSlideshowFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slideshow_item, container, false);

        cardView = view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(cardView.getCardElevation() * ShadowTransformer.CardAdapter.MAX_ELEVATION_FACTOR);
        cardView.setOnClickListener(v -> Toast.makeText(getActivity(), "Button in Card " + getArguments().getInt("position")
                + "Clicked!", Toast.LENGTH_SHORT).show());

        return view;
    }

    public CardView getCardView() {
        return cardView;
    }
}
