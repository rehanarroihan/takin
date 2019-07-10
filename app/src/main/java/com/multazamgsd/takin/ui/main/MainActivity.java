package com.multazamgsd.takin.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.multazamgsd.takin.LoginActivity;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.adapter.EventAdapter;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.DividerItemDecorator;
import com.multazamgsd.takin.util.GlideApp;
import com.multazamgsd.takin.util.StringHelper;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EventAdapter.eventAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AuthHelper authHelper;
    private DatabaseHelper databaseHelper;
    private EventAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        authHelper = new AuthHelper(this);
        if(authHelper.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        databaseHelper = new DatabaseHelper();

        //Setting up drawer navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //Get navigation drawer header layout
        View navHeaderView = navigationView.getHeaderView(0);
        TextView username = navHeaderView.findViewById(R.id.textViewDrawerUsername);
        username.setText("Hello");
        TextView email = navHeaderView.findViewById(R.id.textViewDrawerEmail);
        email.setText(authHelper.getCurrentUser().getEmail());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Setting up bottom navigation
        BottomNavigationView navView = findViewById(R.id.nav_bottom_view);
        navView.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener);
        navigationView.setNavigationItemSelectedListener(this);

        setUpRecyclerView();
        getData();
    }

    private void getData() {
        FirebaseFirestore.getInstance().collection("event").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> eventList = new ArrayList<>();

                for(DocumentSnapshot doc : task.getResult()){
                    Event e = doc.toObject(Event.class);
                    e.setId(doc.getId());
                    eventList.add(e);
                }
                Log.d(TAG, String.valueOf(eventList.size()));
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void setUpRecyclerView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventRef = db.collection("event");
        Query query = eventRef.orderBy("title").limit(5);
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setLifecycleOwner(this)
                .setQuery(query, Event.class)
                .build();

//        mAdapter = new EventAdapter(options, this);

        FirestoreRecyclerAdapter adapter;

        adapter = new FirestoreRecyclerAdapter<Event, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_row, parent, false);

                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event event) {
                StringHelper stringHelper = new StringHelper();
                holder.tvTitle.setText(stringHelper.cutString(event.getTitle(), 57));
                holder.tvLocation.setText(stringHelper.cutString(event.getLocation_name(), 29));
                holder.tvTime.setText(event.getTime_start());

                //Parsing date to readable format
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.ENGLISH);
                    Date date = sdf.parse(event.getDate());
                    sdf.applyPattern("EEE, d");
                    String finalDateFormat = sdf.format(date);
                    holder.tvDate.setText(finalDateFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("Adapter", event.getTitle());

                GlideApp.with(holder.itemView.getContext())
                        .load(event.getPhoto_url())
                        //.apply(RequestOptions
                        //        .placeholderOf(R.raw.image_loading)
                        //        .error(R.raw.image_placeholder))
                        .into(holder.ivBanner);
            }
        };

        RecyclerView rv = findViewById(R.id.rvEvent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.divider));
        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(adapter);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvLocation, tvDate, tvTime, tvPrice;
        final ImageView ivBanner, btShare, btLove;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textViewEventTitle);
            tvLocation = itemView.findViewById(R.id.textViewEventLocationName);
            tvDate = itemView.findViewById(R.id.textViewEventDate);
            tvTime = itemView.findViewById(R.id.textViewEventTimeStart);
            tvPrice = itemView.findViewById(R.id.textViewEventPrice);
            ivBanner = itemView.findViewById(R.id.imageViewEventBanner);
            btShare = itemView.findViewById(R.id.buttonShare);
            btLove = itemView.findViewById(R.id.buttonLove);

            //itemView.setOnClickListener(v -> mEventAdapterListener.onEventClick(getAdapterPosition()));
            //btShare.setOnClickListener(v -> mEventAdapterListener.onEventShare(getAdapterPosition()));
            //btLove.setOnClickListener(v -> mEventAdapterListener.onEventLike(getAdapterPosition()));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Navigation drawer item click listener
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_my_event) {

        } else if (id == R.id.nav_liked_event) {

        } else if (id == R.id.nav_logout) {
            authHelper.doLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            mOnBottomNavigationItemSelectedListener = item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_seminar:
                        return true;
                    case R.id.navigation_committee:
                        return true;
                    case R.id.navigation_contest:
                        return true;
                }
                return false;
            };

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
