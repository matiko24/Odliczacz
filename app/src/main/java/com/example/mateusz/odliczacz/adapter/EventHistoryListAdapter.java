package com.example.mateusz.odliczacz.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mateusz.odliczacz.R;
import com.example.mateusz.odliczacz.data.Event;
import com.example.mateusz.odliczacz.data.MyContentProvider;
import com.example.mateusz.odliczacz.fragment.EventDetailFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class EventHistoryListAdapter extends CursorAdapter {
    private EventDetailFragment fragment;

    public EventHistoryListAdapter(Context context, Cursor c, int flags, EventDetailFragment fragment) {
        super(context, c, flags);
        this.fragment = fragment;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_event_history, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView date = (TextView) view.findViewById(R.id.event_history);
        final String evendId = String.valueOf(cursor.getLong(0));
        final String eventName = cursor.getString(1);
        String dateString = cursor.getString(2);

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
        DateTime eventDate = DateTime.parse(dateString);

        date.setText(eventDate.toString(dateTimeFormatter));

        ImageButton delete = (ImageButton) view.findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(context.getString(R.string.delete_confirmation)).setCancelable(false).setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.getContentResolver().delete(MyContentProvider.CONTENT_URI, Event.EventEntry._ID + "=" + evendId, null);

                        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName);
                        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
                        swapCursor(context.getContentResolver().query(uri, projection, null, null, null));

                        fragment.setLastEventValues();
                    }
                }).setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }
}
