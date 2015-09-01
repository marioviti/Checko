package com.example.marioviti.checko;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import Support.SupporHolder;

/**
 * Created by marioviti on 18/08/15.
 */

public class RootFragment extends Fragment implements FragmentSwapper {

    private static FragmentPool fgtPool;
    private static int PAG_NUM = 5;
    private static FragmentManager fm;
    private static FragmentActivity myContext;
    private int currentFrag;

    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        fm = myContext.getSupportFragmentManager();
        if(activity!=null)
            super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        Log.d("onCreate", "---------------------------ROOT_FRAGMENT");
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        View v = li.inflate(R.layout.root_fragment, container, false);
        initiatePool();
        // hack: per evitare che il primo fragment non sia riconosciuto come quello corrente
        // setto quello corrente a 1 != 0 in modo che venga settato
        this.currentFrag=1;
        this.swapWith(0,true);

        Log.d("onCreateView", "---------------------------ROOT_FRAGMENT");
        return v;
    }

    public static RootFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        RootFragment ff = new RootFragment();
        ff.setArguments(args);

        return ff;
    }

    private static void initiatePool() {

        fgtPool = new FragmentPool(PAG_NUM);
        for(int i = 0; i<PAG_NUM; i++)
            fgtPool.insertFragment(PageFragment.newInstance("page",i));

    }

    /*
        Sistema di interfacciamento per Fragment annidati: il RootFragment ritiene i Fragment
        gestisce le transazioni FragmentTransaction dinamiche
        */
    @Override
    public boolean swapWith( int pos , boolean withSroll ) {

        if(this.currentFrag==pos) {
            ((PageFragment)fgtPool.getAt(pos)).updateUI();
            ((PageFragment)fgtPool.getAt(pos)).animate();
        }
        else {
            Fragment fg;
            FragmentTransaction fgt;

            fg = fgtPool.getAt(pos);
            if (fg != null) {
                fgt = fm.beginTransaction();
                fgt.replace(R.id.fragment_placeholder, fg);
                //fgt.addToBackStack(null);
                fgt.commit();
                this.currentFrag = pos;
                return true;
            }else {
                fg = fgtPool.insertFragmentAtandReturn(PageFragment.newInstance("page", pos), pos);
                fgt = fm.beginTransaction();
                fgt.replace(R.id.fragment_placeholder, fg);
                //fgt.addToBackStack(null);
                fgt.commit();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "---------------------------ROOT_FRAGMENT");
    }
}
