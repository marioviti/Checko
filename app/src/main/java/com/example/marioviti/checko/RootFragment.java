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

public class RootFragment extends Fragment {

    public static FragmentPool fgtPool;
    private static int PAG_NUM = 5;
    private static FragmentManager fm;
    private static FragmentActivity myContext;
    private static final int ROOT_FRAG = 1;

    public void onAttach(Activity activity) {

        myContext = (FragmentActivity) activity;
        fm = myContext.getSupportFragmentManager();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        //Log.d("onCreate", "---------------------------ROOT_FRAGMENT " + this.getTag());
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        View v = li.inflate(R.layout.root_fragment, container, false);
        initiatePool();
        // hack: per evitare che il primo fragment non sia riconosciuto come quello corrente
        // setto quello corrente a 1 != 0 in modo che venga settato
        //this.fgtPool.setCurr(1);
        //this.swapInnerFragmentWith(0, false);

        //Log.d("onCreateView", "---------------------------ROOT_FRAGMENT");
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

    /**
        Workaround dell'implementazione del FragmentPageAdapter.instantiateItem(...)
    */
    public static boolean swapInnerFragmentWith(int pos, boolean withSroll) {

        if( fgtPool.getCurr()==pos ) {
            ((PageFragment)fgtPool.getAt(pos)).updateUI();
            ((PageFragment)fgtPool.getAt(pos)).animate();
        }
        else {
            Fragment fgIn, fgOut;
            FragmentTransaction fgt;
            fgIn = fgtPool.getAt(pos);
            fgt = fm.beginTransaction();
            if (fgIn == null) {
                fgIn = fgtPool.insertFragmentAtandReturn(PageFragment.newInstance("page", pos), pos);
            }
            fgt.replace(R.id.fragment_placeholder,fgIn);
            fgt.commit();
            fgtPool.setCurr(pos);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("onDestroy", "---------------------------ROOT_FRAGMENT");
    }
}
