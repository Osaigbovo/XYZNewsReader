package com.example.xyzreader.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.widgets.DynamicImageView;

public class ArticleListViewHolder extends RecyclerView.ViewHolder {
    public DynamicImageView thumbnailView;
    public TextView titleView;
    public TextView subtitleView;

    public ArticleListViewHolder(View view) {
        super(view);
        thumbnailView = view.findViewById(R.id.thumbnail);
        titleView = view.findViewById(R.id.article_title);
        subtitleView = view.findViewById(R.id.article_subtitle);
    }
}