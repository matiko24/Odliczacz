package com.matekome.odliczacz.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.data.MyPeriod;

public class EventsListAdapter extends CursorAdapter {
    public EventsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_event_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView eventNameTV = (TextView) view.findViewById(R.id.event_name);
        TextView elapsedTimeTV = (TextView) view.findViewById(R.id.event_elapsed_time);

        String eventNameString = cursor.getString(1);
        String eventDateString = cursor.getString(2);

        String periodToDisplay = MyPeriod.getPeriodToDisplay(eventDateString);

        eventNameTV.setText(eventNameString);
        eventNameTV.setTypeface(null, Typeface.BOLD);
        elapsedTimeTV.setText(periodToDisplay);
    }
}
