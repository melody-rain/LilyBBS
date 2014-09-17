package com.os.ui.ui2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.os.activity.ArticleActivity;
import com.os.slidingmenu.R;

/**
 * Created by Jin on 2014/9/17.
 */
public class ReplyArticleFragment extends DialogFragment {

    private Button cancelBt;
    private Button submitBt;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceSate) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reply, null);
        builder.setView(view);

        editText = (EditText)view.findViewById(R.id.reply_article);
        submitBt = (Button) view.findViewById(R.id.submitBt);
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleActivity articleActivity = (ArticleActivity)getActivity();
                articleActivity.onSubmitReply(editText.getText().toString());
                getDialog().dismiss();
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
