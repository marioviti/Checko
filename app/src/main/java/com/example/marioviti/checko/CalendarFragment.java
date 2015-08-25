package com.example.marioviti.checko;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by marioviti on 24/08/15.
 */
public class CalendarFragment extends ListFragment {

    private boolean firstView = true;
    private View view;

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        Log.d("onCreate", "---------------------------CALENDAR_FRAGMENT");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "---------------------------CALENDAR_FRAGMENT");
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        // Get references to UI widgets
        Log.d("onCreateView", "---------------------------CALENDAR_FRAGMENT");

        if(firstView) {
            CalendarEntry[] entries = new CalendarEntry[7];
            entries[0] = new CalendarEntry("today", null);
            entries[1] = new CalendarEntry("tomorrow", null);
            entries[2] = new CalendarEntry("tomotomorrow", null);
            entries[3] = new CalendarEntry("totomotomorrow", null);
            entries[4] = new CalendarEntry("tototomotomorrow", null);
            entries[5] = new CalendarEntry("tototomotomorrow2", null);
            entries[6] = new CalendarEntry("todayaganin", null);

            CalendarListFragmentAdapter caa = new CalendarListFragmentAdapter(li, li.getContext(), R.layout.custom_row_calendar, entries);
            setListAdapter(caa);

            view = super.onCreateView(li, container, si);
            firstView = false;
        }

        return view;
    }

    public static CalendarFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        CalendarFragment ff = new CalendarFragment();
        ff.setArguments(args);

        return ff;
    }

    private class CalendarEntry {
        public String day;
        public Float values[];

        public CalendarEntry(String day,  Float values[]){
            this.day=day;
            this.values=values;
        }
    }

    private class CalendarListFragmentAdapter extends ArrayAdapter<CalendarEntry> {

        private int layoutResourceId;
        private Context context;
        private LayoutInflater li;
        private CalendarEntry[] data;

        public CalendarListFragmentAdapter(LayoutInflater li, Context context, int layoutResourceId, CalendarEntry data[]) {
            super(context, layoutResourceId, data);
            this.li = li;
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row == null)
            {
                row = li.inflate(layoutResourceId, parent, false);

                TextView day = (TextView)row.findViewById(R.id.row_textView);
                SurfaceView values = (SurfaceView)row.findViewById(R.id.row_surfaceView);
                CalendarEntry entryDay = data[position];
                day.setText(entryDay.day);
            }
            return row;
        }
    }
}
