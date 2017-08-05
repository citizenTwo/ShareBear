/*
package xyz.mrdeveloper.sharebear;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

*/
/**
 * Created by Mr. Developer on 29-04-2017.
 *//*


class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Post> mPostList;
    private LinearLayout mLinearLayout;
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

        mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        container.addView(mLinearLayout);

        TextView textView = new TextView(mContext);

        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.3f));

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.setMargins(0, 15, 0, 0);
        textView.setPadding(10, 0, 10, 0);
        textView.setText(mPostList.get(pos).caption);
        textView.setTextSize(13);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextDark));
        textView.setTypeface(Typeface.SERIF);

        mLinearLayout.addView(textView);

        if ("video".equals(mPostList.get(pos).type)) {

            VideoView videoView = new VideoView(mContext);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));

            mLinearLayout.addView(videoView);

            MediaController controller = new MediaController(mContext);
            videoView.setVideoPath(mPostList.get(pos).URLs);
            videoView.setMediaController(controller);
            videoView.start();

        } else {

            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));

            mLinearLayout.addView(imageView);

            Glide
                    .with(mContext)
                    .load(mPostList.get(pos).URLs)
                    .centerCrop()
                    .placeholder(R.drawable.ambassadors_logo)
                    .crossFade()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .thumbnail(0.1f)
                    .into(imageView);
        }

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
*/

package xyz.mrdeveloper.sharebear;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mr. Developer on 29-04-2017.
 */

class VerticalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Activity mActivity;
    private TextView imageCount;
    private ArrayList<Post> mPostList;
    private LayoutInflater mLayoutInflater;
    private ViewPager viewPager;

    VerticalPagerAdapter(Context context, ArrayList<Post> postList, Activity activity) {
        mContext = context;
        mPostList = postList;
        mActivity = activity;
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
    public Object instantiateItem(ViewGroup container, final int pos) {
        View itemView = mLayoutInflater.inflate(R.layout.newsfeed_page, container, false);

        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);

        viewPager = (ViewPager) itemView.findViewById(R.id.viewpager);

        TextView labelView = (TextView) itemView.findViewById(R.id.textView);
        labelView.setText(mPostList.get(pos).caption);

        FullscreenVideoLayout videoView = (FullscreenVideoLayout) itemView.findViewById(R.id.videoView);
        videoView.setActivity(mActivity);

        if ("video".equals(mPostList.get(pos).type)) {
            linearLayout.removeView(viewPager);

            Uri videoUri = Uri.parse(mPostList.get(pos).URLs.get(0));

            try {
                videoView.setVideoURI(videoUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            linearLayout.removeView(videoView);

            SlideshowAdapter slideshowAdapter = new SlideshowAdapter(pos);
            viewPager.setAdapter(slideshowAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            setCurrentItem(0);
        }

        container.addView(itemView);
        return itemView;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    private void displayMetaInfo(int position, int size) {
        imageCount.setText((position + 1) + "/" + size);
    }

    private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            //displayMetaInfo(position);
            //Log.d("Check", "PageSelected + " + position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //Log.d("Check", "onPageScrolled");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //Log.d("Check", "onPageScrollStateChanged");
        }
    };

    private class SlideshowAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private int feedPosition;

        SlideshowAdapter(int feedPosition) {
            this.feedPosition = feedPosition;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.image_halfscreen_preview, container, false);

            imageCount = (TextView) view.findViewById(R.id.lbl_count);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.half_image_preview);

            imageViewPreview.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = MotionEventCompat.getActionMasked(event);

                    if (action == MotionEvent.ACTION_UP) {

                        Bundle bundle = new Bundle();

                        bundle.putString("caption", mPostList.get(feedPosition).caption);
                        bundle.putStringArrayList("imageURLs", mPostList.get(feedPosition).URLs);
                        bundle.putInt("startPosition", position);

                        FragmentManager fragmentManager = mActivity.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        SlideshowDialogFragment slideshowDialogFragment = SlideshowDialogFragment.newInstance();
                        slideshowDialogFragment.setArguments(bundle);
                        slideshowDialogFragment.show(fragmentTransaction, "slideshow");
                    }
                    return true;
                }
            });

            Glide.with(mContext)
                    .load(mPostList.get(feedPosition).URLs.get(position))
                    .thumbnail(0.5f)
                    .centerCrop()
                    .crossFade()
                    .placeholder(R.drawable.ambassadors_logo)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(imageViewPreview);


            displayMetaInfo(position, mPostList.get(feedPosition).URLs.size());
            //Log.d("Check", "PageSelected + " + position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mPostList.get(feedPosition).URLs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}