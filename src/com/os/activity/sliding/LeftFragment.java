package com.os.activity.sliding;

import java.util.Arrays;

import android.os.Handler;

import java.util.logging.LogRecord;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.widget.*;
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
import com.os.utility.DatabaseDealer;
import com.os.utility.LoginHelper;
import com.os.utility.LoginInfo;
import com.os.utility.LogoutHelper;

public class LeftFragment extends BaseSlidingFragment {

    private Button loginBt;
    private Button logoutBt;
    private CheckBox checkBox;
    private EditText account;
    private EditText passwd;

    private String accountStr;
    private String passwdStr;

    private boolean isAutoLogin = false;

    private final static int MENU_NORMAL_ICONS[] = {R.drawable.sliding_livehall_icon_normal,
            R.drawable.mail_normal, R.drawable.sliding_rank_icon_normal};

    private final static int MENU_CHECKED_ICONS[] = {R.drawable.sliding_livehall_icon_checked,
            R.drawable.mail_checked, R.drawable.sliding_rank_icon_checked};

    public final static Class[] FRAGMENTS_CLASSES = {MainHallFragment.class, FollowFragment.class,
            RankFragment.class};

    private View[] mMenuLayouts;
    private ImageView[] mMenuIcons;
    private TextView[] mMenuTexts;


    private Bitmap mLoadingBitmap;
    private int mCurrentIndex = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                Toast.makeText(getActivity(), "用户名或者密码错误，请重新输入", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0) {
                DatabaseDealer.deleteAcc(getActivity());
                DatabaseDealer.insert(getActivity(), accountStr, passwdStr, (isAutoLogin == true ? 1 : 0));
                Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Handler logoutHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0) {
                Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "注销失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sliding_left);

        setData();
        changeMenuByClass(MainHallFragment.class);
    }

    @Override
    public void initViews() {

        mMenuLayouts = new View[]{findViewById(R.id.menu_livehall_layout),
                findViewById(R.id.menu_follow_layout), findViewById(R.id.menu_rank_layout)};

        mMenuIcons = new ImageView[]{
                (ImageView) findViewById(R.id.menu_livehall_icon), (ImageView) findViewById(R.id.menu_follow_icon), (ImageView) findViewById(R.id.menu_rank_icon)};

        mMenuTexts = new TextView[]{
                (TextView) findViewById(R.id.menu_livehall_text), (TextView) findViewById(R.id.menu_follow_text), (TextView) findViewById(R.id.menu_rank_text),};

        Bundle userInfo = DatabaseDealer.query(getActivity());

        isAutoLogin = (userInfo.getInt("isAutoLogin") == 1 ? true : false);

        account = (EditText) findViewById(R.id.account);
        passwd = (EditText) findViewById(R.id.passwd);

        loginBt = (Button) findViewById(R.id.login_bt);

        loginBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account.getText().length() == 0 ||
                        passwd.getText().length() == 0) {
                    Toast.makeText(getActivity(), "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                accountStr = account.getText().toString();
                passwdStr = passwd.getText().toString();

                final Bundle bundle = new Bundle();
                bundle.putString("username", accountStr);
                bundle.putString("password", passwdStr);

                Thread loginThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoginInfo loginInfo = LoginHelper.getInstance(bundle);
                        Message msg = new Message();

                        if (loginInfo == null) {
                            msg.what = -1;
                            handler.sendMessage(msg);
                            return;
                        }

                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                });

                loginThread.start();
            }
        });

        CheckBox autoLogin = (CheckBox) findViewById(R.id.autoLogin);
        if(isAutoLogin == true){
            account.setText(userInfo.getString("username"));
            passwd.setText(userInfo.getString("password"));
            autoLogin.setChecked(true);

            final Bundle bundle = new Bundle();
            bundle.putString("username", userInfo.getString("username"));
            bundle.putString("password", userInfo.getString("password"));

            Thread loginThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginInfo loginInfo = LoginHelper.getInstance(bundle);
                    Message msg = new Message();

                    if (loginInfo == null) {
                        msg.what = -1;
                        handler.sendMessage(msg);
                        return;
                    }

                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            });

            loginThread.start();

        }else{
            autoLogin.setChecked(false);
        }
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoLogin = !isAutoLogin;
            }
        });

        logoutBt = (Button)findViewById(R.id.logout_bt);
        logoutBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                bundle.putString("username", accountStr);
                bundle.putString("password", passwdStr);
                Thread logoutThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();

                        if(LogoutHelper.Logout(bundle)) {
                            msg.what = 0;
                        }else{
                            msg.what = -1;
                        }
                        logoutHandler.sendMessage(msg);
                    }
                });

                logoutThread.start();
            }
        });
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
            mMenuLayouts[i - 1].setBackgroundDrawable(null);
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
        mMenuIcons[index].setImageResource(MENU_CHECKED_ICONS[index]);
        mMenuTexts[index].setTextColor(getResources().getColor(R.color.white));
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
