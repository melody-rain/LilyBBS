package com.os.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.os.model.Article;
import com.os.slidingmenu.R;
import com.os.ui.ui2.NewArticleFragment;
import com.os.utility.DatabaseDealer;
import com.os.utility.DocParser;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jin on 2014/9/18.
 */
public class ShowBoardActivity extends SherlockFragmentActivity {

    private List<Map<String, Object>> dataMap;
    private List<Article> articleList;
    private SimpleAdapter sa;
    private ListView boardList;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    private String prePageUrl;
    private String nextPageUrl;
    private String boardName;

    public String getBoardName(){
        return boardName;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                dataMap.clear();
                List<Map<String, Object>> titleDataTmp = getData();
                dataMap.addAll(titleDataTmp);
                sa.notifyDataSetChanged();
            } else {
                Toast.makeText(ShowBoardActivity.this, "无法获得该板块列表", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void initList(final String fullUrl){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                final String fullBoardName = fullUrl;
                articleList = DocParser.getBoardArticleTitleList(fullBoardName, boardName, 3, DatabaseDealer.getBlockList(ShowBoardActivity.this));
                if (articleList == null) {
                    msg.what = -1;
                    handler.sendMessage(msg);
                    return;
                }
                msg.arg1 = 0;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    public void initList(String url, final String boardName) {
        initList(url + boardName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_board);

        final Intent intent = getIntent();
        ActionBar bar = getSupportActionBar();
        boardName = intent.getStringExtra("boardName");
        bar.setTitle(intent.getStringExtra("ChineseName"));

        dataMap = new ArrayList<Map<String, Object>>();
        sa = new SimpleAdapter(ShowBoardActivity.this, dataMap, R.layout.list_board, new String[] {"title","reply", "author"},
                new int[] {R.id.lb_title, R.id.lb_reply, R.id.lb_author});
        boardList = (ListView)findViewById(R.id.show_board);
        boardList.setAdapter(sa);

        boardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(ShowBoardActivity.this, ArticleActivity.class);
                intent1.putExtra("board", intent.getStringExtra("boardName"));
                intent1.putExtra("contentUrl", articleList.get(position).getContentUrl());
                intent1.putExtra("title", articleList.get(position).getTitle());
                startActivity(intent1);
            }
        });

        initList("http://bbs.nju.edu.cn/bbstdoc?board=", getIntent().getStringExtra("boardName"));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < articleList.size() - 1; i ++) {
            Article article = articleList.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", article.getTitle());
            map.put("author", "作者:" + article.getAuthorName());
            map.put("reply", article.getReply() + "/" + article.getView());
            list.add(map);
        }

        Article article = articleList.get(articleList.size() - 1);
        prePageUrl = article.getBoard() == null ? null : "http://bbs.nju.edu.cn/" + article.getBoard();
        nextPageUrl = article.getBoardUrl() == null ? null : "http://bbs.nju.edu.cn/" + article.getBoardUrl();
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.more_board, menu);
        return super.onCreateOptionsMenu(menu);

//        menu.add("新帖").setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add("上一页").setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add("下一页").setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_article:
                fm = getSupportFragmentManager();
                NewArticleFragment newArticleFragment = new NewArticleFragment();
                newArticleFragment.show(fm, NewArticleFragment.class.getName());
                break;
            case R.id.pre:
                if(prePageUrl != null){
                    initList(prePageUrl);
                }else{
                    Toast.makeText(this, "没有上一页了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.next:
                if(nextPageUrl != null){
                    initList(nextPageUrl);
                }else {
                    Toast.makeText(this, "没有下一页了", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

//    @Override
//    public void onActivityResult(int request, int resultCode, Intent data){
//
//    }
}
