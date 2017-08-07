package xyz.mrdeveloper.sharebear;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Mr. Developer on 01-05-2017.
 */

class Post {

    String caption;
    String type;
    ArrayList<String> URLs;
    String id;
    String videoPath;

    Post(String caption, String URL, String id, String type) {
        this.URLs = new ArrayList<>();
        this.caption = caption;
        this.type = type;
        this.URLs.add(0, URL);
        this.id = id;
    }
}
