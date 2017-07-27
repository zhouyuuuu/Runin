package com.domencai.runin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.domencai.runin.R;
import com.domencai.runin.utils.DbHelper;
import com.domencai.runin.utils.HttpUtil;
import com.domencai.runin.utils.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.domencai.runin.activity.LoadingActivity.globalAccount;
import static com.domencai.runin.activity.LoadingActivity.globalUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_rename,et_resign;
    private TextView title,tv_show_sign;
    private String newOne;
    private String result;
    private Boolean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent = getIntent();
        data = intent.getBooleanExtra("data",false);
        Button btn_back = (Button) findViewById(R.id.btn_back_setting);
        Button btn_sure = (Button) findViewById(R.id.btn_sure_setting);
        title = (TextView) findViewById(R.id.tv_title_setting);
        tv_show_sign = (TextView) findViewById(R.id.tv_show_sign);
        et_rename = (EditText) findViewById(R.id.et_rename);
        et_resign = (EditText) findViewById(R.id.et_resign);
        if (data){
            changeUI(0);
        }
        btn_back.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    private void changeUI(final int i) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (i){
                    case 0:
                        title.setText("编辑个性签名");
                        et_rename.setVisibility(View.INVISIBLE);
                        et_resign.setVisibility(View.VISIBLE);
                        if (globalUser.getIntro()!=null){
                            tv_show_sign.setVisibility(View.VISIBLE);
                            tv_show_sign.setText("个性签名："+globalUser.getIntro());
                        }
                        break;
                    case 1:
                        Toast.makeText(SettingActivity.this,"无任何修改",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SettingActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(SettingActivity.this,"修改失败,请检查网络",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(SettingActivity.this,"个人简介不能太长",Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(SettingActivity.this,"昵称已使用",Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(SettingActivity.this,result,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_setting:
                finish();
                break;
            case R.id.btn_sure_setting:
                if (data){
                    newOne = et_resign.getText().toString();
                }else{
                    newOne = et_rename.getText().toString();
                }
                if (newOne.equals("")) {
                    changeUI(1);
                } else if(newOne.equals(globalUser.getNickname())){
                    changeUI(5);
                } else{
                    result = connectToTheBackEnd();
                    while(result==null){}
                    if (result.equals("ok")){
                        Intent intent = new Intent();
                        intent.putExtra("data_return", newOne);
                        setResult(RESULT_OK, intent);
                        finish();
                        changeUI(2);
                    }else if (result.equals("个人简介不能太长")){
                        changeUI(4);
                        result=null;
                    }else {
                        changeUI(6);
                        result=null;
                    }
                }
                break;
            default:
                break;
        }
    }

    private String connectToTheBackEnd() {
        if (data){
            String uri = "http://api.runin.everfun.me/user/profile/set_user_info";
            RequestBody requestBody = new FormBody.Builder()
                    .add("intro", newOne).build();
            Log.d("SettingActivity", uri + requestBody.toString());
            HttpUtil.sendOkHttpRequest(uri, requestBody, globalAccount.getToken(), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("SettingActivity", "onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("LoginActivity", "response info:" + response);
                    Log.d("LoginActivity", "responseData info:" + responseData);
                    result = JsonUtil.handleNoReturnResponse(responseData);
                    if (result.equals("ok")){
                        int i = DbHelper.updateIntro(newOne);
                        if (i==1){
                            Log.d("SettingActivity","个性签名数据库更新成功");
                        }else {
                            Log.d("SettingActivity","个性签名数据库更新失败");
                        }
                    }else {
                        Log.d("SettingActivity",result);
                    }
                }
            });
        }else {
            String uri = "http://api.runin.everfun.me/user/profile/set_nickname";
            RequestBody requestBody = new FormBody.Builder()
                    .add("nickname", newOne).build();
            Log.d("SettingActivity", uri + requestBody.toString());
            HttpUtil.sendOkHttpRequest(uri, requestBody, globalAccount.getToken(), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("SettingActivity", "onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("LoginActivity", "response info:" + response);
                    Log.d("LoginActivity", "responseData info:" + responseData);
                    result = JsonUtil.handleNoReturnResponse(responseData);
                    if (result.equals("ok")){
                        int i = DbHelper.updateNickName(newOne);
                        if (i==1){
                            Log.d("SettingActivity","用户名数据库更新成功");
                        }else {
                            Log.d("SettingActivity","用户名数据库更新失败");
                        }
                    }
                }
            });
        }
        return result;
    }
}
