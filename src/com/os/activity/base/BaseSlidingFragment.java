package com.os.activity.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 滑动菜单Fragment基类
 * 
 * @author admin
 * 
 */
public class BaseSlidingFragment extends BaseFragment {
	private View mRootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		parent=null;
		return mRootView;
	}

	public void setContentView(int layoutResID) {
		mRootView = LayoutInflater.from(getActivity()).inflate(layoutResID, null);
		initViews();
		addListener();
	}

	
	public void initViews() {

	}

	public View findViewById(int id) {
		if (mRootView == null) {
			return null;
		}
		return mRootView.findViewById(id);
	}

	public void addListener() {

	}

	public void filter(int type) {

	}

	/**
	 * 获取Fragment的Activity
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends FragmentActivity>  T getFragmentActivity(Class<T> clazz) {
		return (T) getActivity();
	}
	
	public String getPackageName() {
		return getActivity().getPackageName();
	}

	@Override
	public void tabNextRecharge() {

	}

	@Override
	public void refreshUserData() {
		
	}

	
}
