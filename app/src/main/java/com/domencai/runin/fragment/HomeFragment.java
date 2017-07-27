package com.domencai.runin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.domencai.runin.R;
import com.domencai.runin.activity.StartActivity;
import com.domencai.runin.utils.VoiceUtil;

import static com.domencai.runin.utils.VoiceUtil.CHINESE;


/**
 * Created by lenovo on 2016/12/14.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{

    private Button btn_startRun;
    private Button btn_challenge;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,container,false);
        btn_startRun = (Button) view.findViewById(R.id.btn_startRun);
        btn_challenge = (Button) view.findViewById(R.id.btn_challenge);
        btn_startRun.setOnClickListener(this);
        btn_challenge.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_startRun:
                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_challenge:
                VoiceUtil voiceUtil = new VoiceUtil(getActivity(),CHINESE);
                voiceUtil.speaking("开始挑战");
                break;
            default:
                break;
        }
    }
}
