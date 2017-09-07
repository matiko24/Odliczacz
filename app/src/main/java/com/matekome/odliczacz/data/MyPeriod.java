package com.matekome.odliczacz.data;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class MyPeriod {

    public static String getPeriodToDisplay(String eventDateString) {
        DateTime eventDateDateTime = DateTime.parse(eventDateString);
        DateTime currentDate = new DateTime();

        Period differenceBetweenDates;
        String periodToDisplay = "";
        if (currentDate.isAfter(eventDateDateTime)) {
            differenceBetweenDates = new Period(eventDateDateTime, currentDate, PeriodType.yearMonthDayTime());
        } else {
            differenceBetweenDates = new Period(currentDate, eventDateDateTime, PeriodType.yearMonthDayTime());
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

        return periodToDisplay;
    }

    @NonNull
    private static String getSecondsString(long second) {
        String secundString = String.valueOf(second);
        if (second == 1)
            secundString += "sekunda";
        else if (second < 5 && second != 0)
            secundString += "sekundy";
        else
            secundString += "sekund";
        return secundString;
    }

    @NonNull
    private static String getMinuteString(long minute) {
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
    private static String getHourString(int hour) {
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
    private static String getDayString(int day) {
        String dayString = String.valueOf(day);
        if (day == 1)
            dayString += "dzień ";
        else
            dayString += "dni ";
        return dayString;
    }

    @NonNull
    private static String getMonthString(int month) {
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
    private static String getYearString(int year) {
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
