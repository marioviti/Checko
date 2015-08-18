package com.example.marioviti.checko;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by marioviti on 18/08/15.
 */
public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle si) {

        View v = li.inflate(R.layout.page_fragment, container, false);

        TextView tv = (TextView) v.findViewById(R.id.tvLabel);
        tv.setText(getArguments().getString("page")+" "+getArguments().getInt("pos"));

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
