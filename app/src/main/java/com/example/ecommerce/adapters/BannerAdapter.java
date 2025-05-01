package com.example.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final Context context;
    private List<Banner> banners;
    private final OnBannerClickListener listener;

    public BannerAdapter(Context context, List<Banner> banners, OnBannerClickListener listener) {
        this.context = context;
        this.banners = banners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.bind(banner);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public void updateBanners(List<Banner> banners) {
        this.banners = banners;
        notifyDataSetChanged();
    }

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBanner;
        private final TextView tvTitle;
        private final TextView tvDescription;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvDescription = itemView.findViewById(R.id.tv_banner_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBannerClick(banners.get(position));
                }
            });
        }

        public void bind(Banner banner) {
            tvTitle.setText(banner.getTitle());
            tvDescription.setText(banner.getDescription());

            // Charger l'image de la bannière
            if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(banner.getImageUrl())
                        .placeholder(R.drawable.placeholder_banner)
                        .error(R.drawable.error_banner)
                        .into(ivBanner);
            } else {
                // Image par défaut si aucune image n'est disponible
                ivBanner.setImageResource(R.drawable.placeholder_banner);
            }
        }
    }
}