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
import com.multazamgsd.takin.util.StringHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "extra_event";
    private Boolean descriptionExpand = false;
    private Integer ticketAvailable;

    private ImageView ivEvent;
    private TextView tvTitle, tvPublisher, tvDescription; // Main Card
    private TextView tvDate, tvTime, tvPlace, tvAddress, tvTicketAvailability, tvPoint, tvPrice, tvSeeMore; // Schedule info card
    private Toolbar toolbar;
    private Event event;

    private StringHelper stringHelper;
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

        //Load string helper for trimming
        stringHelper = new StringHelper();

        // Set main info data
        ivEvent = findViewById(R.id.imageViewDetailEvent);
        tvTitle = findViewById(R.id.textViewDetailEventTitle);
        tvPublisher = findViewById(R.id.textViewDetailEventPublisher);
        tvDescription = findViewById(R.id.textViewDetailEventDesc);
        tvSeeMore = findViewById(R.id.textViewSeeMore);
        tvSeeMore.setOnClickListener(v -> showLessDescription());

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
        tvDescription.setText(stringHelper.cutString(event.getDescription(), 183));

        // Setting up page title
        String title = event.getType().substring(0, 1).toUpperCase() + event.getType().substring(1);
        setTitle(title);

        // Set schedule card info data
        tvDate = findViewById(R.id.textViewEventDetailDate);
        tvTime = findViewById(R.id.textViewEventDetailTime);
        tvPlace = findViewById(R.id.textViewEventDetailPlace);
        tvAddress = findViewById(R.id.textViewEventDetailAddress);
        tvTicketAvailability = findViewById(R.id.textViewEventDetailTicketAvailability);
        tvPoint = findViewById(R.id.textViewEventDetailTAKTotal);
        tvPrice = findViewById(R.id.textViewEventDetailPrice);

        // Parsing date
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH); // Original date format from database
            Date date = sdf.parse(event.getDate());
            sdf.applyPattern("EEEE, MMMM d"); // Date format for: Saturday, January 12
            String finalDateFormat = sdf.format(date);
            tvDate.setText(finalDateFormat); // Set to textView
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvTime.setText(String.format("%s - %s WIB", event.getTime_start(), event.getTime_end()));
        tvPlace.setText(event.getLocation_name());
        tvAddress.setText(stringHelper.cutString(event.getLocation_address(), 63));
        ticketAvailable = Integer.parseInt(event.getTicket_total()) - Integer.parseInt(event.getTicket_sold());
        tvTicketAvailability.setText(String.format("%s / %s Tickets", ticketAvailable ,event.getTicket_total()));
        tvPoint.setText(String.format("%s TAK", event.getPoint()));
        tvPrice.setText(stringHelper.priceOrFree(event.getPrice()));

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

    private void showLessDescription() {
        if (!descriptionExpand) {
            descriptionExpand = true;
            tvSeeMore.setText("Show less");
            tvDescription.setText(event.getDescription());
        } else {
            descriptionExpand = false;
            tvSeeMore.setText("See more");
            tvDescription.setText(stringHelper.cutString(event.getDescription(), 183));
        }
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
