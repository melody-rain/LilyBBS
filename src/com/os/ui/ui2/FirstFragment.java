package com.os.ui.ui2;


import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.os.activity.ArticleActivity;
import com.os.activity.MainActivity;
import com.os.model.Article;
import com.os.slidingmenu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Handler;
import com.os.utility.DatabaseDealer;
import com.os.utility.DocParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment {

    private View viewFragment;
    public List<Article> topList;
    private List<Map<String, Object>> dataMap;
    private Thread getTopList;
    private ListView lv;
    private SimpleAdapter sa;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (topList != null) {
                List<Map<String, Object>> titleDataTmp = getData();
                dataMap.addAll(titleDataTmp);

                sa.notifyDataSetChanged();
            }
        }
    };

    private void waitTopList() {
        getTopList = new Thread(new Runnable() {

            @Override
            public void run() {
                MainActivity mainActivity = (MainActivity) getActivity();
                while (mainActivity.isQuit == false) {
                    if (mainActivity.topList != null) {
                        topList = mainActivity.topList;
                        handler.sendEmptyMessage(0);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        dataMap = new ArrayList<Map<String, Object>>();

        sa = new SimpleAdapter(getActivity(), dataMap, R.layout.list_hot, new String[] {"title","board", "author"},
                new int[] {R.id.lh_title, R.id.lh_board, R.id.lh_author});

        viewFragment = inflater.inflate(R.layout.first, null);
        waitTopList();
        getTopList.start();

        lv = (ListView) viewFragment.findViewById(R.id.top_list);
        lv.setAdapter(sa);

        addListener();

        return viewFragment;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Article article : topList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", article.getTitle());
            map.put("author", "作者:" + article.getAuthorName());
            map.put("board", "版块:" + article.getBoard());
            list.add(map);
        }
        return list;
    }

    private void addListener(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ArticleActivity.class);
                intent.putExtra("board", topList.get(position).getBoard());
                intent.putExtra("contentUrl", topList.get(position).getContentUrl());
                intent.putExtra("title", topList.get(position).getTitle());
                startActivity(intent);
            }
        });
    }
}
