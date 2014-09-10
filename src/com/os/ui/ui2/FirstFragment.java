package com.os.ui.ui2;


import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.os.model.Article;
import com.os.slidingmenu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private ListView lv;
    private List<Map<String, Object>> dataMap;
    private List<String> titleData;

    @Override
    public
    View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                     ) {
        viewFragment = inflater.inflate(R.layout.first, null);

        titleData = getData1();

        lv.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titleData));
        return viewFragment;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(topList == null) {
            topList = DocParser.getArticleTitleList("http://bbs.nju.edu.cn/bbstop10", 3, DatabaseDealer.getBlockList(getActivity()));
        }
        for(Article article : topList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", article.getTitle());
            map.put("author", "作者:" + article.getAuthorName());
            map.put("board", "版块:"+ article.getBoard());
            list.add(map);
        }
        return list;
    }

    private List<String> getData1() {
        List<String> list = new ArrayList<String>();
        if(topList == null) {
            topList = DocParser.getArticleTitleList("http://bbs.nju.edu.cn/bbstop10", 3, DatabaseDealer.getBlockList(getActivity()));
        }
        for(Article article : topList) {
            list.add(article.getTitle());
        }
        return list;
    }


}
