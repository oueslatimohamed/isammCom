package model;

public class Post {
    private String postId;
    private String postImage;
    private String description;
    private String publisher;

    public Post(String postid, String postimage, String description, String publisher) {
        this.postId = postid;
        this.postImage = postimage;
        this.description = description;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postid) {
        this.postId = postid;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postimage) {
        this.postImage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
