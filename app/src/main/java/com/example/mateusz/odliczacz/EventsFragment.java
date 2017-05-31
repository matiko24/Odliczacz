package com.example.mateusz.odliczacz;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mateusz.odliczacz.database.Event;
import com.example.mateusz.odliczacz.database.MyContentProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mateusz on 2017-05-30.
 */

public class EventsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    EventsListAdapter eventsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items_list, container, false);

        FloatingActionButton addNewEventFAB = (FloatingActionButton) view.findViewById(R.id.fab);
        FloatingActionButton refreshEventsListFAB = (FloatingActionButton) view.findViewById(R.id.refresh);

        refreshEventsListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoaderManager().restartLoader(0, null, EventsFragment.this);
            }
        });
        addNewEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE};
        Cursor eventsCursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, projection, null, null, null);

        eventsListAdapter = new EventsListAdapter(getContext(), eventsCursor, 0);
        setListAdapter(eventsListAdapter);
        getLoaderManager().initLoader(0, null, this);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ContentValues values = new ContentValues();
                values.put(Event.EventEntry.EVENT_DATE, getCurrentData());
                String[] selectionArg = {String.valueOf(eventsListAdapter.getCursor().getLong(0))};

                getContext().getContentResolver().update(MyContentProvider.CONTENT_URI, values, Event.EventEntry._ID + "= ?", selectionArg);
                getLoaderManager().restartLoader(0, null, EventsFragment.this);

                Toast.makeText(getActivity(), "Data została zresetowana", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE};
        Loader<Cursor> loader = new CursorLoader(this.getContext(), MyContentProvider.CONTENT_URI, projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        eventsListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        eventsListAdapter.swapCursor(null);
    }

    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText newEventName = (EditText) promptView.findViewById(R.id.edittext);
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues values = new ContentValues();
                        values.put(Event.EventEntry.EVENT_NAME, newEventName.getText().toString());
                        values.put(Event.EventEntry.EVENT_DATE, getCurrentData());
                        getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                        getLoaderManager().restartLoader(0, null, EventsFragment.this);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private String getCurrentData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(new Date());
    }
}
