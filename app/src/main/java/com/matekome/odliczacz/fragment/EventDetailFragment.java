package com.matekome.odliczacz.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.activity.MainActivity;
import com.matekome.odliczacz.adapter.EventHistoryListAdapter;
import com.matekome.odliczacz.data.Event;
import com.matekome.odliczacz.data.MyContentProvider;
import com.matekome.odliczacz.data.MyPeriod;

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

    TextView eventDateTextView;
    TextView eventNameTextView;
    TextView eventElapsedTimeTextView;
    TextView sinceOrToTextView;
    EditText eventDescriptionEditText;
    Spinner spinner;
    EventHistoryListAdapter adapter;
    ImageButton saveDescriptionButton;
    ListView eventHistoryListView;
    String eventName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        eventName = getActivity().getIntent().getExtras().getString("eventName");
        getActivity().setTitle(getString(R.string.event));

        eventDateTextView = (TextView) view.findViewById(R.id.event_date_text_view);
        eventNameTextView = (TextView) view.findViewById(R.id.event_name);
        eventElapsedTimeTextView = (TextView) view.findViewById(R.id.event_elapsed_time);
        sinceOrToTextView = (TextView) view.findViewById(R.id.sinceOrToString);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        eventDescriptionEditText = (EditText) view.findViewById(R.id.event_description);
        eventHistoryListView = (ListView) view.findViewById(R.id.event_history_list_view);
        saveDescriptionButton = (ImageButton) view.findViewById(R.id.save_description_button);

        eventNameTextView.setText(eventName);
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
                setElapsedTimeTextView(position, eventDateTextView.getTag().toString());
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
                setElapsedTimeTextView(spinner.getSelectedItemPosition(), cursor.getString(2));
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
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName);
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
                    getActivity().getContentResolver().update(MyContentProvider.CONTENT_URI, values, Event.EventEntry.EVENT_NAME + "='" + eventName + "'", null);
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

    private void setElapsedTimeTextView(int position, String eventDateString) {
        DateTime currentDate = new DateTime();
        DateTime eventDate = DateTime.parse(eventDateString);
        switch (position) {
            case 0:
                eventElapsedTimeTextView.setText(MyPeriod.getPeriodToDisplay(eventDateString));
                break;
            case 1:
                eventElapsedTimeTextView.setText(String.valueOf(Years.yearsBetween(eventDate, currentDate).getYears()));
                break;
            case 2:
                eventElapsedTimeTextView.setText(String.valueOf(Months.monthsBetween(eventDate, currentDate).getMonths()));
                break;
            case 3:
                eventElapsedTimeTextView.setText(String.valueOf(Days.daysBetween(eventDate, currentDate).getDays()));
                break;
            case 4:
                eventElapsedTimeTextView.setText(String.valueOf(Hours.hoursBetween(eventDate, currentDate).getHours()));
                break;
            case 5:
                eventElapsedTimeTextView.setText(String.valueOf(Minutes.minutesBetween(eventDate, currentDate).getMinutes()));
                break;
            case 6:
                eventElapsedTimeTextView.setText(String.valueOf(Seconds.secondsBetween(eventDate, currentDate).getSeconds()));
                break;
        }
    }

    public void setLastEventValues() {
        Cursor cursor = getCursor();
        cursor.moveToFirst();
        setEventValues(cursor.getLong(0), cursor.getString(2), cursor.getString(3));
    }

    private void setEventValues(Long eventId, String eventDate, String eventDescription) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
        DateTime eventDateDT = DateTime.parse(eventDate);
        eventDateTextView.setText(eventDateDT.toString(dateTimeFormatter));

        String periodToDisplay = MyPeriod.getPeriodToDisplay(eventDate);

        if (periodToDisplay.contains("-"))
            sinceOrToTextView.setText(getString(R.string.to_event));
        else
            sinceOrToTextView.setText(getString(R.string.since_event));

        eventElapsedTimeTextView.setText(periodToDisplay);
        eventDateTextView.setTag(eventDate);
        eventDescriptionEditText.setTag(eventId);
        if (eventDescription != null)
            eventDescriptionEditText.setText(eventDescription);
        else
            eventDescriptionEditText.setText(null);
    }

    private Cursor getCursor() {
        //Todo: sprawdzić projekcje czy trzeba je wymeniać wszystkie
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName);
        Cursor eventsCursor = getActivity().getContentResolver().query(uri, projection, null, null, Event.EventEntry.EVENT_DATE + " DESC");
        return eventsCursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE, Event.EventEntry.EVENT_DESCRIPTION};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + eventName);
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

    public void addEventOccurrence() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.add_event_occurrence_input_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final DatePicker datePicker = (DatePicker) promptView.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) promptView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                ContentValues values = new ContentValues();
                values.put(Event.EventEntry.EVENT_NAME, eventName);

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

                getContext().getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                getLoaderManager().restartLoader(0, null, EventDetailFragment.this);
                setLastEventValues();
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
}
