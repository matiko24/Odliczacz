package com.example.mateusz.odliczacz.data;

import android.provider.BaseColumns;

public final class Event {

    private Event() {
    }

    public static class EventEntry implements BaseColumns {
        public static final String EVENT_TABLE_NAME = "item";
        public static final String EVENT_NAME = "name";
        public static final String EVENT_DATE = "date";
        public static final String EVENT_DESCRIPTION = "description";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + EventEntry.EVENT_TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.EVENT_NAME + " TEXT," +
                    EventEntry.EVENT_DATE + " TEXT," +
                    EventEntry.EVENT_DESCRIPTION + " TEXT)";

    public static final String SQL_UPGRATE_2 = "ALTER TABLE " + EventEntry.EVENT_TABLE_NAME + " ADD COLUMN " + EventEntry.EVENT_DESCRIPTION + " TEXT";

}
