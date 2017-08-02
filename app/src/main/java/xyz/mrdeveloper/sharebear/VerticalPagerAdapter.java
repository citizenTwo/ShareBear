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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static xyz.mrdeveloper.sharebear.MainActivity.verticalPagerAdapter;

/**
 * Created by Mr. Developer on 29-04-2017.
 */

class VerticalPagerAdapter extends PagerAdapter {

    private ArrayList<Post> mPostList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    static int position;
    private int totalPhotos;
    private GraphResponse mPreviousResponse;

    VerticalPagerAdapter(Context context, ArrayList<Post> postList, GraphResponse previousResponse) {
        mPostList = postList;
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPreviousResponse = previousResponse;
        totalPhotos = postList.size();
        Log.d("Check", "Total Photos : " + totalPhotos);
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

        position = pos;
        Log.d("Check", "Position: " + Integer.toString(position));

        if (position == totalPhotos - 5) {
            Log.d("Check", "User wanna read more!");

            GraphRequest nextResultsRequests = mPreviousResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
            if (nextResultsRequests != null) {
                nextResultsRequests.setCallback(new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        //your code
                        ParseTheShitOut(response);
                        //save the last GraphResponse you received
                        mPreviousResponse = response;
                    }
                });
                nextResultsRequests.executeAsync();
            }
        }

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

        container.addView(itemView);
        return itemView;
    }

    private void ParseTheShitOut(GraphResponse response) {
        try {
            JSONObject JSONObjectGraphResponse = new JSONObject(String.valueOf(response.getJSONObject()));
            JSONArray JSONArrayGraphResponse = JSONObjectGraphResponse.getJSONArray("data");

            for (int i = 0; i < JSONArrayGraphResponse.length(); i++) {
                JSONObject postData = JSONArrayGraphResponse.getJSONObject(i);

                //get your values
                if ("photo".equals(postData.getString("type")) && postData.has("message") && postData.has("full_picture") && postData.has("id")) {
                    String id = postData.getString("id");
                    String postId = id.substring(id.lastIndexOf('_') + 1);

                    Post post = new Post(postData.getString("message"), postData.getString("full_picture"), postId);
                    mPostList.add(post);
                    verticalPagerAdapter.notifyDataSetChanged();
                    totalPhotos++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
