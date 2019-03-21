package com.example.xyzreader.ui;

import android.database.Cursor;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

abstract class CursorCallback<C extends Cursor> extends DiffUtil.Callback {
    private final C newCursor;
    private final C oldCursor;

    CursorCallback(C newCursor, C oldCursor) {
        this.newCursor = newCursor;
        this.oldCursor = oldCursor;
    }

    @Override
    public int getOldListSize() {
        return oldCursor == null ? 0 : oldCursor.getCount();
    }

    @Override
    public int getNewListSize() {
        return newCursor == null ? 0 : newCursor.getCount();
    }

    @Override
    public final boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCursor.getColumnCount() == newCursor.getColumnCount() &&
                moveCursorsToPosition(oldItemPosition, newItemPosition) &&
                areCursorRowsTheSame(oldCursor, newCursor);
    }

    @Override
    public final boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCursor.getColumnCount() == newCursor.getColumnCount() &&
                moveCursorsToPosition(oldItemPosition, newItemPosition) &&
                areRowContentsTheSame(oldCursor, newCursor);
    }

    @Nullable
    @Override
    public final Object getChangePayload(int oldItemPosition, int newItemPosition) {
        moveCursorsToPosition(oldItemPosition, newItemPosition);
        return getChangePayload(newCursor, oldCursor);
    }

    @Nullable
    public Object getChangePayload(C newCursor, C oldCursor) {
        return null;
    }

    private boolean moveCursorsToPosition(int oldItemPosition, int newItemPosition) {
        boolean newMoved = newCursor.moveToPosition(newItemPosition);
        boolean oldMoved = oldCursor.moveToPosition(oldItemPosition);
        return newMoved && oldMoved;
    }

    /**
     * Cursors are already moved to positions where you should obtain data by row.
     * Checks if contents at row are same
     *
     * @param oldCursor Old cursor object
     * @param newCursor New cursor object
     * @return See DiffUtil
     */
    public abstract boolean areRowContentsTheSame(Cursor oldCursor, Cursor newCursor);

    /**
     * Cursors are already moved to positions where you should obtain data from row
     * Checks if rows are the same, ideally, check by unique id
     *
     * @param oldCursor Old cursor object
     * @param newCursor New cursor object
     * @return See DiffUtil
     */
    public abstract boolean areCursorRowsTheSame(Cursor oldCursor, Cursor newCursor);
}
