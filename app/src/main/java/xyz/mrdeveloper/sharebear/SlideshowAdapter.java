/*
package xyz.mrdeveloper.sharebear;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

*/
/**
 * Created by Vaibhav on 04-08-2017.
 *//*


public class SlideshowAdapter extends PagerAdapter {

    private Context mContext;
    private int mLayoutID;
    private int mImageViewID;
    private ArrayList<String> mImageURLS;

    SlideshowAdapter(Context context, int layoutID, int imageViewID, ArrayList<String> imageURLs) {
        mContext = context;
        mLayoutID = layoutID;
        mImageViewID = imageViewID;
        mImageURLS = imageURLs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(mLayoutID, container, false);

        ImageView imageViewPreview = (ImageView) view.findViewById(mImageViewID);

        imageViewPreview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = MotionEventCompat.getActionMasked(event);

                Log.d("Check", "MotionEvent : " + event);

                if (action == MotionEvent.ACTION_UP) {

                    if (imageCount.getVisibility() == View.INVISIBLE) {
                        imageCount.setVisibility(View.VISIBLE);
                        imageTitle.setVisibility(View.VISIBLE);
                    } else if (imageCount.getVisibility() == View.VISIBLE) {
                        imageCount.setVisibility(View.INVISIBLE);
                        imageTitle.setVisibility(View.INVISIBLE);
                    }
                }

                return true;
            }
        });

        Glide.with(mContext)
                .load(mImageURLS.get(position))
                .thumbnail(0.5f)
                .centerCrop()
                .crossFade()
                .placeholder(R.drawable.ambassadors_logo)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageViewPreview);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return mImageURLS.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((View) obj);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}*/
