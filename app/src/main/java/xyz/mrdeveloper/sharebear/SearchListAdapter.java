package xyz.mrdeveloper.sharebear;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Lakshay Raj on 07-08-2017.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    Context context;
    ArrayList<Post> postList;
    ArrayList<Post> diaplayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView caption;

        public ViewHolder(View itemView) {
            super(itemView);
            caption = (TextView) itemView.findViewById(R.id.search_caption);
        }
    }

    public SearchListAdapter(Context context, ArrayList<Post> postList){
        this.context = context;
        this.postList = postList;
        diaplayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlist_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.caption.setText(diaplayList.get(position).caption);
    }

    public void filter(String query){
        query = query.toLowerCase(Locale.getDefault());
        diaplayList.clear();

        if(query.length() != 0){
            for (Post post: postList){
                if(post.caption.toLowerCase(Locale.getDefault()).contains(query)){
                    diaplayList.add(post);
                    Log.d("Query", "Post Added");
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return diaplayList.size();
    }
}
