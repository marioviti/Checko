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
import customView.HistogramAnimation;
import customView.HistogramView;


public class PageFragment extends Fragment implements Animation.AnimationListener{

    private int pos;
    private String page;

    private View v;
    private TextView tvCARB,tvPROT,tvFAT,tvCAL,tv,tvIndicator;

    private CircularIndicator c;
    private CircularIndicatorAnimation ca;
    private HistogramView hv;
    private HistogramAnimation ha;

    public void onCreate(Bundle si) {
        super.onCreate(si);
        //Log.d("onCreate", "---------------------------ROOT_FRAGMENT->PAGE_FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle si) {

        page = getArguments().getString("page");
        pos = getArguments().getInt("pos");

        if(v==null) {

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

            tvCARB = (TextView) v.findViewById(R.id.text_CAL);
            tvPROT = (TextView) v.findViewById(R.id.text_PROT);
            tvFAT = (TextView) v.findViewById(R.id.text_FAT);
            tvCAL = (TextView) v.findViewById(R.id.text_CAL);
            tv = (TextView) v.findViewById(R.id.tvLabel);
            tvIndicator = (TextView) v.findViewById(R.id.indicator_textView);
            hv = (HistogramView) v.findViewById(R.id.page_histogram);
            c = (CircularIndicator) v.findViewById(R.id.circular_indicator);

        }
        updateUI();

        //Log.d("onCreateView", "---------------------------ROOT_FRAGMENT->PAGE_FRAGMENT:" + pos);
        return v;
    }

    public void updateUI() {

        hv.setValues(new float[] {5,5,5,5});
        float percent = 0;
        float[] values = null;
        if(SupporHolder.currentCacheDayID!=-1) {

            values = SupporHolder.calendarCache[SupporHolder.currentCacheDayID].summaries[pos];
            percent = getPercent(SupporHolder.calendarCache[SupporHolder.currentCacheDayID].values);
            tv.setText(SupporHolder.currentDay);
            tvCARB.setText(values[0] + "gr");
            tvPROT.setText(values[1] + "gr");
            tvFAT.setText(values[2] + "gr");
            tvCAL.setText(values[3] + "kcal");
            tvIndicator.setText(Math.ceil(percent * 100) + "%");

            ha = new HistogramAnimation(hv,values);
            ha.setDuration(1500);
        }
        else {
            tv.setText("Starts Today");
        }

        c.setAngle(0);
        ca = new CircularIndicatorAnimation(c,percent*360);
        ca.setDuration(1500);
    }

    public void animate(){
        c.startAnimation(ca);
        hv.startAnimation(ha);
    }

    public float getPercent(int [] values) {
        int sum = 0;
        for (int i=0; i<5; i++) {
            sum+=values[i];
        }
        return (float)(values[this.pos]/(float)sum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if(v!=null)
            animate();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
