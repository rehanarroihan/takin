package com.multazamgsd.takin.ui.event_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.GlideApp;

public class EventDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "extra_event";

    private ImageView ivEvent;
    private TextView tvTitle, tvPublisher, tvDescription; // Main Card
    private TextView tvDate, tvTime, tvPlace, tvAddress, tvTicketAvailability, tvPoint, tvPrice; // Schedule info card
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detail");

        // Set main info data
        ivEvent = findViewById(R.id.imageViewDetailEvent);
        tvTitle = findViewById(R.id.textViewDetailEventTitle);
        tvPublisher = findViewById(R.id.textViewDetailEventPublisher);
        tvDescription = findViewById(R.id.textViewDetailEventDesc);

        Event event = getIntent().getParcelableExtra(EXTRA_EVENT);
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
        tvTicketAvailability.setText(String.format("%s Tickets", event.getTicket_total()));

    }
}
