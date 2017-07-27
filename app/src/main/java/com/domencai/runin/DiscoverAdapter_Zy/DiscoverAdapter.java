package com.domencai.runin.DiscoverAdapter_Zy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.domencai.runin.R;

/**
 * Created by Lenovo on 2017/7/27.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverHolder>{
    @Override
    public DiscoverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_discover_item_zy,parent,false);
        return new DiscoverHolder(v);
    }

    @Override
    public void onBindViewHolder(DiscoverHolder holder, int position) {
        holder.imageView.setImageResource(R.drawable.medal_champion);
        holder.title_tv.setText("广州大学城内环");
        holder.length_tv.setText("3.3公里");
        holder.distance_tv.setText("最近距离 250米");
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class DiscoverHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView title_tv;
        private TextView length_tv;
        private TextView distance_tv;

        public DiscoverHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title_tv = (TextView) itemView.findViewById(R.id.title_tv);
            length_tv = (TextView) itemView.findViewById(R.id.length_tv);
            distance_tv = (TextView) itemView.findViewById(R.id.distance_tv);
        }

    }
}
