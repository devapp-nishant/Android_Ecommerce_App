package com.hdddekho.thirty.percent.customer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Utils.MediaItem;

import java.util.List;

public class MediaSliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MediaItem> mediaItems;
    private ViewPager2 viewPager;

    public MediaSliderAdapter(Context context, List<MediaItem> mediaItems, ViewPager2 viewPager) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MediaItem.TYPE_IMAGE) {
            View itemView = inflater.inflate(R.layout.image_item, parent, false);
            return new ImageViewHolder(itemView);
        } else if (viewType == MediaItem.TYPE_VIDEO) {
            View itemView = inflater.inflate(R.layout.video_item, parent, false);
            return new VideoViewHolder(itemView);
        }

        // Handle other cases or return a default ViewHolder
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);

        if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            Glide.with(context).load(mediaItem.getUrl()).into(imageViewHolder.photoView);
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            videoViewHolder.progressBar.setVisibility(View.VISIBLE);
            videoViewHolder.videoView.setVideoURI(Uri.parse(mediaItem.getUrl()));

            MediaController mediaController = new MediaController(context);
            videoViewHolder.videoView.setMediaController(mediaController);

            videoViewHolder.videoView.setOnPreparedListener(mp -> {
                videoViewHolder.progressBar.setVisibility(View.GONE);
                videoViewHolder.videoView.start();
                videoViewHolder.videoView.setOnTouchListener((v, event) -> {
                    if (videoViewHolder.videoView.isPlaying()) {
                        videoViewHolder.videoView.pause();
                    } else {
                        videoViewHolder.videoView.start();
                    }
                    return false;
                });
            });

            videoViewHolder.videoView.setOnCompletionListener(mp -> {
                if (position < mediaItems.size() - 1) {
                    viewPager.setCurrentItem(position + 1);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mediaItems.get(position).getType();
    }

    // ViewHolder classes
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}

