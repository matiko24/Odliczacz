package com.matekome.odliczacz.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.adapter.EventHistoryListAdapter;
import com.matekome.odliczacz.data.Event;
import com.matekome.odliczacz.data.MyContentProvider;

public class EventOccurrenceFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    EventHistoryListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_occurances, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String eventName = getActivity().getIntent().getExtras().getString("eventName");
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName);
        Cursor eventsCursor = getActivity().getContentResolver().query(uri, projection, null, null, Event.EventEntry.EVENT_DATE + " DESC");

        EventDetailFragment fragment = (EventDetailFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.event_detail_fragment);
        adapter = new EventHistoryListAdapter(getContext(), eventsCursor, 0, fragment);

        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) l.getAdapter().getItem(position);
        EventDetailFragment fragment = (EventDetailFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.event_detail_fragment);

        fragment.setEventValues(cursor.getLong(0), cursor.getString(2), cursor.getString(3));
        fragment.setElapsedTimeTextView(cursor.getString(2));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + getActivity().getIntent().getExtras().getString("eventName"));
        Loader<Cursor> loader = new CursorLoader(getContext(), uri, projection, null, null, Event.EventEntry.EVENT_DATE + " DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, EventOccurrenceFragment.this);
    }
}
