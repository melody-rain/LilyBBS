package com.os.activity.sliding;

import java.util.Arrays;

import com.os.activity.MainActivity;
import com.os.activity.base.BaseSlidingFragment;
import com.os.slidingmenu.R;
import com.os.ui.FollowFragment;
import com.os.ui.MainHallFragment;
import com.os.ui.RankFragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
public class LeftFragment extends BaseSlidingFragment {
	private final static int MENU_NORMAL_ICONS[] = {  R.drawable.sliding_livehall_icon_normal,
			R.drawable.sliding_follow_icon_normal, R.drawable.sliding_rank_icon_normal};

	private final static int MENU_CHECKED_ICONS[] = {  R.drawable.sliding_livehall_icon_checked,
			R.drawable.sliding_follow_icon_checked, R.drawable.sliding_rank_icon_checked };

	public final static Class[] FRAGMENTS_CLASSES = {  MainHallFragment.class, FollowFragment.class,
			RankFragment.class};

	private View[] mMenuLayouts;
	private ImageView[] mMenuIcons;
	private TextView[] mMenuTexts;

	
	private Bitmap mLoadingBitmap;
	private int mCurrentIndex = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.sliding_left);
		setData();
		changeMenuByClass(MainHallFragment.class);

		
	}

	@Override
	public void initViews() {
		
		mMenuLayouts = new View[] { findViewById(R.id.menu_livehall_layout),
				findViewById(R.id.menu_follow_layout), findViewById(R.id.menu_rank_layout) };

		mMenuIcons = new ImageView[] {
				(ImageView) findViewById(R.id.menu_livehall_icon), (ImageView) findViewById(R.id.menu_follow_icon), (ImageView) findViewById(R.id.menu_rank_icon) };

		mMenuTexts = new TextView[] { 
				(TextView) findViewById(R.id.menu_livehall_text), (TextView) findViewById(R.id.menu_follow_text), (TextView) findViewById(R.id.menu_rank_text),};

	}

	@Override
	public void addListener() {
	
		

		for (int i = 0; i < mMenuLayouts.length; i++) {
			mMenuLayouts[i].setTag(i);
			mMenuLayouts[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					changeMenuByIndex(index);
				}
			});
		}
	}

	private void setData() {
		
	}

	

	


	/**
	 * 通过索引改变Menu
	 * 
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void changeMenuByIndex(int index) {
		
		Class<? extends Fragment> clazz = null;
		if (mCurrentIndex != index) {
			clearMenu();
			setMenuChecked(index);
		}
		clazz = FRAGMENTS_CLASSES[index];
		getFragmentActivity(MainActivity.class).switchCenterFragment(clazz);
		mCurrentIndex = index;
		
	}

	/**
	 * 通过Fragment类改变menu
	 * 
	 * @param clazz
	 */
	public void changeMenuByClass(Class<? extends Fragment> clazz) {
		int index = Arrays.asList(FRAGMENTS_CLASSES).indexOf(clazz);
		if (index != -1) {
			changeMenuByIndex(index);

		}
	}

	
	@SuppressWarnings("deprecation")
	private void clearMenu() {
		for (int i = 1; i <= mMenuLayouts.length; i++) {
			mMenuLayouts[i-1].setBackgroundDrawable(null);
			mMenuIcons[i - 1].setImageResource(MENU_NORMAL_ICONS[i - 1]);
			mMenuTexts[i - 1].setTextColor(getResources().getColor(R.color.gray7));
		}
	}

	private void setMenuChecked(int index) {
//		if (index == 0) {
//			return;
//		}
		if (index != 1 && index != 2) {
			mMenuLayouts[index].setBackgroundResource(R.drawable.sliding_menu_checked_bg);
		}
		mMenuIcons[index ].setImageResource(MENU_CHECKED_ICONS[index ]);
		mMenuTexts[index ].setTextColor(getResources().getColor(R.color.white));
	}





	



	@Override
	public void onDestroy() {
		if (mLoadingBitmap != null && !mLoadingBitmap.isRecycled()) {
			mLoadingBitmap.recycle();
			mLoadingBitmap = null;
		}

		super.onDestroy();
	}
}
