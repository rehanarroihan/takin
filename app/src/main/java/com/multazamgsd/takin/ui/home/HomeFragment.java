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

public class HomeFragment extends Fragment implements EventAdapter.eventAdapterListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private EventAdapter mAdapter;

    private RecyclerView rvEvent;

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
        rvEvent = view.findViewById(R.id.rvEvent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData() {
        mAdapter = new EventAdapter(getActivity(), HomeFragment.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvEvent.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        rvEvent.addItemDecoration(dividerItemDecoration);
        rvEvent.setAdapter(mAdapter);

        FirebaseFirestore.getInstance().collection("event").limit(5).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> eventList = new ArrayList<>();

                for(DocumentSnapshot doc : task.getResult()){
                    Event e = doc.toObject(Event.class);
                    e.setId(doc.getId());
                    eventList.add(e);
                }
                mAdapter.setListEvents(eventList);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    public void onEventClick(int itemPosition) {

    }

    @Override
    public void onEventLike(int itemPosition) {

    }

    @Override
    public void onEventShare(int itemPosition) {

    }
}
