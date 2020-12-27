package model;

public class Comment {
    private String comment;
    private String publisher;

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public Comment(){

    }

    public String getComment() {
        return comment;
    }

    public String getPublisher() {
        return publisher;
    }


}
