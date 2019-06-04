package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.utils.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {

    private static final String TAG = ArticleListActivity.class.toString();

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat outputFormat = new SimpleDateFormat();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);
    private final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private final Context mContext;
    private Cursor mCursor;

    ArticleListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);

        final ArticleListViewHolder articleListViewHolder = new ArticleListViewHolder(view);

        view.setOnClickListener(view1 -> {

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity) mContext, articleListViewHolder.thumbnailView, mContext.getResources()
                            .getString(R.string.transition_image_name));

            Bundle bundle = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext)
                    .toBundle();

            mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                    ItemsContract.Items.buildItemUri(getItemId(articleListViewHolder.getAdapterPosition()))), bundle);
        });
        return articleListViewHolder;
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));

        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        }

        GlideApp.with(mContext)
                .asBitmap()
                .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            Palette palette = Palette.from(resource).generate();
                            // Use generated instance
                            int defaultColor = 0xFF333333;
                            int color = palette.getMutedColor(defaultColor);
                            holder.itemView.setBackgroundColor(color);
                        }

                        return false;
                    }
                })
                .into(holder.thumbnailView);

        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

    }

    void updateList(Cursor newCursor) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CursorCallback<Cursor>(this.mCursor, newCursor) {
            @Override
            public boolean areRowContentsTheSame(Cursor oldCursor, Cursor newCursor) {
                boolean contentsTheSame = (oldCursor.getString(ArticleLoader.Query.TITLE)
                        .equals(newCursor.getString(ArticleLoader.Query.TITLE)));
                Log.i("ArticleListAdapter", "contents the same: " + contentsTheSame);
                return contentsTheSame;
            }

            // as there is no unique id in the data, we simply regard the
            // article title as the unique id, as article titles are all different.
            @Override
            public boolean areCursorRowsTheSame(Cursor oldCursor, Cursor newCursor) {
                boolean cursorRowsTheSame = (oldCursor.getString(ArticleLoader.Query.TITLE)
                        .equals(newCursor.getString(ArticleLoader.Query.TITLE)));
                Log.i("ArticleListAdapter", "cursor rows the same: " + cursorRowsTheSame);
                return cursorRowsTheSame;
            }
        });
        diffResult.dispatchUpdatesTo(this);
        this.mCursor = newCursor;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

}