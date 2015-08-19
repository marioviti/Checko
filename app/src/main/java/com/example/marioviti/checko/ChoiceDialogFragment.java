package com.example.marioviti.checko;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by marioviti on 19/08/15.
 */
public class ChoiceDialogFragment extends DialogFragment {
    public View onCreateView (LayoutInflater li, ViewGroup container, Bundle si) {
        View v = li.inflate(R.layout.dialog_view,container,false);
        getDialog().setTitle("Fragment di dialogo");
        return v;
    }
}
