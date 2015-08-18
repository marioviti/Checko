package com.example.marioviti.checko;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by marioviti on 18/08/15.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    FragmentSwapper fgSwap;

    public void onAttach (Activity activity) {
        super.onAttach(activity);
        fgSwap = (FragmentSwapper) activity;

    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {
        View v = li.inflate(R.layout.menu_fragment, container, false);
        Button btn0 = (Button) v.findViewById(R.id.frag_button_0),
         btn1 = (Button) v.findViewById(R.id.frag_button_1),
         btn2 = (Button) v.findViewById(R.id.frag_button_2);
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick (View v) {
        switch(v.getId()) {
            case R.id.frag_button_0: fgSwap.swapWith(0);
            case R.id.frag_button_1: fgSwap.swapWith(1);
            case R.id.frag_button_2: fgSwap.swapWith(2);
        }

    }
}
