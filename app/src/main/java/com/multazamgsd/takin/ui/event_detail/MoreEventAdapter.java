package com.multazamgsd.takin.ui.event_detail;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.GlideApp;
import com.multazamgsd.takin.util.StringHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoreEventAdapter extends RecyclerView.Adapter<MoreEventAdapter.EventViewHolder> {
    private final Activity activity;
    private ArrayList<Event> events = new ArrayList<>();
    private eventAdapterListener mEventAdapterListener;

    public MoreEventAdapter(Activity activity, eventAdapterListener eventAdapterListener) {
        this.activity = activity;
        this.mEventAdapterListener = eventAdapterListener;
    }

    private ArrayList<Event> getEventList() {
        return events;
    }

    public void setListEvents(ArrayList<Event> listEvents) {
        if (listEvents == null) return;
        this.events.clear();
        this.events.addAll(listEvents);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.more_event_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        StringHelper stringHelper = new StringHelper();
        Event eventItem = getEventList().get(position);

        holder.tvTitle.setText(stringHelper.cutString(eventItem.getTitle(), 30));
        holder.tvLocation.setText(stringHelper.cutString(eventItem.getLocation_name(), 18));
        holder.tvPrice.setText(stringHelper.priceOrFree(eventItem.getPrice()));

        //Parsing date to readable format
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH); // Original date format from database
            Date date = sdf.parse(eventItem.getDate());
            sdf.applyPattern("dd"); // Date format for: Tue, 24 Mar
            holder.tvDate.setText(sdf.format(date)); // Set to textView
            sdf.applyPattern("MMM");
            holder.tvMonth.setText(sdf.format(date));
            sdf.applyPattern("yyyy");
            holder.tvYear.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set image
        GlideApp.with(holder.itemView.getContext())
                .load(eventItem.getPhoto_url())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_image_grey_24dp)
                        .error(R.drawable.ic_image_grey_24dp))
                .into(holder.ivBanner);
    }

    @Override
    public int getItemCount() {
        return getEventList() == null ? 0 : getEventList().size();
    }

    public interface eventAdapterListener {
        void onEventClick(int itemPosition);
        void onEventLike(int itemPosition);
        void onEventShare(int itemPosition);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvLocation, tvDate, tvMonth, tvYear, tvPrice;
        final ImageView ivBanner, btShare, btLove;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textViewEventTitle);
            tvLocation = itemView.findViewById(R.id.textViewEventLocationName);
            tvDate = itemView.findViewById(R.id.textViewDate);
            tvMonth = itemView.findViewById(R.id.textViewMonth);
            tvYear = itemView.findViewById(R.id.textViewYear);
            tvPrice = itemView.findViewById(R.id.textViewEventPrice);
            ivBanner = itemView.findViewById(R.id.imageViewEventBanner);
            btShare = itemView.findViewById(R.id.buttonShare);
            btLove = itemView.findViewById(R.id.buttonLove);

            itemView.setOnClickListener(v -> mEventAdapterListener.onEventClick(getAdapterPosition()));
            btShare.setOnClickListener(v -> mEventAdapterListener.onEventShare(getAdapterPosition()));
            btLove.setOnClickListener(v -> mEventAdapterListener.onEventLike(getAdapterPosition()));
        }
    }
}
