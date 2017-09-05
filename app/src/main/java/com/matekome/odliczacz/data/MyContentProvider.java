package com.matekome.odliczacz.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    private DBHelper dbHelper;

    private static final int EVENTS = 10;
    private static final int EVENT_ID = 20;

    private static final String AUTHORITY = "com.matekome.odliczacz";
    private static final String BASE_PATH = "events";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, EVENTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", EVENT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Event.EventEntry.EVENT_TABLE_NAME);
        String groupByString = null;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                groupByString = Event.EventEntry.EVENT_NAME;
                break;
            case EVENT_ID:
                queryBuilder.appendWhere(Event.EventEntry.EVENT_NAME + "='" + uri.getLastPathSegment() + "'");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupByString, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                id = db.insert(Event.EventEntry.EVENT_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                rowsDeleted = db.delete(Event.EventEntry.EVENT_TABLE_NAME, selection, selectionArgs);
                break;
            case EVENT_ID:
                String eventName = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(Event.EventEntry.EVENT_TABLE_NAME, Event.EventEntry.EVENT_NAME + "='" + eventName + "'", null);
                } else {
                    rowsDeleted = db.delete(Event.EventEntry.EVENT_TABLE_NAME, Event.EventEntry.EVENT_NAME + "='" + eventName + "' and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknow URI: " + uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                rowsUpdated = db.update(Event.EventEntry.EVENT_TABLE_NAME, values, selection, selectionArgs);
                break;
            case EVENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(Event.EventEntry.EVENT_TABLE_NAME, values, Event.EventEntry._ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(Event.EventEntry.EVENT_TABLE_NAME, values, Event.EventEntry._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
