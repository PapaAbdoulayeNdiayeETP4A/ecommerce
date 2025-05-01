package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.Review;
import com.example.ecommerce.utils.DateUtils;

import java.util.Date;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivUserProfile;
        private final TextView tvUserName;
        private final TextView tvReviewDate;
        private final RatingBar ratingBar;
        private final TextView tvReviewText;
        private final TextView tvVerifiedPurchase;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserProfile = itemView.findViewById(R.id.iv_user_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvReviewDate = itemView.findViewById(R.id.tv_review_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            tvReviewText = itemView.findViewById(R.id.tv_review_text);
            tvVerifiedPurchase = itemView.findViewById(R.id.tv_verified_purchase);
        }

        void bind(Review review) {
            tvUserName.setText(review.getUserDisplayName());
            tvReviewDate.setText(DateUtils.formatDate(review.getDate()));
            ratingBar.setRating(review.getRating());
            tvReviewText.setText(review.getText());

            // Afficher l'icône "achat vérifié" si applicable
            if (review.isVerified()) {
                tvVerifiedPurchase.setVisibility(View.VISIBLE);
            } else {
                tvVerifiedPurchase.setVisibility(View.GONE);
            }

            // Charger l'image de profil de l'utilisateur
            if (review.getUserProfileImageUrl() != null && !review.getUserProfileImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(review.getUserProfileImageUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .circleCrop()
                        .into(ivUserProfile);
            } else {
                ivUserProfile.setImageResource(R.drawable.profile_placeholder);
            }
        }
    }
}