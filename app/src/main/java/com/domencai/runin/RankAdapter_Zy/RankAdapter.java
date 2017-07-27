package com.domencai.runin.RankAdapter_Zy;

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

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankHolder>{
    @Override
    public RankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_rank_item_zy,parent,false);
        return new RankHolder(v);
    }

    @Override
    public void onBindViewHolder(RankAdapter.RankHolder holder, int position) {
        holder.imageView.setImageResource(R.drawable.medal_champion);
        holder.nickname_tv.setText("我叫王小明");
        holder.time_tv.setText("19.32分钟");
        holder.distance_tv.setText("2.32分钟/公里");
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class RankHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView nickname_tv;
        private TextView time_tv;
        private TextView distance_tv;

        public RankHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nickname_tv = (TextView) itemView.findViewById(R.id.nickname_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            distance_tv = (TextView) itemView.findViewById(R.id.speed_tv);
        }

    }
}
