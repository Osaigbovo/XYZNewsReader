package com.example.xyzreader.ui;

import android.view.View;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.widgets.DynamicHeightNetworkImageView;

import androidx.recyclerview.widget.RecyclerView;

public class ArticleListViewHolder extends RecyclerView.ViewHolder {
    public DynamicHeightNetworkImageView thumbnailView;
    public TextView titleView;
    public TextView subtitleView;

    public ArticleListViewHolder(View view) {
        super(view);
        thumbnailView = view.findViewById(R.id.thumbnail);
        titleView = view.findViewById(R.id.article_title);
        subtitleView = view.findViewById(R.id.article_subtitle);
    }
}