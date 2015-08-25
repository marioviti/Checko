package com.example.marioviti.checko;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import customView.CircularIndicator;
import customView.CircularIndicatorAnimation;


public class PageFragment extends Fragment implements Animation.AnimationListener{

    private int pos;
    private String page;
    private CircularIndicator c;
    private CircularIndicatorAnimation ca;

    public void onCreate(Bundle si) {
        super.onCreate(si);
        //Log.d("onCreate", "---------------------------ROOT_FRAGMENT->PAGE_FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle si) {

        page = getArguments().getString("page");
        pos = getArguments().getInt("pos");
        View v = li.inflate(R.layout.page_fragment0, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tvLabel);
        tv.setText(pos + " " + page);

        c = (CircularIndicator) v.findViewById(R.id.circular_indicator);
        ca = new CircularIndicatorAnimation(c,240);
        ca.setDuration(4000);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("onDestroy", "---------------------------ROOT_FRAGMENT->PAGE_FRAGMENT");
    }


    public static PageFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        PageFragment ff = new PageFragment();
        ff.setArguments(args);

        return ff;
    }

    // Animazioni transizioni su transazioni Fragments
    @Override
    public Animation onCreateAnimation( int transit, boolean enter, int nextAnim ) {
        Animation anim;
        if (enter) {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_in);
        } else {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_out);
        }

        anim.setAnimationListener(this);
        return anim;
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        c.startAnimation(ca);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
