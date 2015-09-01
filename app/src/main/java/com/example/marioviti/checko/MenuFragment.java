package com.example.marioviti.checko;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import Support.SupporHolder;
import customView.CustomImageButton;
import databaseHandling.DBOpenHelper;

/**
 * Created by marioviti on 18/08/15.
 */

public class MenuFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{

    private FragmentSwapper fgSwap;
    private OnTopDialogLauncher oTlauncher;
    private View view;

    @Override
    public void onCreate(Bundle si) {
        super.onCreate(si);
        Log.d("onCreate", "---------------------------MENU_FRAGMENT"+this.getTag());
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        fgSwap = (FragmentSwapper) activity;
        oTlauncher = (OnTopDialogLauncher) activity;

    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {

        Log.d("onCreateView", "---------------------------MENU_FRAGMENT");

        view = li.inflate(R.layout.menu_fragment, container, false);
        CustomImageButton btn0 = (CustomImageButton) view.findViewById(R.id.frag_custom_button_0),
            btn1 = (CustomImageButton) view.findViewById(R.id.frag_custom_button_1),
            btn2 = (CustomImageButton) view.findViewById(R.id.frag_custom_button_2),
            btn3 = (CustomImageButton) view.findViewById(R.id.frag_custom_button_3),
            btn4 = (CustomImageButton) view.findViewById(R.id.frag_custom_button_4);

        btn0.setOnClickListener(this);
        btn0.setOnLongClickListener(this);
        btn1.setOnClickListener(this);
        btn1.setOnLongClickListener(this);
        btn2.setOnClickListener(this);
        btn2.setOnLongClickListener(this);
        btn3.setOnClickListener(this);
        btn3.setOnLongClickListener(this);
        btn4.setOnClickListener(this);
        btn4.setOnLongClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "---------------------------MENU_FRAGMENT");
    }

    public static MenuFragment newInstance(String page, int pos) {

        Bundle args = new Bundle();
        args.putString("page", page);
        args.putInt("pos", pos);
        MenuFragment ff = new MenuFragment();
        ff.setArguments(args);

        return ff;
    }

    @Override
    public void onClick(View v) {

        int vID = v.getId();
        switch (vID) {
            case R.id.frag_custom_button_0: {
                SupporHolder.globalTypeVariable = 0;
                fgSwap.swapWith(0,true);
                break;
            }
            case  R.id.frag_custom_button_1: {
                SupporHolder.globalTypeVariable = 1;
                fgSwap.swapWith(1,true);
                break;
            }
            case  R.id.frag_custom_button_2: {
                SupporHolder.globalTypeVariable = 2;
                fgSwap.swapWith(2,true);
                break;
            }
            case  R.id.frag_custom_button_3: {
                SupporHolder.globalTypeVariable = 3;
                fgSwap.swapWith(3,true);
                break;
            }
            case  R.id.frag_custom_button_4: {
                SupporHolder.globalTypeVariable = 4;
                fgSwap.swapWith(4,true);
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.frag_custom_button_0: {
                SupporHolder.globalTypeVariable = 0;
                oTlauncher.lauchDialog(0);
                break;
            }

            case  R.id.frag_custom_button_1: {
                /// ASSURDO
                SupporHolder.globalTypeVariable = 1;
                oTlauncher.lauchDialog(1);
                break;
            }

            case  R.id.frag_custom_button_2: {
                SupporHolder.globalTypeVariable = 2;
                oTlauncher.lauchDialog(2);
                break;
            }

            case  R.id.frag_custom_button_3: {
                SupporHolder.globalTypeVariable = 3;
                oTlauncher.lauchDialog(3);
                break;
            }

            case  R.id.frag_custom_button_4: {
                SupporHolder.globalTypeVariable = 4;
                oTlauncher.lauchDialog(4);
                break;
            }
        }

        return true;
    }
}
