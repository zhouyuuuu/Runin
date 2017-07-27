package com.domencai.runin.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.domencai.runin.R;
import com.domencai.runin.bean.Badge;

import java.util.List;

/**
 * Created by lenovo on 2016/12/20.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {
    private List<Badge> badgeList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_rv);
        }
    }

    public RvAdapter(List<Badge> badgeList){
        this.badgeList = badgeList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Badge badge = badgeList.get(position);
        holder.imageView.setImageResource(badge.getBadgeId());
    }

    @Override
    public int getItemCount() {
        return badgeList.size();
    }
}
