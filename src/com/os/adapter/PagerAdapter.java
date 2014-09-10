package com.os.adapter;

import java.util.ArrayList;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	
	private final Context mContext;
	  private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	  
	  static final class TabInfo {

	        private final Class<?> mClss;
	        private final Bundle mArgs;

	        TabInfo(Class<?> aClass, Bundle args) {
	            mClss = aClass;
	            mArgs = args;
	        }
	    }
	  
	  
/*	private Context mContext;
	private  ArrayList<Class> mFragments = new ArrayList<Class>();
	public PagerAdapter(FragmentActivity activity,ArrayList fragments) {
		 super(activity.getSupportFragmentManager());
		 mContext=activity;
		 mFragments=fragments;
	}
*/
	  
	  public PagerAdapter(FragmentActivity activity) {
	        super(activity.getSupportFragmentManager());
	        mContext = activity;
	    }
	@Override
	public Fragment getItem(int position) {

//		return Fragment.instantiate(mContext, mFragments.get(position).getName(), null);
		   TabInfo info = mTabs.get(position);
	        return Fragment.instantiate(mContext, info.mClss.getName(),
	                info.mArgs);
	}

	@Override
	public int getCount() {
		
//		return mFragments.size();
		  return mTabs.size();
	}

	 public void addTab(Class<?> clss, Bundle args) {
	        TabInfo info = new TabInfo(clss, args);
	        mTabs.add(info);
	        notifyDataSetChanged();
	    }
}
