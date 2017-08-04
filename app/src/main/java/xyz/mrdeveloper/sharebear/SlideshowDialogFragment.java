package xyz.mrdeveloper.sharebear;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by Vaibhav on 03-08-2017.
 */

public class SlideshowDialogFragment extends DialogFragment {

    private ArrayList<String> imagesURLs;
    private ViewPager viewPager;
    private TextView imageCount, imageTitle;
    private int selectedPosition = 0;
    private String caption;

    static SlideshowDialogFragment newInstance() {
        return new SlideshowDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        imageCount = (TextView) v.findViewById(R.id.lbl_count);
        imageTitle = (TextView) v.findViewById(R.id.title);

        imagesURLs = new ArrayList<>();
        imagesURLs = getArguments().getStringArrayList("imageURLs");
        caption = getArguments().getString("caption");

        SlideshowAdapter slideshowAdapter = new SlideshowAdapter();
        viewPager.setAdapter(slideshowAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
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

    private void displayMetaInfo(int position) {
        imageCount.setText((position + 1) + " of " + imagesURLs.size());

        imageTitle.setText(caption);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //    //  adapter
    private class SlideshowAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        SlideshowAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

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

            Glide.with(getActivity())
                    .load(imagesURLs.get(position))
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
            return imagesURLs.size();
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
}
