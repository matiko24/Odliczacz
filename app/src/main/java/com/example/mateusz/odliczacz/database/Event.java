package com.example.mateusz.odliczacz.database;

import android.provider.BaseColumns;

/**
 * Created by Mateusz on 2017-05-30.
 */

public final class Event {

    private Event() {
    }

    public static class EventEntry implements BaseColumns {
        public static final String EVENT_TABLE_NAME = "item";
        public static final String EVENT_NAME = "name";
        public static final String EVENT_DATE = "date";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + EventEntry.EVENT_TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.EVENT_NAME + " TEXT," +
                    EventEntry.EVENT_DATE + " TEXT)";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS + " + EventEntry.EVENT_TABLE_NAME;

}
