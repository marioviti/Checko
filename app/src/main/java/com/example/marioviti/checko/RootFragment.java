package com.example.marioviti.checko;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by marioviti on 18/08/15.
 */
public class RootFragment extends Fragment implements FragmentSwapper {

    private FragmentPool fgtPool;
    private int PAG_NUM = 3;

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        View v = li.inflate(R.layout.root_fragment, container, false);
        fgtPool = new FragmentPool(PAG_NUM);
        for(int i = 0; i<PAG_NUM; i++)
            fgtPool.inesertFragment(PageFragment.newInstance("page",i));

        return v;
    }

    @Override
    public boolean swapWith (int pos) {
        return false;
    }

    private class FragmentPool {

        private int curr;
        private int length;
        private Fragment[] fragArray;

        public FragmentPool (int lenght) {

            fragArray = new Fragment[lenght];
            this.length = lenght;
            curr = 0;
        }

        public boolean inesertFragment (Fragment f) {

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
    }
}
