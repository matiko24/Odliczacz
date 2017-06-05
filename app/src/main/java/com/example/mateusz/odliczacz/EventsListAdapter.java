package com.example.mateusz.odliczacz;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Created by Mateusz on 2017-05-30.
 */

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

        String eventName = cursor.getString(1);
        String elapsedTimeString = cursor.getString(2);

        DateTime elapsedTimeDataTime = DateTime.parse(elapsedTimeString);
        DateTime currentDate = new DateTime();

        Period differenceBetweenDates;
        String periodToDisplay = "";
        System.out.println(currentDate.isAfter(elapsedTimeDataTime));
        if (currentDate.isAfter(elapsedTimeDataTime)) {
            differenceBetweenDates = new Period(elapsedTimeDataTime, currentDate);
        } else {
            differenceBetweenDates = new Period(currentDate, elapsedTimeDataTime);
            periodToDisplay = "- ";
        }

        if (differenceBetweenDates.getYears() > 0) {
            periodToDisplay += +differenceBetweenDates.getYears();
            if (differenceBetweenDates.getYears() == 1)
                periodToDisplay += "rok ";
            else if (differenceBetweenDates.getYears() < 5)
                periodToDisplay += "lata ";
            else
                periodToDisplay += "lat ";
            periodToDisplay += differenceBetweenDates.getMonths() + "miesiecy " + differenceBetweenDates.getDays() + "dni";
        } else if (differenceBetweenDates.getMonths() > 0) {
            periodToDisplay += +differenceBetweenDates.getMonths();
            if (differenceBetweenDates.getMonths() == 1)
                periodToDisplay += "miesiąc ";
            else if (differenceBetweenDates.getMonths() < 5)
                periodToDisplay += "miesiące ";
            else
                periodToDisplay += "miesięcy ";
            periodToDisplay += differenceBetweenDates.getDays() + "dni " + differenceBetweenDates.getHours() + "godzin";
        } else if (differenceBetweenDates.getDays() > 0) {
            periodToDisplay += +differenceBetweenDates.getDays();
            if (differenceBetweenDates.getDays() == 1)
                periodToDisplay += "dzień ";
            else
                periodToDisplay += "dni ";
            periodToDisplay += differenceBetweenDates.getHours() + "godzin " + differenceBetweenDates.getMinutes() + "minut";
        } else if (differenceBetweenDates.getHours() > 0) {
            periodToDisplay += differenceBetweenDates.getHours();
            if (differenceBetweenDates.getHours() < 5)
                periodToDisplay += "godzina ";
            else
                periodToDisplay += "godzin ";
            periodToDisplay += differenceBetweenDates.getMinutes() + "minut " + differenceBetweenDates.getSeconds() + "sekund";
        } else if (differenceBetweenDates.getMinutes() > 0) {
            periodToDisplay += differenceBetweenDates.getMinutes();
            if (differenceBetweenDates.getMinutes() == 1)
                periodToDisplay += "minuta ";
            else if (differenceBetweenDates.getMinutes() < 5)
                periodToDisplay += "minuty ";
            else
                periodToDisplay += "minut ";
            periodToDisplay += differenceBetweenDates.getSeconds() + "sekund";
        } else {
            periodToDisplay += differenceBetweenDates.getSeconds();
            if (differenceBetweenDates.getSeconds() == 1)
                periodToDisplay += "sekunda";
            else if (differenceBetweenDates.getSeconds() < 5)
                periodToDisplay += "sekundy";
            else
                periodToDisplay += "sekund";
        }

        eventNameTV.setText(eventName);
        eventNameTV.setTypeface(null, Typeface.BOLD);
        elapsedTimeTV.setText(periodToDisplay);
    }
}
