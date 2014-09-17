package com.os.ui.ui2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.os.slidingmenu.R;

/**
 * Created by Jin on 2014/9/16.
 */
public class AboutFragment extends PreferenceFragment{

    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.about, null);
        return v;
    }
}
