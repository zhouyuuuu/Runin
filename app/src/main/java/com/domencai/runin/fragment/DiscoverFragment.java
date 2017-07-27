package com.domencai.runin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.domencai.runin.DiscoverAdapter_Zy.DiscoverAdapter;
import com.domencai.runin.R;

/**
 * Created by lenovo on 2016/12/14.
 */

public class DiscoverFragment extends Fragment{

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_zy,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView_Discover);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new DiscoverAdapter());
        return view;
    }
}
