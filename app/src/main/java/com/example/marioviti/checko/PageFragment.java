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

import java.util.ArrayList;

import Support.SupporHolder;
import customView.CircularIndicator;
import customView.CircularIndicatorAnimation;


public class PageFragment extends Fragment implements Animation.AnimationListener{

    private int pos;
    private String page;
    private CircularIndicator c;
    private CircularIndicatorAnimation ca;
    private View v;
    private ArrayList<float[]> summariesCache = new ArrayList<>(10);

    public void onCreate(Bundle si) {
        super.onCreate(si);
        //Log.d("onCreate", "---------------------------ROOT_FRAGMENT->PAGE_FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle si) {

        page = getArguments().getString("page");
        pos = getArguments().getInt("pos");
        v = li.inflate(R.layout.page_fragment0, container, false);

        switch (pos) {
            case 0: {
                v.setBackgroundColor(getResources().getColor(R.color.btn_color_type1));
                break;
            }
            case 1: {
                v.setBackgroundColor(getResources().getColor(R.color.btn_color_type2));
                break;
            }
            case 2: {
                v.setBackgroundColor(getResources().getColor(R.color.btn_color_type3));
                break;
            }
            case 3: {
                v.setBackgroundColor(getResources().getColor(R.color.btn_color_type4));
                break;
            }
            case 4: {
                v.setBackgroundColor(getResources().getColor(R.color.btn_color_type5));
                break;
            }
        }


        TextView tv = (TextView) v.findViewById(R.id.tvLabel);
        //tv.setText(pos + " " + page);
        if(SupporHolder.currentDayID!=-1)
            tv.setText(SupporHolder.currentDay);
        else
            tv.setText("Starts Today");

        updateUI();
        return v;
    }

    public void updateUI() {

        float[] values = null;
        String key = SupporHolder.summaryKey(SupporHolder.currentDayID, pos);
        if(SupporHolder.summaryCalendarCache.containsKey(key)){
            values = SupporHolder.summaryCalendarCache.get(key);
        }

        TextView tvCARB = (TextView) v.findViewById(R.id.text_CARB);
        TextView tvPROT = (TextView) v.findViewById(R.id.text_PROT);
        TextView tvFAT = (TextView) v.findViewById(R.id.text_FAT);
        TextView tvCAL = (TextView) v.findViewById(R.id.text_CAL);
        if(values!=null) {
            tvCARB.setText("Carbs\n"+values[0] + " gr");
            tvPROT.setText("Proteins\n"+values[1] + " gr");
            tvFAT.setText("Fats\n"+values[2] + " gr");
            tvCAL.setText("Calories\n"+values[3] + " kcal");
        }
        float percent = 0;
        if(SupporHolder.currentChaceDayID!=-1)
            percent = getPercent();
        TextView tvIndicator = (TextView) v.findViewById(R.id.indicator_textView);
        tvIndicator.setText(Math.ceil(percent * 100)+"%");
        c = (CircularIndicator) v.findViewById(R.id.circular_indicator);
        c.setAngle(0);
        ca = new CircularIndicatorAnimation(c,percent*360);
        ca.setDuration(1500);
    }

    public float getPercent() {
        int[] values = SupporHolder.calendarCache[SupporHolder.currentChaceDayID].values;
        int sum = 0;
        for (int i=0; i< values.length-1; i++) {
            sum+=values[i];
        }
        return (float)(values[this.pos]/(float)sum);
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
