package com.multazamgsd.takin.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.ui.event_detail.EventDetailActivity;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.DividerItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private ArrayList<Event> recommendedList = new ArrayList<>();
    private ArrayList<Event> newList = new ArrayList<>();

    private RecommendedAdapter recommendedAdapter;
    private NewAdapter newAdapter;

    private RecyclerView rvEventRecommended;
    private RecyclerView rvEventNew;

    public HomeFragment() {}

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEventRecommended = view.findViewById(R.id.recyclerViewEventRecommended);
        rvEventNew = view.findViewById(R.id.recyclerViewEventNew);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            setRecommendedList();
            setNewList();
            loadData();
        }
    }

    private void setNewList() {
        newAdapter = new NewAdapter(getActivity(), new NewAdapter.eventAdapterListener() {
            @Override
            public void onEventClick(int itemPosition) {
                detailIntent(newList.get(itemPosition));
            }

            @Override
            public void onEventLike(int itemPosition) {

            }

            @Override
            public void onEventShare(int itemPosition) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        rvEventNew.setLayoutManager(layoutManager);
        rvEventNew.addItemDecoration(dividerItemDecoration);
        rvEventNew.setHasFixedSize(true);
        rvEventNew.setNestedScrollingEnabled(false);
        rvEventNew.setAdapter(newAdapter);
    }

    private void setRecommendedList() {
        recommendedAdapter = new RecommendedAdapter(getActivity(), new RecommendedAdapter.eventAdapterListener() {
            @Override
            public void onEventClick(int itemPosition) {
                detailIntent(recommendedList.get(itemPosition));
            }

            @Override
            public void onEventLike(int itemPosition) {

            }

            @Override
            public void onEventShare(int itemPosition) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        rvEventRecommended.setLayoutManager(layoutManager);
        rvEventRecommended.addItemDecoration(dividerItemDecoration);
        rvEventRecommended.setHasFixedSize(true);
        rvEventRecommended.setNestedScrollingEnabled(false);
        rvEventRecommended.setAdapter(recommendedAdapter);
    }

    private void loadData() {
        FirebaseFirestore.getInstance().collection("event").limit(4).get().addOnCompleteListener(task -> {
            ArrayList<Event> result = new ArrayList<>();
            if (task.isSuccessful()) {
                for(DocumentSnapshot doc : task.getResult()){
                    Event e = doc.toObject(Event.class);
                    e.setId(doc.getId());
                    result.add(e);
                }

                // Divide into 2 lists
                for (int i=0; i < result.size(); i++) {
                    if (i < 2) {
                        newList.add(result.get(i));
                    } else {
                        recommendedList.add(result.get(i));
                    }
                }

                // Set to recommended rv
                recommendedAdapter.setListEvents(recommendedList);
                recommendedAdapter.notifyDataSetChanged();

                // Set to new rv
                newAdapter.setListEvents(newList);
                newAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void detailIntent(Event dataToSend) {
        Intent i = new Intent(getActivity(), EventDetailActivity.class);
        i.putExtra(EventDetailActivity.EXTRA_EVENT, dataToSend);
        startActivity(i);
    }
}