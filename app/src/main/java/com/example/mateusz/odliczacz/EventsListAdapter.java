package com.example.mateusz.odliczacz;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mateusz on 2017-05-30.
 */

public class EventsListAdapter extends CursorAdapter {
    public EventsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView eventNameTV = (TextView) view.findViewById(R.id.event_name);
        TextView elapsedTimeTV = (TextView) view.findViewById(R.id.event_elapsed_time);

        String eventName = cursor.getString(1);
        String elapsedTime = cursor.getString(2);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date eventDate = null;
        try {
            eventDate = sdf.parse(elapsedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentDate = new Date();
        long different = currentDate.getTime() - eventDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        eventNameTV.setText(eventName);
        eventNameTV.setTypeface(null, Typeface.BOLD);
        elapsedTimeTV.setText(elapsedDays + "dni " + elapsedHours + "godzin " + elapsedMinutes + "minut " /*+ elapsedSeconds + "sekund"*/);
    }
}
