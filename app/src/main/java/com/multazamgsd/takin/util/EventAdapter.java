package com.multazamgsd.takin.util;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Activity activity;
    private ArrayList<Event> events = new ArrayList<>();
    private eventAdapterListener mEventAdapterListener;

    public EventAdapter(Activity activity, eventAdapterListener eventAdapterListener) {
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
        View view = LayoutInflater.from(activity).inflate(R.layout.event_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.tvTitle.setText(getEventList().get(position).getTitle());
        holder.tvLocation.setText(getEventList().get(position).getLocation_name());
        holder.tvTime.setText(getEventList().get(position).getTime_start());

        //Parsing date to readable format
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.ENGLISH); // Original date format from database
            Date date = sdf.parse(getEventList().get(position).getDate());
            sdf.applyPattern("EEE, d"); // Date format for: Tue, 24 Mar
            String finalDateFormat = sdf.format(date);
            holder.tvDate.setText(finalDateFormat); // Set to textView
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set image
        GlideApp.with(holder.itemView.getContext())
                .load(getEventList().get(position).getPhoto_url())
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

            itemView.setOnClickListener(v -> mEventAdapterListener.onEventClick(getAdapterPosition()));
            btShare.setOnClickListener(v -> mEventAdapterListener.onEventShare(getAdapterPosition()));
            btLove.setOnClickListener(v -> mEventAdapterListener.onEventLike(getAdapterPosition()));
        }
    }
}
