package com.example.marioviti.checko;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    private CalendarListFragmentAdapter caa;
    private FragmentSwapper caller;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.caller= (FragmentSwapper)activity;
    }

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
        entries[0] = new CalendarEntry("today", new int[] {20,20,20,20,20,0},0,0);

        int active = 0;
        for( int i =0; i<SupporHolder.calendarCache.length; i++) {
            if(SupporHolder.calendarCache[i]!=null)
                active++;
        }
        if (active!=0) {
            entries = new CalendarEntry[active];
            for (int i = 0; i < active; i++) {
                entries[i] = SupporHolder.calendarCache[i];
            }
        }

        caa = new CalendarListFragmentAdapter(li, li.getContext(), R.layout.custom_row_calendar, entries);
        caa.data=entries;
        setListAdapter(caa);
        view = super.onCreateView(li, container, si);

        return view;
    }

    public boolean updateUI() {
        if (view==null){return false;}

        CalendarEntry[] entries = caa.data;
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
        caa.data=entries;
        caa.notifyDataSetChanged();
        return true;
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
                int oldChaceDayID =  SupporHolder.currentChaceDayID;
                SupporHolder.currentDayID = ((CalendarRowBarView)v).getDateID();
                SupporHolder.currentChaceDayID = ((CalendarRowBarView)v).getPositionDATECacheID();
                SupporHolder.currentDay = ((CalendarRowBarView)v).getDate();
                animateCalendar ((CalendarRowBarView)v,caa.getViewRow(oldChaceDayID));
                break;
            }
        }
    }

    public void animateCalendar (CalendarRowBarView in, CalendarRowBarView out){
        if(in.getPositionDATECacheID()!=out.getPositionDATECacheID()) {
            CalendarRowBarSelectAnimation crbanimIn = new CalendarRowBarSelectAnimation(in, 0);
            CalendarRowBarSelectAnimation crbanimOut = new CalendarRowBarSelectAnimation(out, 100);
            crbanimIn.setDuration(200);
            crbanimOut.setDuration(200);
            in.startAnimation(crbanimIn);
            out.startAnimation(crbanimOut);
        }else {
            if(in.getAlphaVal()!=0) {
                CalendarRowBarSelectAnimation crbanimIn = new CalendarRowBarSelectAnimation(in, 0);
                crbanimIn.setDuration(200);
                in.startAnimation(crbanimIn);
            }
        }
    }

    private class CalendarListFragmentAdapter extends ArrayAdapter<CalendarEntry> {

        private int layoutResourceId;
        private LayoutInflater li;
        public CalendarEntry[] data;
        private ArrayList<CalendarRowBarView> viewRows;
        public int size;

        public CalendarListFragmentAdapter(LayoutInflater li, Context context, int layoutResourceId, CalendarEntry data[]) {
            super(context, layoutResourceId, data);
            this.li = li;
            this.layoutResourceId = layoutResourceId;
            this.data = data;
            viewRows = new ArrayList<>();
            size = 0;
        }

        public CalendarRowBarView getViewRow(int pos) {
            return viewRows.get(pos);
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
                if(crow.getPositionDATECacheID()==SupporHolder.currentChaceDayID)
                    crow.setAlphaVal(0);
                viewRows.add(position, crow);
                this.size ++;
            }
            return row;
        }
    }
}
