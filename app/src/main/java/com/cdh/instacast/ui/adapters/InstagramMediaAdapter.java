package com.cdh.instacast.ui.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cdh.instacast.R;
import com.cdh.instacast.pojo.InstagramMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dudu_Cohen on 19/08/2017.
 */

public class InstagramMediaAdapter extends RecyclerView.Adapter<InstagramMediaAdapter.MediaViewHolder> {

    private List<InstagramMedia.DataEntity> mInstagramMediaList;

    public void setMediaList(InstagramMedia instagramMedia) {
        if (instagramMedia != null) {
            mInstagramMediaList = instagramMedia.getData();
            notifyDataSetChanged();
        }
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.instagram_media_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        holder.setMedia(mInstagramMediaList.get(position));
    }

    @Override
    public int getItemCount() {
        return mInstagramMediaList != null ? mInstagramMediaList.size() : 0;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.media_main_image)
        ImageView mainImage;
        @BindView(R.id.media_user_image)
        ImageView userImage;
        @BindView(R.id.media_user_name)
        AppCompatTextView userName;
        @BindView(R.id.media_user_comments)
        AppCompatTextView comments;

        public MediaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setMedia(InstagramMedia.DataEntity media) {

            if (media != null) {

                // load main image
                loadImage(mainImage, media.getImages().getStandard_resolution().getUrl());

                // load user image
                loadImage(userImage, media.getUser().getProfile_picture());

                // set user name
                userName.setText(media.getUser().getUsername());

                // set comments
                comments.setText(String.format("Comments: %d", media.getComments().getCount()));

            }
        }

        private void loadImage(ImageView imageView, String imageUrl) {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .into(imageView);
        }

    }
}
