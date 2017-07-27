package com.domencai.runin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.domencai.runin.R;
import com.domencai.runin.RankAdapter_Zy.RankAdapter;

public class RankActivity_Zy extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_zy);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView_Rank);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new RankAdapter());
    }
}
