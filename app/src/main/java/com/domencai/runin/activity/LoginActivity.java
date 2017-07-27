package com.domencai.runin.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.domencai.runin.R;
import com.domencai.runin.bean.Account;
import com.domencai.runin.bean.User;
import com.domencai.runin.custom.ResizeRelativeLayout;
import com.domencai.runin.utils.DbHelper;
import com.domencai.runin.utils.HttpUtil;
import com.domencai.runin.utils.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.domencai.runin.activity.LoadingActivity.globalAccount;
import static com.domencai.runin.activity.LoadingActivity.globalUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,ResizeRelativeLayout.KeyBoardStateListener{

    private String phone_number,captcha;
    private TextView title;
    private Button btn_login,btn_dynamicCode;
    private RelativeLayout rl_sv;
    private EditText et_account,et_password;
    private int screenWidth,screenHeight;
    private ResizeRelativeLayout mRoot;
    private RelativeLayout.LayoutParams params;
    private final int DYNAMIC_CODE = 2;
    private final int DYNAMIC_CODE_FAILED = 3;
    private ProgressBar progressBar;
    private Timer timer;
    private int second = 59;
    private Boolean timerStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRoot = (ResizeRelativeLayout) findViewById(R.id.root);
        mRoot.setKeyBoardStateListener(this);
        mRoot.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_dynamicCode = (Button) findViewById(R.id.dynamicCode);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        title = (TextView) findViewById(R.id.title);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        rl_sv = (RelativeLayout) findViewById(R.id.rl_sv);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        params = (RelativeLayout.LayoutParams) title.getLayoutParams();
        params.setMargins(0,screenHeight/10,0,0);
        btn_login.setOnClickListener(this);
        btn_dynamicCode.setOnClickListener(this);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(2*screenWidth/3,screenHeight/3,0,0);
//        progressBar.setLayoutParams(lp);
    }

    @Override
    public void onStateChange(int state) {
        switch (state) {
            case ResizeRelativeLayout.HIDE:
                title.setVisibility(View.VISIBLE);
                params.setMargins(0,screenHeight/10,0,0);
                break;
            case ResizeRelativeLayout.SHOW:
                title.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dynamicCode:
                phone_number = et_account.getText().toString();
                if (phone_number.equals("")){
                    Toast.makeText(this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    String uri = "http://api.runin.everfun.me/sms/captcha/send";
                    RequestBody requestBody = new FormBody.Builder().add("phone_number",phone_number).build();
                    Log.d("LoginActivity",uri+requestBody.toString());
                    HttpUtil.sendOkHttpRequest(uri,requestBody,"",new okhttp3.Callback(){

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            boolean result = JsonUtil.handleDynamicCodeResponse(responseData);
                            Log.d("LoginActivity","response"+response);
                            Log.d("LoginActivity","responseData info:"+responseData);
//                            SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
//                            SimpleDateFormat toSecond = new SimpleDateFormat("ss",Locale.CHINA);
//                            String preDateString = toDate.format(time);
//                            String preSecondString = toSecond.format(time);
//                            Date date = new Date();
//                            String dateString = toDate.format(date);
//                            String secondString = toSecond.format(date);
//                            dValue = Integer.parseInt(secondString)-Integer.parseInt(preSecondString);
//                            Log.d("LoginActivity",String.valueOf(dValue));
                            if (result){
                                if (!timerStart){
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            showResponse(DYNAMIC_CODE);
                                        }
                                    },0,1000);
                                    timerStart = true;
                                }
                            }else {
                                showResponse(DYNAMIC_CODE_FAILED);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            showResponse(6);
                        }
                    });
                }
                break;
            case R.id.btn_login:
                phone_number = et_account.getText().toString();
                captcha = et_password.getText().toString();
                if (phone_number.equals("")){
                    Toast.makeText(this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(captcha.equals("")){
                    Toast.makeText(this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    showResponse(5);
                    String uri = "http://api.runin.everfun.me/account/login/phone";
                    RequestBody requestBody = new FormBody.Builder()
                            .add("phone_number", phone_number)
                            .add("captcha", captcha)
                            .build();
                    Log.d("LoginActivity",uri+requestBody.toString());
                    HttpUtil.sendOkHttpRequest(uri,requestBody,"",new okhttp3.Callback(){

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            globalAccount = JsonUtil.handleLoginResponse(responseData);
                            if (globalAccount != null) {
                                List<Account> dbAccountList = DataSupport.where("user_id=?", globalAccount.getUser_id()).find(Account.class);
                                if (dbAccountList.size()==0) {
                                    globalAccount.save();
                                } else {
                                    Account dbAccount = dbAccountList.get(0);
                                    if (globalAccount != dbAccount) {
                                        Log.d("LoginActivity", "account != dbAccount");
                                        dbAccount.setToken_expire_time(globalAccount.getToken_expire_time());
                                        dbAccount.setNo_password(globalAccount.getNo_password());
                                        ContentValues values = new ContentValues();
                                        values.put("token_expire_time", globalAccount.getToken_expire_time());
                                        values.put("no_password", globalAccount.getNo_password());
                                        DataSupport.update(Account.class, values, dbAccount.getId());
                                    } else {
                                        Log.d("LoginActivity", "account == dbAccount");
                                    }
                                }
                            }
                            Log.d("MainActivity", "response" + response);
                            Log.d("LoginActivity", "responseData info:" + responseData);
                            if (globalAccount != null) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                startGetUserInfo();
                                showResponse(1);
                            } else {
                                showResponse(0);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            showResponse(6);
                        }
                    });
                }
                break;
            case R.id.root:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                break;
            default:
                break;
        }
    }

//    private Bitmap getUserAvatar() {
//        String avatarURI = "http://user.static.runin.everfun.me/avatar/"+user.getAvatar();
//        user_avatar = BitmapUtil.loadImage(avatarURI);
//        return user_avatar;
//    }

    private void startGetUserInfo() {
        String uri = "http://api.runin.everfun.me/user/profile/get_my_info";
        RequestBody requestBody = new FormBody.Builder().build();
        Log.d("LoginActivity", uri + requestBody.toString());
        HttpUtil.sendOkHttpRequest(uri, requestBody, globalAccount.getToken(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showResponse(4);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("LoginActivity", "response info:" + response);
                Log.d("LoginActivity", "responseData info:" + responseData);
                globalUser = JsonUtil.handleUserInfoResponse(responseData);
                SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);
                Date date = new Date();
                String dataStr = toDate.format(date);
                if (globalUser != null) {
                    Log.d("LoginActivity","globalUser.getUser_id():"+globalUser.getUser_id());
                    List<User> dbUserList = DataSupport.where("user_id=?", globalUser.getUser_id()).find(User.class);
                    Log.d("LoginActivity","dbUserList.size():"+dbUserList.size());
                    if (dbUserList.size()==0) {
                        globalUser.setSync_at(dataStr);
                        globalUser.save();
                    } else {
                        User dbUser = dbUserList.get(0);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);
                        String today = sdf.format(new Date());//第一个时间
                        today = today + " 04:00:00 000";
                        Log.d("LoginActivity","today="+today);
                        Date sync_date = null;
                        Date today_date = null;
                        try {
                            Log.d("LoginActivity","sync_at:"+dbUser.getSync_at());
                            sync_date = sdf1.parse(dbUser.getSync_at());
                            today_date = sdf1.parse(today);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        double diffVal = 0;
                        if (sync_date != null && today_date != null) {
                            Log.d("LoginActivity","sync_date.getTime():"+sync_date.getTime());
                            Log.d("LoginActivity","today_date.getTime():"+today_date.getTime());
                            diffVal = ((sync_date.getTime() - today_date.getTime() * 0.01 * 100) / (1000 * 60 * 60 * 24));

                        }
                        Log.d("LoginActivity", "diffVal=" + diffVal);
                        if (diffVal <= 0) {
                            Log.d("LoginActivity", "diffVal <= 0");
                            DbHelper.updateUser(globalUser);
                        } else {
                            Log.d("LoginActivity", "diffVal>0");
                        }
                    }
                }
            }
        });
    }

    private void showResponse(final int result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (result){
                    case 0:
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                        btn_dynamicCode.setText("获取验证码");
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_login.setText("登  录");
                        if(timer!=null){
                            timer.cancel();
                            second = 59;
                            timerStart = false;
                        }
                        break;
                    case 1:
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        if(timer!=null){
                            timer.cancel();
                            second = 59;
                            timerStart = false;
                        }
                        break;
                    case 2:
                        if (second>=0){
                            btn_dynamicCode.setText("还剩"+second+"秒");
                            second--;
                        }else{
                            btn_dynamicCode.setText("获取验证码");
                            if(timer!=null){
                                timer.cancel();
                                second = 59;
                                timerStart = false;
                            }
                        }
                        break;
                    case 3:
                        Toast.makeText(LoginActivity.this,"获取动态码失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(LoginActivity.this,"获取用户信息失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        btn_login.setText("登录中...");
                        progressBar.setVisibility(View.VISIBLE);
                        if(timer!=null){
                            timer.cancel();
                            second = 59;
                            timerStart = false;
                        }
                        break;
                    case 6:
                        btn_dynamicCode.setText("获取验证码");
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_login.setText("登  录");
                        if(timer!=null){
                            timer.cancel();
                            second = 59;
                            timerStart = false;
                        }
                        Toast.makeText(LoginActivity.this,"网络异常请检查网络",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}

