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
 * Overview: Sottoclasse di ListFragment per l'implementazione del calendario.
 */
public class CalendarFragment extends ListFragment implements View.OnClickListener{

    private static View view;
    private static View.OnClickListener myself;
    private static CalendarListFragmentAdapter caa;
    private static FragmentSwapper caller;
    private static MainActivity callerContext;
    private String calendarNextDay = "";
    private int calendarNextDayID = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.caller= (FragmentSwapper)activity;
        callerContext = (MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        myself = this;
        Log.d("onCreate", "---------------------------CALENDAR_FRAGMENT "+this.getTag());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "---------------------------CALENDAR_FRAGMENT");
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        int coutActiveDays = 0;
        for (int i= 0; i<SupporHolder.calendarCache.length; i++)
            if (SupporHolder.calendarCache[i]!=null)
                coutActiveDays++;

        CalendarEntry[] values = new CalendarEntry[coutActiveDays];

        for (int i= 0; i<values.length; i++)
            values[i]=SupporHolder.calendarCache[i];

        caa = new CalendarListFragmentAdapter(li, li.getContext(), R.layout.custom_row_calendar, values);
        caa.data=values;
        setListAdapter(caa);
        caa.notifyDataSetChanged();
        view = super.onCreateView(li, container, si);

        Log.d("onCreateView", "---------------------------CALENDAR_FRAGMENT " + this.getTag());

        return view;
    }

    public void show(CalendarEntry[] entry) {
        for (int i = 0 ; i<entry.length; i++)
            if(entry[i]!=null)
                Log.d("show","\n"+entry[i].toString());
    }

    public static void clearCalendar () {
        caa=null;
    }

    public static boolean updateDayEntry (int currentDayCacheId ) {

        if (view==null){
            //Log.d("updateDayEntry","-----------------------------------------------UPDATE LAYOUT DEL CALENDARIO FALLITA");
            return false;
        }
        //Log.d("updateDayEntry","-----------------------------------------------UPDATE LAYOUT DEL CALENDARIO");
        CalendarRowBarView v = caa.getViewRow(currentDayCacheId);
        v.setValues(SupporHolder.calendarCache[currentDayCacheId].values);
        v.requestLayout();
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
                int oldChaceDayID =  SupporHolder.currentCacheDayID;
                SupporHolder.currentDayID = ((CalendarRowBarView)v).getDateID();
                SupporHolder.currentCacheDayID = ((CalendarRowBarView)v).getPositionDATECacheID();
                SupporHolder.currentDay = ((CalendarRowBarView)v).getDate();
                animateCalendar((CalendarRowBarView) v, caa.getViewRow(oldChaceDayID));
                int pos = calculateMainOfTheDay();
                caller.swapWith(pos, false);
                break;
            }
        }
    }

    private int calculateMainOfTheDay() {

        int[] values = SupporHolder.calendarCache[SupporHolder.currentCacheDayID].values;
        int max = 0;
        int j = 0;
        for (int i=0; i<values.length-1; i++) {
            if(values[i]>max) {
                max = values[i];
                j=i;
            }
        }
        return j;
    }
    /**
    EFFECTS: animazioni on touch delle entries del calendario
     */
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
    /**
    OVERVIEW: sottoclasse dell ArrayAdapter.

    MODIFY: assegna Listener agli items del calendar rendendoli cliccabili.
     */
    private class CalendarListFragmentAdapter extends ArrayAdapter<CalendarEntry> implements View.OnClickListener{

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
            viewRows = new ArrayList<>(data.length);
            viewRows.size();
            size = 0;
        }

        public CalendarRowBarView getViewRow(int pos) {
            return viewRows.get(pos);
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            if( size<data.length && data[size]!=null) {

                row = li.inflate(layoutResourceId, parent, false);
                TextView day = (TextView) row.findViewById(R.id.row_textView);
                CalendarEntry entryDay = data[size];
                CalendarRowBarView crow = (CalendarRowBarView) row.findViewById(R.id.row_custom_view);
                crow.setOnClickListener(myself);
                day.setText(entryDay.day);
                crow.setValues(entryDay.values);
                crow.setDate(entryDay.day);
                crow.setPositionDATECacheID(size);
                if (crow.getPositionDATECacheID() == SupporHolder.currentCacheDayID)
                    crow.setAlphaVal(0);
                viewRows.add(size, crow);
                if (size==9) {
                    day.setText("più giorni");
                    crow.setValues(new int[]{1, 1, 1, 1, 1});
                    crow.setDate("più giorni");
                    crow.setOnClickListener(this);
                }
            }
            this.size++;
            return row;
        }

        @Override
        public void onClick(View v) {
            String command = ((CalendarRowBarView)v).getDate();

            switch (command) {
                case "più giorni" : {
                    calendarNextDay = SupporHolder.latestDay;
                    calendarNextDayID = SupporHolder.latestDayID;
                    SupporHolder.latestDay = SupporHolder.calendarCache[9].day;
                    SupporHolder.latestDayID = SupporHolder.calendarCache[9].dayID;
                    SupporHolder.currentPage = SupporHolder.CALEDAR_FRAG;
                    callerContext.sync();
                    break;
                }
            }
        }
    }
}
