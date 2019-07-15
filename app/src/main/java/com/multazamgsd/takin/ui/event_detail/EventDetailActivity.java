package com.multazamgsd.takin.ui.event_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.GlideApp;

public class EventDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "extra_event";
    private Integer ticketAvailable;

    private ImageView ivEvent;
    private TextView tvTitle, tvPublisher, tvDescription; // Main Card
    private TextView tvDate, tvTime, tvPlace, tvAddress, tvTicketAvailability, tvPoint, tvPrice; // Schedule info card
    private Toolbar toolbar;
    private Event event;

    private GoogleMap locationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setPadding(12,0,24,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set main info data
        ivEvent = findViewById(R.id.imageViewDetailEvent);
        tvTitle = findViewById(R.id.textViewDetailEventTitle);
        tvPublisher = findViewById(R.id.textViewDetailEventPublisher);
        tvDescription = findViewById(R.id.textViewDetailEventDesc);

        event = getIntent().getParcelableExtra(EXTRA_EVENT);
        GlideApp.with(this)
                .load(event.getPhoto_url())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_image_grey_24dp)
                        .error(R.drawable.ic_image_grey_24dp))
                .into(ivEvent);
        tvTitle.setText(event.getTitle());
        tvPublisher.setText(String.format("Published by %s", event.getPublisher()));
        tvTitle.setText(event.getTitle());
        tvDescription.setText(event.getDescription());

        // Setting up page title
        String title = event.getType().substring(0, 1).toUpperCase() + event.getType().substring(1);
        setTitle(title);

        // Set schedule card info data
        tvDate = findViewById(R.id.textViewEventDetailDate);
        tvTime = findViewById(R.id.textViewEventDetailTime);
        tvPlace = findViewById(R.id.textViewEventDetailPlace);
        tvAddress = findViewById(R.id.textViewEventDetailAddress);
        tvTicketAvailability = findViewById(R.id.textViewEventDetailTicketAvailability);

        tvDate.setText(event.getDate());
        tvTime.setText(String.format("%s - %s WIB", event.getTime_start(), event.getTime_end()));
        tvPlace.setText(event.getLocation_name());
        tvAddress.setText(event.getLocation_address());
        ticketAvailable = Integer.parseInt(event.getTicket_total()) - Integer.parseInt(event.getTicket_sold());
        tvTicketAvailability.setText(String.format("%s / %s Tickets", ticketAvailable ,event.getTicket_total()));

        // Defining google maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(googleMap -> {
            locationMap = googleMap;

            double lat = Double.parseDouble(event.getLocation_lat());
            double lng = Double.parseDouble(event.getLocation_long());

            locationMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(event.getLocation_name()));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_like:
                break;
            case R.id.action_share:
                ShareCompat.IntentBuilder
                        .from(EventDetailActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("Share event")
                        .setText(String.format("Ayo daftar event %s di aplikasi Takin, hanya %s tiket tersedia!", event.getTitle(), String.valueOf(ticketAvailable)))
                        .startChooser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
