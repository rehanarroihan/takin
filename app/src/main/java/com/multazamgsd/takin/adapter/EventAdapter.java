package com.multazamgsd.takin.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.GlideApp;

public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventViewHolder> {
    private eventAdapterListener mEventAdapterListener;

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options, eventAdapterListener mEventAdapterListener) {
        super(options);
        this.mEventAdapterListener = mEventAdapterListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder holder, int i, @NonNull Event event) {
        holder.tvTitle.setText(event.getTitle());
        holder.tvLocation.setText(event.getLocation_name());
        holder.tvDate.setText(event.getDate());
        holder.tvTime.setText(event.getTime_start());

        Log.d("Adapter", event.getTitle());

        GlideApp.with(holder.itemView.getContext())
                .load(event.getPhoto_url())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder))
                .into(holder.ivBanner);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.d("Adapter", e.getMessage());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("Adapter", "Data changed");
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
