package model;

public class User {
    private String id;
    private String username;
    private String fullname;
    private String image;
    private String bio;

    public User(String id, String username, String fullname, String imageurl, String bio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.image = imageurl;
        this.bio = bio;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImage() {
        return image;
    }

    public String getBio() {
        return bio;
    }
}
