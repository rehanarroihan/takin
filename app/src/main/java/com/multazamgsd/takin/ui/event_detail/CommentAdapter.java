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
import com.multazamgsd.takin.model.Comment;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.util.GlideApp;
import com.multazamgsd.takin.util.StringHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.EventViewHolder> {
    private final Activity activity;
    private ArrayList<Comment> comment = new ArrayList<>();
    private CommentAdapterListener mCommentAdapterListner;

    public CommentAdapter(Activity activity, CommentAdapterListener mCommentAdapterListner) {
        this.activity = activity;
        this.mCommentAdapterListner = mCommentAdapterListner;
    }

    private ArrayList<Comment> getCommentList() {
        return comment;
    }

    public void setListComment(ArrayList<Comment> listComment) {
        if (listComment == null) return;
        this.comment.clear();
        this.comment.addAll(listComment);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        StringHelper stringHelper = new StringHelper();
        Comment commentItem = getCommentList().get(position);
        String nickName = commentItem.getNick_name();

        // Setting image
        if (!commentItem.getPict().equals("")) {
            GlideApp.with(holder.itemView.getContext())
                    .load(commentItem.getNick_name())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_image_grey_24dp)
                            .error(R.drawable.ic_image_grey_24dp))
                    .into(holder.ivPicture);
        }
        if (nickName.equals("")) nickName = "User";
        holder.tvUsername.setText(stringHelper.cutString(nickName, 18));
        holder.tvTime.setText(commentItem.getTime());
        holder.tvComment.setText(commentItem.getComment());
        holder.tvLike.setText(commentItem.getLike());
        holder.tvDislike.setText(commentItem.getDislike());
    }

    @Override
    public int getItemCount() {
        return getCommentList() == null ? 0 : getCommentList().size();
    }

    public interface CommentAdapterListener {
        void onCommentLike(int itemPosition);
        void onCommentDislike(int itemPosition);
        void onCommentReply(int itemPosition);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPicture;
        ImageView btReply;
        TextView tvUsername, tvTime, tvComment, tvLike, tvDislike;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPicture = itemView.findViewById(R.id.imageViewUserComment);
            tvUsername = itemView.findViewById(R.id.textViewUserName);
            tvTime = itemView.findViewById(R.id.textViewCommentTime);
            tvComment = itemView.findViewById(R.id.textViewComment);
            tvLike = itemView.findViewById(R.id.textViewLike);
            tvDislike = itemView.findViewById(R.id.textViewDislike);
            btReply = itemView.findViewById(R.id.buttonReply);

            tvLike.setOnClickListener(v -> mCommentAdapterListner.onCommentLike(getAdapterPosition()));
            tvDislike.setOnClickListener(v -> mCommentAdapterListner.onCommentDislike(getAdapterPosition()));
            btReply.setOnClickListener(v -> mCommentAdapterListner.onCommentReply(getAdapterPosition()));
        }
    }
}
