package com.os.activity.sliding;

/**
 * Created by Jin on 2014/9/16.
 */
public class SingleArticle {
    private String content;
    private String authorName;
    private String authorUrl;
    private String replyUrl;
    private String post_time;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getAuthorUrl() {
        return authorUrl;
    }
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }
    public String getReplyUrl() {
        return replyUrl;
    }
    public void setReplyUrl(String replyUrl) {
        this.replyUrl = replyUrl;
    }

    public String getPost_time(){
        return post_time;
    }

    public void setPost_time(String post_time){
        this.post_time = post_time;
    }
}
