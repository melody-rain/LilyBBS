package com.os.utility;

import com.os.model.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jin on 2014/9/10.
 */
public class DocParser {
    public static final
    List<Article> getArticleTitleList(String url, int tryTimes, List<String> blockList) {
        if(tryTimes <= 0) {
            return null;
        }
        try {
            List<Article> list = new ArrayList<Article>();
            Document doc = Jsoup.connect(url).timeout(5000).get();
            Elements blocks = doc.select("tr");
            for (Element block : blocks) {
                Elements links = block.select("a[href]");
                if (links.size()==0) {
                    continue;
                }
                links = block.select("td");
                String authorName = links.get(3).select("a").text();
                if(blockList.contains(authorName)) {
                    continue;
                }
                Article article = new Article();
                article.setAuthorName(authorName);
                article.setAuthorUrl(links.get(3).select("a").attr("abs:href"));
                article.setBoard(links.get(1).select("a").text());
                article.setBoardUrl(links.get(1).select("a").attr("abs:href"));
                article.setContentUrl(links.get(2).select("a").attr("abs:href"));
                article.setView(Integer.valueOf(links.get(4).text()));
                article.setTitle(links.get(2).select("a").text());
                list.add(article);
            }
            return list;
        } catch (IOException e) {
            return getArticleTitleList(url, tryTimes - 1, blockList);
        }
    }
}
