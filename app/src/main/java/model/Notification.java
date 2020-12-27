package model;

public class Notification {
    String userid;
    String text;
    String postid;
    boolean ispost;

    public Notification(String userid, String text, String postid, boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public String getText() {
        return text;
    }

    public String getPostid() {
        return postid;
    }

    public boolean isIspost() {
        return ispost;
    }
}
