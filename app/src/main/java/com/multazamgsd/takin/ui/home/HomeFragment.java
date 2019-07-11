package com.multazamgsd.takin.ui.home;

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
import com.multazamgsd.takin.util.DividerItemDecorator;
import com.multazamgsd.takin.util.EventAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private EventAdapter recommendedAdapter, newAdapter;

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
        recommendedAdapter = new EventAdapter(getActivity(), new EventAdapter.eventAdapterListener() {
            @Override
            public void onEventClick(int itemPosition) {

            }

            @Override
            public void onEventLike(int itemPosition) {

            }

            @Override
            public void onEventShare(int itemPosition) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvEventRecommended.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        rvEventRecommended.addItemDecoration(dividerItemDecoration);
        rvEventRecommended.setAdapter(recommendedAdapter);
    }

    private void setRecommendedList() {
        newAdapter = new EventAdapter(getActivity(), new EventAdapter.eventAdapterListener() {
            @Override
            public void onEventClick(int itemPosition) {

            }

            @Override
            public void onEventLike(int itemPosition) {

            }

            @Override
            public void onEventShare(int itemPosition) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvEventNew.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        rvEventNew.addItemDecoration(dividerItemDecoration);
        rvEventNew.setAdapter(recommendedAdapter);
    }

    private void loadData() {
        FirebaseFirestore.getInstance().collection("event").limit(4).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> eventList = new ArrayList<>();

                for(DocumentSnapshot doc : task.getResult()){
                    Event e = doc.toObject(Event.class);
                    e.setId(doc.getId());
                    eventList.add(e);
                }

                // Divide to 2 lists

                // Set to recommended rv
                recommendedAdapter.setListEvents(eventList);
                recommendedAdapter.notifyDataSetChanged();

                // Set to new rv
                newAdapter.setListEvents(eventList);
                newAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}
