package com.os.ui.ui2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.os.slidingmenu.R;

public class SecondFragment extends Fragment {

	private View viewFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewFragment=inflater.inflate(R.layout.second, null);
		return viewFragment;
	}
}
