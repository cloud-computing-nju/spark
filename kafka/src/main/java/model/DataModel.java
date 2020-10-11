package model;

import com.google.gson.annotations.SerializedName;

/**
 * 爬虫数据结构
 */
public class DataModel {
    @SerializedName("pubdate")
    private long timeStamp; //发布时间

    @SerializedName("view")
    private int views; //播放量

    @SerializedName("danmaku")
    private int danmaku; //弹幕数

    @SerializedName("reply")
    private int reply; //回复

    @SerializedName("favorite")
    private int favorite; //收藏

    @SerializedName("coin")
    private int coins; //打赏

    @SerializedName("like")
    private int like; //点赞

    @SerializedName("tag_list")
    private String[] tags; //标签

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(int danmaku) {
        this.danmaku = danmaku;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
