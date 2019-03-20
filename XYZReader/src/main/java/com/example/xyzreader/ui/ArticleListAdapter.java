package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.utils.ImageLoaderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {

    private static final String TAG = ArticleListActivity.class.toString();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);
    // Use default locale format
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private final Context mContext;
    private Cursor mCursor;

    ArticleListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        mCursor = cursor;
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

        final ArticleListViewHolder vh = new ArticleListViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
            }
        });
        return vh;
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
        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(mContext).getImageLoader());
        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    void updateList(Cursor newCursor) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CursorCallback<Cursor>(this.mCursor, newCursor) {
            @Override
            public boolean areRowContentsTheSame(Cursor oldCursor, Cursor newCursor) {
                boolean contentsTheSame = (oldCursor.getString(ArticleLoader.Query.TITLE).equals(newCursor.getString(ArticleLoader.Query.TITLE)));
                Log.i("ArticleListAdapter", "contents the same: " + contentsTheSame);
                return contentsTheSame;
            }

            // as there is no unique id in the data, we simply regard the
            // article title as the unique id, as article titles are all different.
            @Override
            public boolean areCursorRowsTheSame(Cursor oldCursor, Cursor newCursor) {
                boolean cursorRowsTheSame = (oldCursor.getString(ArticleLoader.Query.TITLE).equals(newCursor.getString(ArticleLoader.Query.TITLE)));
                Log.i("ArticleListAdapter", "cursor rows the same: " + cursorRowsTheSame);
                return cursorRowsTheSame;
            }
        });
        diffResult.dispatchUpdatesTo(this);
        this.mCursor = newCursor;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

}