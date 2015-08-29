package com.example.marioviti.checko;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import Support.CalendarEntry;
import Support.SupporHolder;
import customView.CalendarRowBarSelectAnimation;
import customView.CalendarRowBarView;

/**
 * Created by marioviti on 24/08/15.
 */
public class CalendarFragment extends ListFragment implements View.OnClickListener{

    private View view;
    private View.OnClickListener myself;

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        myself = this;
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

        CalendarEntry[] entries = new CalendarEntry[1];
        entries[0] = new CalendarEntry("today", new int[] {20,20,20,20,20,-1});

        int active = 0;
        for( int i =0 ; i<SupporHolder.calendarCache.length; i++) {
            if(SupporHolder.calendarCache[i]!=null)
                active++;
        }
        if (active!=0) {
            entries = new CalendarEntry[active];
            for (int i = 0; i < active; i++) {
                entries[i] = SupporHolder.calendarCache[i];
            }
        }

        CalendarListFragmentAdapter caa = new CalendarListFragmentAdapter(li, li.getContext(), R.layout.custom_row_calendar, entries);
        caa.data=entries;
        setListAdapter(caa);
        view = super.onCreateView(li, container, si);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.row_custom_view : {

                SupporHolder.currentDayID = ((CalendarRowBarView)v).getDateID();
                SupporHolder.currentChaceDayID = ((CalendarRowBarView)v).getPositionDATECacheID();
                SupporHolder.currentDay = ((CalendarRowBarView)v).getDate();
                CalendarRowBarSelectAnimation crbanim = new CalendarRowBarSelectAnimation((CalendarRowBarView) v, 0);
                crbanim.setDuration(650);
                v.startAnimation(crbanim);
                break;
            }
        }
    }

    private class CalendarListFragmentAdapter extends ArrayAdapter<CalendarEntry> {

        private int layoutResourceId;
        private LayoutInflater li;
        public CalendarEntry[] data;

        public CalendarListFragmentAdapter(LayoutInflater li, Context context, int layoutResourceId, CalendarEntry data[]) {
            super(context, layoutResourceId, data);
            this.li = li;
            this.layoutResourceId = layoutResourceId;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row == null) {
                row = li.inflate(layoutResourceId, parent, false);
                TextView day = (TextView)row.findViewById(R.id.row_textView);
                CalendarEntry entryDay = data[position];
                CalendarRowBarView crow = (CalendarRowBarView)row.findViewById(R.id.row_custom_view);
                crow.setOnClickListener(myself);

                day.setText(entryDay.day);
                crow.setValues(entryDay.values);
                crow.setDate(entryDay.day);
                crow.setPositionDATECacheID(position);
            }
            return row;
        }
    }
}
