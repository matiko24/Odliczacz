package com.matekome.odliczacz.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matekome.odliczacz.R;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

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
        String elapsedTimeString = cursor.getString(2);

        DateTime elapsedTimeDataTime = DateTime.parse(elapsedTimeString);
        DateTime currentDate = new DateTime();

        Period differenceBetweenDates;
        String periodToDisplay = "";
        if (currentDate.isAfter(elapsedTimeDataTime)) {
            differenceBetweenDates = new Period(elapsedTimeDataTime, currentDate, PeriodType.yearMonthDayTime());
        } else {
            differenceBetweenDates = new Period(currentDate, elapsedTimeDataTime, PeriodType.yearMonthDayTime());
            periodToDisplay = "- ";
        }

        int differenceYears = differenceBetweenDates.getYears();
        int differenceMonths = differenceBetweenDates.getMonths();
        int differenceDays = differenceBetweenDates.getDays();
        int differenceHours = differenceBetweenDates.getHours();
        long differenceMinutes = differenceBetweenDates.getMinutes();
        long differenceSecunds = differenceBetweenDates.getSeconds();

        if (differenceYears > 0) {
            periodToDisplay += getYearString(differenceYears);
            periodToDisplay += getMonthString(differenceMonths);
            periodToDisplay += getDayString(differenceDays);
        } else if (differenceBetweenDates.getMonths() > 0) {
            periodToDisplay += getMonthString(differenceMonths);
            periodToDisplay += getDayString(differenceDays);
            periodToDisplay += getHourString(differenceHours);
        } else if (differenceBetweenDates.getDays() > 0) {
            periodToDisplay += getDayString(differenceDays);
            periodToDisplay += getHourString(differenceHours);
            periodToDisplay += getMinuteString(differenceMinutes);
        } else if (differenceBetweenDates.getHours() > 0) {
            periodToDisplay += getHourString(differenceHours);
            periodToDisplay += getMinuteString(differenceMinutes);
            periodToDisplay += getSecondsString(differenceSecunds);
        } else if (differenceBetweenDates.getMinutes() > 0) {
            periodToDisplay += getMinuteString(differenceMinutes);
            periodToDisplay += getSecondsString(differenceSecunds);
        } else {
            periodToDisplay += getSecondsString(differenceSecunds);
        }

        eventNameTV.setText(eventNameString);
        eventNameTV.setTypeface(null, Typeface.BOLD);
        elapsedTimeTV.setText(periodToDisplay);
    }

    @NonNull
    private String getSecondsString(long second) {
        String secundString = String.valueOf(second);
        if (second == 1)
            secundString += "sekunda";
        else if (second < 5)
            secundString += "sekundy";
        else
            secundString += "sekund";
        return secundString;
    }

    @NonNull
    private String getMinuteString(long minute) {
        String minuteString = String.valueOf(minute);
        int minuteLastNumber = (int) minute % 10;
        if (minute == 1)
            minuteString += "minuta ";
        else if (minuteLastNumber < 5 && minuteLastNumber != 0)
            minuteString += "minuty ";
        else
            minuteString += "minut ";
        return minuteString;
    }

    @NonNull
    private String getHourString(int hour) {
        String hourString = String.valueOf(hour);
        if (hour == 1)
            hourString += "godzina ";
        else if (hour < 5 && hour != 0 || hour > 21)
            hourString += "godziny ";
        else
            hourString += "godzin ";
        return hourString;
    }

    @NonNull
    private String getDayString(int day) {
        String dayString = String.valueOf(day);
        if (day == 1)
            dayString += "dzień ";
        else
            dayString += "dni ";
        return dayString;
    }

    @NonNull
    private String getMonthString(int month) {
        String monthString = String.valueOf(month);
        int monthLastNumber = month % 10;
        if (month == 1)
            monthString += "miesiąc ";
        else if (monthLastNumber < 5 && monthLastNumber != 0)
            monthString += "miesiące ";
        else
            monthString += "miesięcy ";
        return monthString;
    }

    @NonNull
    private String getYearString(int year) {
        String yearString = String.valueOf(year);
        int yearLastNumber = year % 10;
        if (year == 1)
            yearString += "rok ";
        else if (yearLastNumber < 5 && yearLastNumber != 0)
            yearString += "lata ";
        else
            yearString += "lat ";
        return yearString;
    }
}
