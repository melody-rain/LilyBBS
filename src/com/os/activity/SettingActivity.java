package com.os.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.os.slidingmenu.R;

import java.util.List;

/**
 * Created by Jin on 2014/9/16.
 */
public class SettingActivity extends SherlockPreferenceActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setIcon(android.R.color.transparent);
        bar.setTitle("设置");
        bar.setSubtitle("小百合客户端设置");
    }
    @Override
    public void onBuildHeaders(List<Header> target){
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
}
