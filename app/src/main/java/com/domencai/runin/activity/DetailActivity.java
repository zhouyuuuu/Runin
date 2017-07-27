package com.domencai.runin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.domencai.runin.custom.ChartView;

/**
 * Created by Domen、on 2016/11/22.
 */

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new ChartView(this);
        view.setBackgroundColor(0xff888888);
        setContentView(view);
    }
}
