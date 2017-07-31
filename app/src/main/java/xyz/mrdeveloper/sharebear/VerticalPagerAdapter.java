package xyz.mrdeveloper.sharebear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mr. Developer on 29-04-2017.
 */

class VerticalPagerAdapter extends PagerAdapter {

    private ArrayList<Post> mPostList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    static int position;

    VerticalPagerAdapter(Context context, ArrayList<Post> postList) {
        mPostList = postList;
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPostList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int pos) {
        View itemView = mLayoutInflater.inflate(R.layout.newsfeed_page, container, false);

        position = pos;
        Log.d("Check", "Position: " + Integer.toString(position));

        TextView label = (TextView) itemView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        label.setText(mPostList.get(pos).caption);

        Glide
                .with(mContext)
                .load(mPostList.get(pos).imageURL)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .thumbnail(0.1f)
                .into(imageView);

        container.addView(itemView);
        return itemView;
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
