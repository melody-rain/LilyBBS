package com.os.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.os.activity.sliding.SingleArticle;
import com.os.slidingmenu.R;
import com.os.ui.ui2.NewArticleFragment;
import com.os.ui.ui2.ReplyArticleFragment;
import com.os.utility.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View.OnClickListener;
import android.text.Html.ImageGetter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Jin on 2014/9/15.
 */
public class ArticleActivity extends SherlockFragmentActivity {

    private String board;
    private String title;
    private ListView lv;
    private List<Map<String, Object>> contentList;
    private int currentPage = 0;
    private List<SingleArticle> singleList;
    private int selectedIndex;
    private int totalPage = 0;
    private List<String> picPathList;
    private Map<String,Bitmap> picMap;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyListAdapter myListAdapter = new MyListAdapter(R.layout.list_single_article);
            lv.setAdapter(myListAdapter);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        initComponents();
        initList(true);
    }

    private void initComponents() {
        lv = (ListView) findViewById(R.id.lv);
        board = getIntent().getStringExtra("board");

        title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setIcon(android.R.color.transparent);
    }

    private void initList(final boolean loadImage) {
        contentList = new ArrayList<Map<String, Object>>();
        Thread initContentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> contentListTmp;
                while (true) {
                    contentListTmp = getData();
                    if(loadImage){
                        initPic();
                    }
                    if (contentListTmp != null) {
                        contentList.addAll(contentListTmp);
                        handler.sendEmptyMessage(0);
                        break;
                    }
                }
            }
        });
        initContentThread.start();
    }

    private boolean isPic(String source) {
        return source.startsWith("http://bbs.nju.edu.cn/file") &&
                (source.endsWith(".jpg") || source.endsWith(".JPG") || source.endsWith(".png") ||
                        source.endsWith(".PNG")|| source.endsWith(".bmp") || source.endsWith("jpeg") || source.endsWith("JPEG")
                        || source.endsWith("gif") || source.endsWith("GIF"));
    }



    private void initPic() {
        if (singleList == null || singleList.size() == 0) {
            return;
        }

        picPathList = new ArrayList<String>();
        for(SingleArticle article : singleList) {
            String content = article.getContent();
            if (content == null || content.indexOf("http://bbs.nju.edu.cn/file") < 0) {
                continue;
            }
            Document doc = Jsoup.parse(content);
            Elements imgs = doc.getElementsByTag("img");
            for(Element pic: imgs) {
                String path = pic.attr("src");
                if(!isPic(path)) {
                    continue;
                }
                picPathList.add(path);
            }
        }
        if (picPathList.size() > 0) {

            Thread savePic = new Thread(new Runnable() {

                @Override
                public void run() {
                    for(int i = 0; i < picPathList.size(); i ++) {
                        String source = picPathList.get(i);
                        String picDir = FileDealer.getPicDirPath() + source.substring(source.lastIndexOf("/"));
                        File file = new File(picDir);
                        if (file.exists() && picMap == null) {
                            picMap = new HashMap<String, Bitmap>();
                            picMap.put(picDir, BitmapFactory.decodeFile(picDir));
                        } else if (file.exists() && !picMap.containsKey(picDir)) {
                            picMap.put(picDir, BitmapFactory.decodeFile(picDir));
                        } else if (!file.exists() && (picMap != null && picMap.containsKey(picDir)
                                && !picMap.get(picDir).isRecycled()) && false/*DatabaseDealer.getSettings(ArticleActivity.this).isSavePic()*/) {
                            FileDealer.writeBitmap(picMap.get(picDir),picDir);
                        } else {
                            if(picMap == null) {
                                picMap = new HashMap<String, Bitmap>();
                            }
                            picMap.put(picDir, FileDealer.downloadBitmap(source));
//                    if(DatabaseDealer.getSettings(ArticleActivity.this).isSavePic()) {
//                        Bitmap bm = picMap.get(picDir);
//                        if(bm == null || picMap.get(picDir).isRecycled()) {
//                            bm = FileDealer.downloadBitmap(source);
//                        }
//                        FileDealer.writeBitmap(picMap.get(picDir),picDir);
//                    }
                        }
                    }
                    Message msg = Message.obtain();
                    handler.sendEmptyMessage(0);
                }
            });

            savePic.start();
        }
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String pageString = "";
        if (currentPage != 0) {
            pageString = "&start=" + String.valueOf(currentPage);
        }

        singleList = DocParser.getSingleArticleList(this.getIntent().getStringExtra("contentUrl") + pageString, 3, DatabaseDealer.getBlockList(ArticleActivity.this));
        totalPage = Integer.parseInt(singleList.get(singleList.size() - 1).getAuthorName());
        if (singleList == null) {
            return null;
        }
        for (int i = 0; i < singleList.size() - 1; i++) {
            SingleArticle article = singleList.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("author", "作者:" + article.getAuthorName());

            if(i == 0){
                map.put("floor", "楼主");
            }else {
                if(currentPage != -1){
                    map.put("floor", singleList.indexOf(article) + currentPage + "楼");
                }else {
                    map.put("floor", singleList.indexOf(article) + "楼");
                }
            }

            map.put("content", article.getContent());
            list.add(map);
        }
        return list;
    }

    private class MyListAdapter extends BaseAdapter {

        int layoutID;
        private View view;
        private TextView author;
        private TextView floor;
        private TextView content;
        private TextView reply;

        public MyListAdapter(int layoutID) {
            this.layoutID = layoutID;
        }

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public Object getItem(int position) {
            return contentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            view = ArticleActivity.this.getLayoutInflater().inflate(layoutID, null);
            author = (TextView) view.findViewById(R.id.sa_author);
            floor = (TextView) view.findViewById(R.id.sa_floor);
            content = (TextView) view.findViewById(R.id.sa_content);
            reply = (TextView) view.findViewById(R.id.sa_reply);

            Map<String, Object> map = contentList.get(position);
            author.setText((CharSequence) map.get("author"));
            floor.setText((CharSequence) map.get("floor"));
            String articleContent = (String) map.get("content");
            if (articleContent.contains("<img src='") || articleContent.contains("<uid>")) {
                content.setText(Html.fromHtml(articleContent, imageGetter, null));
            } else {
            content.setText(articleContent);
            }

            reply.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    ReplyArticleFragment replyArticleFragment = new ReplyArticleFragment();
                    replyArticleFragment.show(fm, "reply_article_fragment");
                    selectedIndex = position;
//                    Intent intent = new Intent(ArticleActivity.this, ReplyArticle.class);
//                    intent.putExtra("isTitleVisiable", false);
//                    startActivityForResult(intent, 1);
                }
            });
//
//            author.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(ArticleActivity.this, Author.class);
//                    intent.putExtra("authorUrl", singleList.get(position).getAuthorUrl());
//                    intent.putExtra("authorName", singleList.get(position).getAuthorName());
//                    startActivity(intent);
//                }
//            });
            return view;
        }

    }

    private boolean sendReply(String replyContent, String picPath) {

        String replyUrl = singleList.get(selectedIndex).getReplyUrl();
        String authorName = singleList.get(selectedIndex).getAuthorName();
        String boardName = replyUrl.substring(replyUrl.indexOf("=") + 1, replyUrl.indexOf("&"));
        String reIdString = replyUrl.substring(replyUrl.indexOf("M.") + 2);
        reIdString = reIdString.substring(0, reIdString.indexOf(".A"));

        String pidString = DocParser.getPid(replyUrl, 3, ArticleActivity.this);
        if (pidString == null) {
            return false;
        }
        return DocParser.sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, ArticleActivity.this, 3);
    }

    public void onSubmitReply(final String replyContent) {
        Thread submitReply = new Thread(new Runnable() {
            @Override
            public void run() {
                if (selectedIndex < 0 || selectedIndex > singleList.size() - 1) {
                    return;
                }

                boolean success = sendReply(replyContent, null);
            }
        });
        submitReply.start();
        Toast.makeText(this, replyContent, Toast.LENGTH_SHORT).show();
    }

//    private  boolean isPic(String source) {
//        return source.startsWith("http://bbs.nju.edu.cn/file") &&
//                (source.endsWith(".jpg") || source.endsWith(".JPG") || source.endsWith(".png") ||
//                        source.endsWith(".PNG")|| source.endsWith(".bmp") || source.endsWith("jpeg") || source.endsWith("JPEG")
//                        || source.endsWith("gif") || source.endsWith("GIF"));
//    }
//
    private ImageGetter imageGetter = new ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable;
            if(isPic(source)) {
                String picDir = FileDealer.getPicDirPath() + source.substring(source.lastIndexOf("/"));
                File file = new File(picDir);
                int width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);

                if (picMap != null && picMap.containsKey(picDir) && !picMap.get(picDir).isRecycled()) {
                    Bitmap bm= picMap.get(picDir);
                    drawable = new BitmapDrawable(bm);
                    int bmWidth = bm.getWidth();
                    int bmHeight = bm.getHeight();
                    drawable.setBounds(0, 0, width, width * bmHeight / bmWidth);
                    return drawable;
                } else if (file.exists() && BitmapFactory.decodeFile(picDir) != null) {
                    if (picMap == null) {
                        picMap = new HashMap<String, Bitmap>();
                    }
                    Bitmap bm = BitmapFactory.decodeFile(picDir);
                    picMap.put(picDir, bm);
                    drawable = new BitmapDrawable(bm);
                    int bmWidth = bm.getWidth();
                    int bmHeight = bm.getHeight();
                    drawable.setBounds(0, 0, width, width * bmHeight / bmWidth);
                    return drawable;
                } else {
                    drawable = getResources().getDrawable(R.drawable.downloading);
                    drawable.setBounds(0, 0, width, width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                    return drawable;
                }
            } else if(source.equals("emotion_s")) {
                drawable = getResources().getDrawable(R.drawable.emotion_s);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_o")) {
                drawable = getResources().getDrawable(R.drawable.emotion_o);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_v")) {
                drawable = getResources().getDrawable(R.drawable.emotion_v);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_d")) {
                drawable = getResources().getDrawable(R.drawable.emotion_d);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_x")) {
                drawable = getResources().getDrawable(R.drawable.emotion_x);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_q")) {
                drawable = getResources().getDrawable(R.drawable.emotion_q);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } else if(source.equals("emotion_a")) {
                drawable = getResources().getDrawable(R.drawable.emotion_a);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_p")) {
                drawable = getResources().getDrawable(R.drawable.emotion_p);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_e")) {
                drawable = getResources().getDrawable(R.drawable.emotion_e);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_h")) {
                drawable = getResources().getDrawable(R.drawable.emotion_h);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_b")) {
                drawable = getResources().getDrawable(R.drawable.emotion_b);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_c")) {
                drawable = getResources().getDrawable(R.drawable.emotion_c);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_f")) {
                drawable = getResources().getDrawable(R.drawable.emotion_f);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_g")) {
                drawable = getResources().getDrawable(R.drawable.emotion_g);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_i")) {
                drawable = getResources().getDrawable(R.drawable.emotion_i);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_j")) {
                drawable = getResources().getDrawable(R.drawable.emotion_j);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_k")) {
                drawable = getResources().getDrawable(R.drawable.emotion_k);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_l")) {
                drawable = getResources().getDrawable(R.drawable.emotion_l);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_m")) {
                drawable = getResources().getDrawable(R.drawable.emotion_m);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_n")) {
                drawable = getResources().getDrawable(R.drawable.emotion_n);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_r")) {
                drawable = getResources().getDrawable(R.drawable.emotion_r);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_t")) {
                drawable = getResources().getDrawable(R.drawable.emotion_t);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_u") ) {
                drawable = getResources().getDrawable(R.drawable.emotion_u);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_w")) {
                drawable = getResources().getDrawable(R.drawable.emotion_w);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_y")) {
                drawable = getResources().getDrawable(R.drawable.emotion_y);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_z")) {
                drawable = getResources().getDrawable(R.drawable.emotion_z);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }else if(source.equals("emotion_0")) {
                drawable = getResources().getDrawable(R.drawable.emotion_0);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_1")) {
                drawable = getResources().getDrawable(R.drawable.emotion_1);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_2")) {
                drawable = getResources().getDrawable(R.drawable.emotion_2);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_3")) {
                drawable = getResources().getDrawable(R.drawable.emotion_3);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_4")) {
                drawable = getResources().getDrawable(R.drawable.emotion_4);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_5")) {
                drawable = getResources().getDrawable(R.drawable.emotion_5);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_6")) {
                drawable = getResources().getDrawable(R.drawable.emotion_6);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_7")) {
                drawable = getResources().getDrawable(R.drawable.emotion_7);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_8")) {
                drawable = getResources().getDrawable(R.drawable.emotion_8);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            else if(source.equals("emotion_9")) {
                drawable = getResources().getDrawable(R.drawable.emotion_9);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            return null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.more_article, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int fragment0 = totalPage / 30;
        int fragment1 = totalPage % 30;
        switch (item.getItemId()){
            case R.id.pre:
                if(currentPage == 0){
                    Toast.makeText(this, "已经在首页", Toast.LENGTH_SHORT).show();
                }else if(currentPage == -1){
                    if(fragment0 > 0){
                        currentPage = (fragment0 - 1) * 30;
                        if(fragment1 == 0){
                            currentPage = currentPage - 30;
                        }
                    }else{
                        currentPage = 0;
                    }
                    initList(true);
                }else{
                    currentPage = currentPage - 30;
                    initList(true);
                }
                break;
            case R.id.post:
                if(currentPage > 0){
                    if(currentPage >= fragment0 * 30){
                        Toast.makeText(this, "本篇没有下30个主题", Toast.LENGTH_SHORT).show();
                    }else{
                        currentPage = currentPage + 30;
                        initList(true);
                    }
                }else if(currentPage < 0){
                    Toast.makeText(this, "本篇没有下30个主题", Toast.LENGTH_SHORT).show();
                }else{
                    if(fragment0 == 0){
                        Toast.makeText(this, "本篇没有下30个主题", Toast.LENGTH_SHORT).show();
                    }else{
                        currentPage = currentPage + 30;
                        initList(true);
                    }
                }

                break;
            case R.id.all:
                currentPage = -1;
                initList(true);
        }

        return super.onOptionsItemSelected(item);
    }
}
