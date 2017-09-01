package com.example.mateusz.odliczacz.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mateusz.odliczacz.R;
import com.example.mateusz.odliczacz.activity.MainActivity;
import com.example.mateusz.odliczacz.adapter.EventHistoryListAdapter;
import com.example.mateusz.odliczacz.data.Event;
import com.example.mateusz.odliczacz.data.MyContentProvider;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class EventDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        final String event_name = getActivity().getIntent().getExtras().getString("event_name");
        final String event_date = getActivity().getIntent().getExtras().getString("event_date");

        //Todo: sprawdzić projekcje czy trzeba je wymeniać wszystkie
        String[] projection = {Event.EventEntry._ID, Event.EventEntry.EVENT_NAME, Event.EventEntry.EVENT_DATE};
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + event_name);
        Cursor eventsCursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY hh:mm");
        final DateTime eventDate = DateTime.parse(event_date);
        final DateTime currentDate = new DateTime();

        final TextView durationTextView = (TextView) view.findViewById(R.id.duration);
        TextView tv = (TextView) view.findViewById(R.id.event_detail_text_view);
        tv.setText(eventDate.toString(dateTimeFormatter));

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ListView eventHistoryListView = (ListView) view.findViewById(R.id.event_history_list_view);
        EventHistoryListAdapter adapter = new EventHistoryListAdapter(getContext(), eventsCursor, 0);
        eventHistoryListView.setAdapter(adapter);

        Button deleteButton = (Button) view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage(getString(R.string.delete_confirmation)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + event_name);
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
        });

        return view;
    }
}
