package com.os.activity;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.os.activity.base.BaseFragmentActivity;
import com.os.activity.base.BaseSlidingFragment;
import com.os.activity.sliding.LeftFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.os.model.Article;
import com.os.slidingmenu.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.os.ui.FollowFragment;
import com.os.ui.RankFragment;
import com.os.utility.DatabaseDealer;
import com.os.utility.DocParser;

public class MainActivity extends BaseFragmentActivity {
	private Fragment mCurFragment;
	public static SlidingMenu mSlidingMenu;
	private Handler handler = new MyHandler(this);
    public List<Article> topList;
    private Thread getUserInfo;

    private RankFragment rankFragment;
    private FollowFragment followFragment;
    public boolean isQuit;
    private static class MyHandler extends Handler {
		private final WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity activity = mActivity.get();
			if (activity == null) {
				return;
			}
			activity.handleMsg(msg);
		}
	}
	
	private void handleMsg(Message msg) {
        Toast.makeText(MainActivity.this, "网络连接失败，请稍后再试", Toast.LENGTH_SHORT).show();
        isQuit = true;
        finish();
	}

    private void initComplements(){
        getUserInfo = new Thread(new Runnable() {
            @Override public
            void run() {

                topList = DocParser.getArticleTitleList(
                        "http://bbs.nju.edu.cn/bbstop10", 3,
                        DatabaseDealer.getBlockList(MainActivity.this));

                if(topList == null){
                    handler.sendEmptyMessage(0);
                    return;
                }
            }
        });
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("南京大学小百合");
        bar.setIcon(R.drawable.nju_logo_purple);

        isQuit = false;
		setContentView(R.layout.activity_main);
		initViews();
	    initComplements();

		if (savedInstanceState != null) {
			mCurFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mCurContent");
		}
        getUserInfo.start();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add("设置").setIcon(R.drawable.ic_launcher_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        return true;
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mCurContent", mCurFragment);
	}
	private void initViews() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingmenu);
		mSlidingMenu.setMenu(R.layout.sliding_left_frame);
		if (getFragmentByTag(LeftFragment.class) == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.left_frame, new LeftFragment(), LeftFragment.class.getName()).commit();
		}

		mSlidingMenu.setContent(R.layout.sliding_center_frame);

		if (mCurFragment != null) {
			postSwitchFragment();
		}
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}
	/**
	 * slidingMenu中的内容Fragment切换(左侧菜单触发)
	 * 
	 * @param clazz
	 */
	public void switchCenterFragment(Class<? extends Fragment> clazz) {
		try {
			if (mSlidingMenu == null) {
				removeAllFragments();
				return;
			}

			boolean isInit = false;
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment userFragment = fm.findFragmentByTag(clazz.getName());

			if (userFragment == null) {
				isInit = true;
				try {
					userFragment = clazz.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (mCurFragment != null && mCurFragment != userFragment) {
				ft.hide(mCurFragment);
			}

			if (!userFragment.isAdded() && isInit) {
				ft.add(R.id.center_frame, userFragment, clazz.getName());
			} else {
				ft.show(userFragment);
			}

			ft.commitAllowingStateLoss();

			mCurFragment = userFragment;
			
			postShowContent(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * lidingMenu中的内容Fragment内容过滤(右侧菜单触发)
	 * 
	 * @param clazz
	 * @param type
	 */
	public void filterCenterFragment(Class<? extends BaseSlidingFragment> clazz, int type) {
		BaseSlidingFragment userFragment = (BaseSlidingFragment) getFragmentByTag(clazz);
		if (userFragment != null) {
			userFragment.filter(type);
		}
		if (mSlidingMenu != null)
			mSlidingMenu.showContent();
	}
	/**
	 * 延迟切换Fragment
	 */
	private void postSwitchFragment() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				switchCenterFragment(mCurFragment.getClass());
			}
		}, 50);

	}
	
	/**
	 * 清除FragmentManager中所有Fragment
	 */
	private void removeAllFragments() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		for (int i = 0; i < LeftFragment.FRAGMENTS_CLASSES.length; i++) {
			Fragment fragment = getFragmentByTag(LeftFragment.FRAGMENTS_CLASSES[i].getName());
			if (fragment != null) {
				ft.remove(fragment);
			}
		}
		ft.commitAllowingStateLoss();
	}
	
	/**
	 * 延时mSlidingMenu.showContent()
	 * 
	 * @param delayMillis 延时时间 单位毫秒
	 */
	private void postShowContent(long delayMillis) {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mSlidingMenu!=null && !MainActivity.this.isFinishing()) {
					mSlidingMenu.showContent();					
				}
			}
		}, delayMillis);
	}
}
