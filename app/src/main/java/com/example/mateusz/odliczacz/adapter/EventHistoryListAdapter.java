package com.example.mateusz.odliczacz.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateusz.odliczacz.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class EventHistoryListAdapter extends CursorAdapter {
    public EventHistoryListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.event_history_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView date = (TextView) view.findViewById(R.id.event_history);
        String dateString = cursor.getString(2);

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY hh:mm");
        DateTime eventDate = DateTime.parse(dateString);

        date.setText(eventDate.toString(dateTimeFormatter));
    }
}
