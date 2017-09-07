package com.matekome.odliczacz.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.activity.EventDetailActivity;
import com.matekome.odliczacz.adapter.EventsListAdapter;
import com.matekome.odliczacz.data.Event;
import com.matekome.odliczacz.data.MyContentProvider;

import org.joda.time.DateTime;

public class EventsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    EventsListAdapter eventsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        return view;
    }

    public void refreshEvents() {
        getLoaderManager().restartLoader(0, null, EventsFragment.this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventsListAdapter = new EventsListAdapter(getContext(), getEventsCursor(), 0);
        setListAdapter(eventsListAdapter);
        getLoaderManager().initLoader(0, null, this);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ContentValues values = new ContentValues();
                values.put(Event.EventEntry.EVENT_NAME, eventsListAdapter.getCursor().getString(1));
                values.put(Event.EventEntry.EVENT_DATE, getCurrentData());

                getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                refreshEvents();

                Toast.makeText(getActivity(), getString(R.string.toast_reset_date), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                intent.putExtra("eventName", eventsListAdapter.getCursor().getString(1));
                startActivity(intent);
            }
        });

    }

    private Cursor getEventsCursor() {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE};
        return getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, projection, Event.EventEntry.EVENT_NAME, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE};
        Loader<Cursor> loader = new CursorLoader(this.getContext(), MyContentProvider.CONTENT_URI, projection, null, null, Event.EventEntry.EVENT_DATE + " DESC");
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

    public void showInputDialogToAddNewEvent() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.add_event_input_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final CheckBox isNowEventCheckBox = (CheckBox) promptView.findViewById(R.id.is_now_event_check_box);
        final DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) promptView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);

        isNowEventCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                } else {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                }
            }
        });

        final EditText newEventNameEditText = (EditText) promptView.findViewById(R.id.new_event_name);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newEventNameString = newEventNameEditText.getText().toString();

                if (TextUtils.isEmpty(newEventNameString)) {
                    Toast.makeText(getContext(), getString(R.string.toast_event_without_name), Toast.LENGTH_SHORT).show();
                } else if (ifSuchNameEventExist(newEventNameString)) {
                    Toast.makeText(getContext(), getString(R.string.toast_event_with_name_which_exists), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(Event.EventEntry.EVENT_NAME, newEventNameString);
                    if (isNowEventCheckBox.isChecked()) {
                        values.put(Event.EventEntry.EVENT_DATE, getCurrentData());
                    } else {
                        int selectedHour, selectedMinute;

                        if (Build.VERSION.SDK_INT >= 23) {
                            selectedHour = timePicker.getHour();
                            selectedMinute = timePicker.getMinute();
                        } else {
                            selectedHour = timePicker.getCurrentHour();
                            selectedMinute = timePicker.getCurrentMinute();
                        }

                        DateTime userSetDate = new DateTime(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), selectedHour, selectedMinute);
                        values.put(Event.EventEntry.EVENT_DATE, userSetDate.toString());
                    }
                    getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                    refreshEvents();
                }
            }

        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private String getCurrentData() {
        return new DateTime().toString();
    }

    private boolean ifSuchNameEventExist(String eventName) {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName), new String[]{"name"}, null, null, null);
        boolean isExist = false;
        if (cursor != null) {
            isExist = cursor.getCount() > 0;
            cursor.close();
        }
        return isExist;
    }
}
