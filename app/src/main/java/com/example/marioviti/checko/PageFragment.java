package com.example.marioviti.checko;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by marioviti on 18/08/15.
 */
public class PageFragment extends Fragment {

    private int pos;
    private String page;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle si) {

        page = getArguments().getString("page");
        pos = getArguments().getInt("pos");
        View v = null;
        if(pos == 0)
            v = li.inflate(R.layout.page_fragment0, container, false);
        else if (pos == 1)
            v = li.inflate(R.layout.page_fragment1, container, false);
        else if (pos == 2)
            v = li.inflate(R.layout.page_fragment2, container, false);

        if(v!=null) {
            TextView tv = (TextView) v.findViewById(R.id.tvLabel);
            tv.setText(pos + " " + page);
        }

        return v;
    }

    public static PageFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        PageFragment ff = new PageFragment();
        ff.setArguments(args);

        return ff;
    }
}
