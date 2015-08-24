package com.example.marioviti.checko;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by marioviti on 24/08/15.
 */
public class CalendarFragment extends ListFragment {

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        Log.d("onCreate", "---------------------------CALENDAR_FRAGMENT");
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        // Get references to UI widgets
        String[] numbers_text = new String[] { "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten", "eleven",
                "twelve", "thirteen", "fourteen", "fifteen" };

        final ArrayAdapter<String> aa;
        aa = new ArrayAdapter<String>(li.getContext(), android.R.layout.simple_list_item_1, numbers_text);
        setListAdapter(aa);

        return super.onCreateView(li, container, si);
    }

    public static CalendarFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        CalendarFragment ff = new CalendarFragment();
        ff.setArguments(args);

        return ff;
    }

    private class CalendarListFragmentAdapter extends ArrayAdapter<String> {

        private int layoutResourceId;
        private Context context;
        private String[] data;

        public CalendarListFragmentAdapter(Context context, int layoutResourceId, String[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

    }
}
