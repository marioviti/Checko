package com.example.marioviti.checko;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by marioviti on 18/08/15.
 */
public class FragmentPool {

    private int curr;
    private int length;
    private Fragment[] fragArray;

    public FragmentPool (int lenght) {

        Log.d("FragmentPool","---------------------------------allocazione Pool");
        fragArray = new Fragment[lenght];
        this.length = lenght;
        curr = 0;
    }

    public boolean insertFragment (Fragment f) {
        Log.d("FragmentPool","---------------------------------prima allocazione Fragment");
        if(curr==length)
            return false;
        fragArray[curr]=f;
        curr++;

        return true;
    }

    public Fragment getAt (int i) {

        if(i>=0 && i<length)
            return fragArray[i];

        return null;
    }

    public Fragment insertFragmentAtandReturn (Fragment f, int i) {
        Log.d("FragmentPool","---------------------------------nuova allocazione Fragment");
        if(i>=0 && i<length) {
            fragArray[i]=f;
            return f;
        }
        return null;
    }


}
