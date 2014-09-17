package com.os.ui.ui2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.os.slidingmenu.R;

/**
 * Created by Jin on 2014/9/16.
 */
public class ImageUtilFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.image_util);
    }
}
