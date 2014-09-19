package com.os.ui.ui2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.os.activity.ArticleActivity;
import com.os.activity.ShowBoardActivity;
import com.os.slidingmenu.R;
import com.os.utility.DocParser;

/**
 * Created by Jin on 2014/9/19.
 */
public class NewArticleFragment extends SherlockDialogFragment{
    private Button cancelBt;
    private Button submitBt;
    private EditText title;
    private EditText content;
    ShowBoardActivity showBoardActivity;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                Toast.makeText(getActivity(), "发帖成功", Toast.LENGTH_SHORT).show();
                showBoardActivity.initList("http://bbs.nju.edu.cn/bbstdoc?board=", showBoardActivity.getBoardName());
            }else{
                Toast.makeText(getActivity(), "发帖失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        showBoardActivity = (ShowBoardActivity)getSherlockActivity();
        return null;
    }

    private boolean commitNewArticle(String replyContent, String title, String picPath) {
        return DocParser.sendReply(showBoardActivity.getBoardName(), title, "0", "0", replyContent, null, picPath, getActivity(), 3);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceSate) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_article, null);
        builder.setView(view);

        title = (EditText)view.findViewById(R.id.title);
        content = (EditText)view.findViewById(R.id.content);

        submitBt = (Button) view.findViewById(R.id.submitBt);
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contentStr = content.getText().toString();
                final String titleStr = title.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = commitNewArticle(contentStr, titleStr, null);
                        getDialog().dismiss();
                        Message msg = new Message();
                        if(success){
                            msg.what = 0;
                        }else{
                            msg.what = -1;
                        }
                        handler.sendMessage(msg);
                    }
                });

                thread.start();
            }
        });


        cancelBt = (Button) view.findViewById(R.id.cancelBt);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return builder.create();
    }
}
