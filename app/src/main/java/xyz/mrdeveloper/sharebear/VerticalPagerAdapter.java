package xyz.mrdeveloper.sharebear;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by Mr. Developer on 29-04-2017.
 */

class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    ArrayList<Post> mPostList;
    private LayoutInflater mLayoutInflater;

    VerticalPagerAdapter(Context context, ArrayList<Post> postList) {
        mContext = context;
        mPostList = postList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPostList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int pos) {
        View itemView = mLayoutInflater.inflate(R.layout.newsfeed_page, container, false);

        TextView label = (TextView) itemView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        label.setText(mPostList.get(pos).caption);

        Glide
                .with(mContext)
                .load(mPostList.get(pos).imageURL)
                .centerCrop()
                .placeholder(R.drawable.ambassadors_logo)
                .crossFade()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .thumbnail(0.1f)
                .into(imageView);

        Log.d("Check", "Position: " + pos);
        Log.d("Check", "Total Photos: " + mPostList.size());

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
