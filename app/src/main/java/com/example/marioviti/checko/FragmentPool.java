package com.example.marioviti.checko;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by marioviti on 18/08/15.
 */
public class FragmentPool {

    private int curr;
    private int length;
    //private Fragment[] fragArray;
    private ArrayList<WeakReference<Fragment>> fragArrayList ;

    public FragmentPool (int lenght) {

        //fragArray = new Fragment[lenght];
        fragArrayList = new ArrayList<WeakReference<Fragment>>(lenght);
        this.length = lenght;
        curr = 0;
        Log.d("FragmentPool","---------------------------------allocazione Pool"+fragArrayList.size());
    }

    public boolean insertFragment (Fragment f) {
        Log.d("FragmentPool","---------------------------------prima allocazione Fragment");
        if(curr==length)
            return false;
        //fragArray[curr]=f;
        fragArrayList.add(curr,new WeakReference<Fragment>(f));
        curr++;

        return true;
    }

    public int getCurr () { return curr; }

    public void setCurr (int curr) { this.curr = curr; }

    public Fragment getAt (int i) {

        if(i>=0 && i<length)
            return fragArrayList.get(i).get();

        return null;
    }

    public Fragment insertFragmentAtandReturn (Fragment f, int i) {
        Log.d("FragmentPool","---------------------------------nuova allocazione Fragment");
        if(i>=0 && i<length) {
            fragArrayList.set(i,new WeakReference<Fragment>(f));
            return fragArrayList.get(i).get();
        }
        return null;
    }


}
