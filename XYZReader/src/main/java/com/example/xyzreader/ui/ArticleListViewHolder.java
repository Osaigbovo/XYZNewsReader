package com.example.xyzreader.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.widgets.DynamicImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.article_title) public TextView titleView;
    @BindView(R.id.article_subtitle) public TextView subtitleView;
    @BindView(R.id.thumbnail) public DynamicImageView thumbnailView;

    public ArticleListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, itemView);
    }

}