package xyz.mrdeveloper.sharebear;

/**
 * Created by Mr. Developer on 01-05-2017.
 */

class Post {
    String caption;
    String URL;
    String type;
    String id;

    Post(String caption, String URL, String id, String type) {
        this.caption = caption;
        this.type = type;
        this.URL = URL;
        this.id = id;
    }
}
