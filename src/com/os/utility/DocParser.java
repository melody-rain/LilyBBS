package com.os.utility;

import android.content.Context;
import android.os.Bundle;
import com.os.activity.sliding.SingleArticle;
import com.os.model.Article;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jin on 2014/9/10.
 */
public class DocParser {
    public static final List<Article> getArticleTitleList(String url, int tryTimes, List<String> blockList) {
        if (tryTimes <= 0) {
            return null;
        }
        try {
            List<Article> list = new ArrayList<Article>();
            Document doc = Jsoup.connect(url).timeout(5000).get();
            Elements blocks = doc.select("tr");
            for (Element block : blocks) {
                Elements links = block.select("a[href]");
                if (links.size() == 0) {
                    continue;
                }
                links = block.select("td");
                String authorName = links.get(3).select("a").text();
                if (blockList.contains(authorName)) {
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

    public static final List<SingleArticle> getSingleArticleList(String url, int tryTimes, List<String> blockList) {
        if(tryTimes <= 0) {
            return null;
        }
        try {
            List<SingleArticle> list = new ArrayList<SingleArticle>();
            Document doc = Jsoup.connect(url).get();
            Elements blocks = doc.select("tr");
            for (int i = 0; i < blocks.size() - 1; i++) {
                Elements links = blocks.get(i).select("a[href]");
                if (links.size()==0) {
                    continue;
                }
                Elements content =  blocks.get(i + 1).select("textarea");
                String authorName = links.get(2).select("a").text();
                if(blockList.contains(authorName)) {
                    continue;
                }
                SingleArticle article = new SingleArticle();
                article.setAuthorName(authorName);
                article.setAuthorUrl(links.get(2).select("a").attr("abs:href"));
                article.setReplyUrl(links.get(1).select("a").attr("abs:href"));
                article.setContent(formatContent(content.get(0).text()));
                list.add(article);
            }
            String allString = doc.toString();
            String s = allString.substring(allString.indexOf("</script>本主题共有 ") + 15);
            SingleArticle article = new SingleArticle();
            article.setAuthorName(s.substring(0, s.indexOf(" ")));
            list.add(article);

            return list;
        } catch (IOException e) {
            return getSingleArticleList(url, tryTimes - 1, blockList);
        }
    }

    private static final String formatContent(String content) {
        String result = content.substring(content.indexOf("发信站: 南京大学小百合站 (") + 41);
        result = result.indexOf("-- ") > 0 ? result.substring(0,result.indexOf("-- ")) : result;
        result = result.replace("http://bbs.nju.edu.cn/file", "<br/><img src='http://bbs.nju.edu.cn/file");
        if(result.indexOf("[") >= 0) {
            result = result.replace("[:s]", "<img src='emotion_s'/>");
            result = result.replace("[:O]", "<img src='emotion_o'/>");
            result = result.replace("[:|]", "<img src='emotion_v'/>");
            result = result.replace("[:$]", "<img src='emotion_d'/>");
            result = result.replace("[:X]", "<img src='emotion_x'/>");
            result = result.replace("[:'(]", "<img src='emotion_q'/>");
            result = result.replace("[:@]", "<img src='emotion_a'/>");
            result = result.replace("[:-|]", "<img src='emotion_h'/>");
            result = result.replace("[:P]", "<img src='emotion_p'/>");
            result = result.replace("[:D]", "<img src='emotion_e'/>");
            result = result.replace("[:)]", "<img src='emotion_b'/>");
            result = result.replace("[:(]", "<img src='emotion_c'/>");
            result = result.replace("[:Q]", "<img src='emotion_f'/>");
            result = result.replace("[:T]", "<img src='emotion_g'/>");
            result = result.replace("[;P]", "<img src='emotion_i'/>");
            result = result.replace("[;-D]", "<img src='emotion_j'/>");
            result = result.replace("[:!]", "<img src='emotion_k'/>");
            result = result.replace("[:L]", "<img src='emotion_l'/>");
            result = result.replace("[:?]", "<img src='emotion_m'/>");
            result = result.replace("[:U]", "<img src='emotion_n'/>");
            result = result.replace("[:K]", "<img src='emotion_r'/>");
            result = result.replace("[:C-]", "<img src='emotion_t'/>");
            result = result.replace("[;X]", "<img src='emotion_u'/>");
            result = result.replace("[:H]", "<img src='emotion_w'/>");
            result = result.replace("[;bye]", "<img src='emotion_y'/>");
            result = result.replace("[;cool]", "<img src='emotion_z'/>");
            //[:-b][:-8][;PT][:hx][;K][:E][:-(][;hx][:-v][;xx]
            result = result.replace("[:-b]", "<img src='emotion_0'/>");
            result = result.replace("[:-8]", "<img src='emotion_1'/>");
            result = result.replace("[;PT]", "<img src='emotion_2'/>");
            result = result.replace("[:hx]", "<img src='emotion_3'/>");
            result = result.replace("[;K]", "<img src='emotion_4'/>");
            result = result.replace("[:E]", "<img src='emotion_5'/>");
            result = result.replace("[:-(]", "<img src='emotion_6'/>");
            result = result.replace("[;hx]", "<img src='emotion_7'/>");
            result = result.replace("[:-v]", "<img src='emotion_8'/>");
            result = result.replace("[;xx]", "<img src='emotion_9'/>");
        }
        result = result.replace("[uid]", "<uid>");
        result = result.replace("[/uid]", "</uid>");
        result = result.replace("jpg", "jpg'/><br/>");
        result = result.replace("JPG", "JPG'/><br/>");
        result = result.replace("gif", "gif'/><br/>");
        result = result.replace("GIF", "GIF'/><br/>");
        result = result.replace("png", "png'/><br/>");
        result = result.replace("PNG", "PNG'/><br/>");
        result = result.replace("jpeg", "jpeg'/><br/>");
        result = result.replace("JPEG", "JPEG'/><br/>");
        result = result.replaceAll("\\[(1;.*?|37;1|32|33)m", "");
        return result;
    }

    public static final String getPid(String replyUrl,int tryTimes, Context context) {
        if(tryTimes <= 0) {
            return null;
        }
        LoginInfo loginInfo;
        if(tryTimes == 1) {
            loginInfo = LoginHelper.resetLoginInfo(context);
        } else {
            loginInfo = LoginHelper.getInstance(context);
        }
        String tempString = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + replyUrl.substring(replyUrl.indexOf("/bbspst"));
        URL mUrl;
        try {
            mUrl = new URL(tempString);
            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestProperty("Cookie", loginInfo.getLoginCookie());
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();
                InputStream in = conn.getInputStream();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(in,"gb2312"));
                String inputLine = null;
                while ((inputLine = reader1.readLine()) != null) {
                    if ( inputLine.contains("name=pid") ) {
                        String temp = inputLine.substring(inputLine.indexOf("name=pid"));
                        if (temp.indexOf("value='")!=-1 && temp.indexOf("'>")!=-1) {
                            reader1.close();
                            in.close();
                            return temp.substring(temp.indexOf("value='") + 7,temp.indexOf("'>"));
                        }
                    }
                }
            } catch (IOException e) {
                return getPid(replyUrl,tryTimes -1, context);
            }

        } catch (MalformedURLException e) {
            return getPid(replyUrl, tryTimes -1, context);
        }
        return getPid(replyUrl,tryTimes -1, context);
    }

    public static final LoginInfo login(Bundle userInfo) {
        return login(userInfo.getString("username"),userInfo.getString("password"), 3);
    }

    public static final LoginInfo login(String username, String password, int tryTimes) {
        if(tryTimes <= 0) {
            return null;
        }
        int s = new Random().nextInt(99999)%(90000) + 10000;
        String urlString = "http://bbs.nju.edu.cn/vd" + String.valueOf(s) + "/bbslogin?type=2&id=" + username + "&pw=" + password;
        try {
            String doc = Jsoup.connect(urlString).get().toString();
            if (doc.indexOf("setCookie") < 0) {
                return login(username, password, tryTimes - 1);
            } else {
                LoginInfo info = new LoginInfo();
                String loginString = doc.substring(doc.indexOf("setCookie"));
                loginString =  loginString.substring(11, loginString.indexOf(")") - 1) + "+vd" + String.valueOf(s);
                String[] tmpString =  loginString.split("\\+");
                String _U_KEY = String.valueOf(Integer.parseInt(tmpString[1])-2);
                String[] loginTmp = tmpString[0].split("N");
                String _U_UID = loginTmp[1];
                String _U_NUM = "" + String.valueOf(Integer.parseInt(loginTmp[0]) + 2);
                info.setLoginCookie("_U_KEY=" + _U_KEY + "; " + "_U_UID=" + _U_UID + "; " + "_U_NUM=" + _U_NUM + ";");
                info.setLoginCode(tmpString[2]);
                info.setUsername(username);
                info.setPassword(password);
                return info;
            }
        } catch (IOException e) {
            return login(username, password, tryTimes - 1);
        }
    }

    public static boolean sendReply(String boardName, String title, String pidString, String reIdString, String replyContent, String authorName, String picPath, Context context, int tryTimes) {
        if (tryTimes <= 0) {
            return false;
        }
        LoginInfo loginInfo;
        if (tryTimes == 1) {
            loginInfo = LoginHelper.resetLoginInfo(context);
        } else {
            loginInfo = LoginHelper.getInstance(context);
        }
        try {
            String newurlString = "http://bbs.nju.edu.cn/" + loginInfo.getLoginCode() + "/bbssnd?board=" + boardName;
            HttpPost httpRequest = new HttpPost(newurlString);
            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair("title", title.replace("○", "Re:")));
            postData.add(new BasicNameValuePair("pid", pidString));
            postData.add(new BasicNameValuePair("reid", reIdString));
            postData.add(new BasicNameValuePair("signature", "1"));
            postData.add(new BasicNameValuePair("autocr", "on"));
            postData.add(new BasicNameValuePair("text", DocParser.formatString(replyContent, authorName, picPath, context)));
            httpRequest.addHeader("Cookie", loginInfo.getLoginCookie());
            httpRequest.setEntity(new UrlEncodedFormEntity(postData, "GB2312"));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return true;
            } else {
                return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
            }
        } catch (UnsupportedEncodingException e) {
            return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
        } catch (IOException e) {
            return sendReply(boardName, title, pidString, reIdString, replyContent, authorName, picPath, context, tryTimes - 1);
        }
    }

    public static final String formatString(String replyContent,String authorName,String picPath, Context context) {
        if (authorName != null) {
            replyContent += "【在  " +  authorName + "  的大作中提到】";
        }
        String finalString = replyContent;
        if(replyContent.length() > 40) {
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i <replyContent.length(); i++) {
                buffer.append(replyContent.charAt(i));
                if (i > 0 && i % 40 == 0) {
                    buffer.append('\n');
                }
            }
            finalString = buffer.toString();
        }
        finalString += picPath;
//        String sign = DatabaseDealer.getSettings(context).getSign();
//        if (sign != null && sign.length() > 0) {
//            finalString += ('\n' + "--" + sign);
//        }
        return finalString;
    }
}
