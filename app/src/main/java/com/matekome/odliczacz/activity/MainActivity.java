package com.matekome.odliczacz.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.matekome.odliczacz.R;
import com.matekome.odliczacz.fragment.EventsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.your_events));

        //getBaseContext().deleteDatabase("Odliczacz.db");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EventsFragment fragment = (EventsFragment) getSupportFragmentManager().findFragmentById(R.id.events_fragment);

        switch (item.getItemId()) {
            case R.id.refresh:
                fragment.refreshEvents();
                break;
            case R.id.add_event:
                fragment.showInputDialogToAddNewEvent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
