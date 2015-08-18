package com.example.marioviti.checko;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
            fgtPool.insertFragment(PageFragment.newInstance("page",i));
        swapWith(0);

        return v;
    }

    @Override
    public boolean swapWith(int pos) {

        Fragment fg = fgtPool.getAt(pos);
        if (fg!=null) {
            FragmentTransaction fgt = getFragmentManager().beginTransaction();
            fgt.replace(R.id.fragment_placeholder, fg);
            fgt.commit();

            return true;
        }
        return false;
    }
}
