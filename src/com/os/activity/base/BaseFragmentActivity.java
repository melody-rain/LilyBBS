package com.os.activity.base;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.view.View.OnClickListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseFragmentActivity extends SherlockFragmentActivity {


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	
	}
	
	public Fragment getFragmentByTag(Class<? extends Fragment> clazz) {
		return getSupportFragmentManager().findFragmentByTag(clazz.getName());
	}
	
	public Fragment getFragmentByTag(String tag) {
		return getSupportFragmentManager().findFragmentByTag(tag);
	}
	
	/**
	 * 退出
	 */
	public void exitApp(final android.content.DialogInterface.OnClickListener listener ) {
	
	}

	/**
	 * 添加引导图片
	 */
	public void addGuideImage(int rootViewId,int guideResourceId) {
		addGuideImage(rootViewId, guideResourceId, null);
	}
	/**
	 * 添加引导图片
	 */
	public void addGuideImage(int rootViewId,int guideResourceId,final OnClickListener listener) {
	
	}
}
