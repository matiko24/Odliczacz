package com.matekome.odliczacz.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.activity.MainActivity;
import com.matekome.odliczacz.adapter.EventHistoryListAdapter;
import com.matekome.odliczacz.data.Event;
import com.matekome.odliczacz.data.MyContentProvider;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class EventDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView durationTextView;
    TextView eventDateTextView;
    EditText eventDescriptionEditText;
    Spinner spinner;
    EventHistoryListAdapter adapter;
    ImageButton saveDescriptionButton;
    ListView eventHistoryListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        String event_name = getActivity().getIntent().getExtras().getString("event_name");
        getActivity().setTitle(event_name);

        durationTextView = (TextView) view.findViewById(R.id.duration);
        eventDateTextView = (TextView) view.findViewById(R.id.event_date_text_view);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        eventDescriptionEditText = (EditText) view.findViewById(R.id.event_description);
        saveDescriptionButton = (ImageButton) view.findViewById(R.id.save_description_button);
        eventHistoryListView = (ListView) view.findViewById(R.id.event_history_list_view);

        setLastEventValues();

        saveDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventDescriptionEditText.getTag());
                ContentValues values = new ContentValues();

                values.put(Event.EventEntry.EVENT_DESCRIPTION, eventDescriptionEditText.getText().toString());
                getActivity().getContentResolver().update(uri, values, null, null);

                eventDescriptionEditText.clearFocus();
                hideKeyboard();
                getLoaderManager().restartLoader(0, null, EventDetailFragment.this);

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setDurationTextView(position, eventDateTextView.getTag().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new EventHistoryListAdapter(getContext(), getCursor(), 0, EventDetailFragment.this);
        eventHistoryListView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        eventHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                setEventValues(cursor.getLong(0), cursor.getString(2), cursor.getString(3));
                setDurationTextView(spinner.getSelectedItemPosition(), cursor.getString(2));
            }
        });

        return view;
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void deleteEvent() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(getString(R.string.delete_confirmation)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + getActivity().getTitle());
                getActivity().getContentResolver().delete(uri, null, null);

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void editEventName() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.edit_event_name_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText editingEventName = (EditText) promptView.findViewById(R.id.editing_event_name);

        alertDialogBuilder.setMessage(getString(R.string.change_event_name_information)).setCancelable(false).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEventName = editingEventName.getText().toString();

                if (newEventName.equals("")) {
                    Toast.makeText(getContext(), "Nie można zapisać wydarzenia bez nazwy", Toast.LENGTH_SHORT).show();
                } else if (ifSuchNameEventExist(newEventName)) {
                    Toast.makeText(getContext(), "Wydarzenie o takiej nazwie już istnieje", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(Event.EventEntry.EVENT_NAME, newEventName);
                    getActivity().getContentResolver().update(MyContentProvider.CONTENT_URI, values, Event.EventEntry.EVENT_NAME + "='" + getActivity().getTitle() + "'", null);
                    getActivity().setTitle(editingEventName.getText().toString());
                    getLoaderManager().restartLoader(0, null, EventDetailFragment.this);
                }
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean ifSuchNameEventExist(String eventName) {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName), new String[]{"name"}, null, null, null);
        boolean isExist = false;
        if (cursor != null) {
            isExist = cursor.getCount() > 0;
            cursor.close();
        }
        return isExist;
    }

    private void setDurationTextView(int position, String eventDateString) {
        DateTime currentDate = new DateTime();
        DateTime eventDate = DateTime.parse(eventDateString);
        switch (position) {
            case 0:
                durationTextView.setText(String.valueOf(Years.yearsBetween(eventDate, currentDate).getYears()));
                break;
            case 1:
                durationTextView.setText(String.valueOf(Months.monthsBetween(eventDate, currentDate).getMonths()));
                break;
            case 2:
                durationTextView.setText(String.valueOf(Days.daysBetween(eventDate, currentDate).getDays()));
                break;
            case 3:
                durationTextView.setText(String.valueOf(Hours.hoursBetween(eventDate, currentDate).getHours()));
                break;
            case 4:
                durationTextView.setText(String.valueOf(Minutes.minutesBetween(eventDate, currentDate).getMinutes()));
                break;
            case 5:
                durationTextView.setText(String.valueOf(Seconds.secondsBetween(eventDate, currentDate).getSeconds()));
                break;
        }
    }

    public void setLastEventValues() {
        Cursor cursor = getCursor();
        cursor.moveToLast();
        setEventValues(cursor.getLong(0), cursor.getString(2), cursor.getString(3));
    }

    private void setEventValues(Long eventId, String eventDate, String eventDescription) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
        DateTime eventDateDT = DateTime.parse(eventDate);
        eventDateTextView.setText(eventDateDT.toString(dateTimeFormatter));
        eventDateTextView.setTag(eventDate);
        eventDescriptionEditText.setTag(eventId);
        if (eventDescription != null)
            eventDescriptionEditText.setText(eventDescription);
        else eventDescriptionEditText.setText("");

    }

    private Cursor getCursor() {
        //Todo: sprawdzić projekcje czy trzeba je wymeniać wszystkie
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + getActivity().getTitle());
        Cursor eventsCursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        return eventsCursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + getActivity().getTitle());
        Loader<Cursor> loader = new CursorLoader(getContext(), uri, projection, null, null, null);
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

}
