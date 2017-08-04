package xyz.mrdeveloper.sharebear;

import java.util.ArrayList;

/**
 * Created by Mr. Developer on 01-05-2017.
 */

class Post {
    String caption;
    String type;
    ArrayList<String> URLs;
    String id;

    Post(String caption, String URL, String id, String type) {
        this.URLs = new ArrayList<>();
        this.caption = caption;
        this.type = type;
        this.URLs.add(0, URL);
        this.id = id;
    }
}
