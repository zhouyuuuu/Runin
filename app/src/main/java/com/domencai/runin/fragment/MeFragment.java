package com.domencai.runin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.domencai.runin.R;
import com.domencai.runin.activity.ClipHeaderActivity;
import com.domencai.runin.activity.RankActivity_Zy;
import com.domencai.runin.activity.RvAdapter;
import com.domencai.runin.activity.SettingActivity;
import com.domencai.runin.bean.Badge;
import com.domencai.runin.utils.BitmapUtil;
import com.domencai.runin.utils.DbHelper;
import com.domencai.runin.utils.HttpUtil;
import com.domencai.runin.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.domencai.runin.activity.LoadingActivity.globalAccount;
import static com.domencai.runin.activity.LoadingActivity.globalUser;

/**
 * Created by lenovo on 2016/12/14.
 */

public class MeFragment extends Fragment implements View.OnClickListener{

    private static final int RESULT_CAPTURE = 100;
    private static final int RESULT_PICK = 101;
    private static final int CROP_PHOTO = 102;
    private File tempFile,cropFile;
    public static LruCache<String, Bitmap> mLruCache;
    LinearLayout ll_user;
    ImageView iv_head,iv_sex,iv_head_background;
    TextView tv_username,tv_sign,tv_level,tv_exp;
    List<Badge> badgeList = new ArrayList<>();
    String newOne;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me,container,false);
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        iv_head = (ImageView) view.findViewById(R.id.iv_head);
        iv_head_background = (ImageView) view.findViewById(R.id.iv_head_background);
        iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
        tv_level = (TextView) view.findViewById(R.id.tv_level);
        tv_exp = (TextView) view.findViewById(R.id.tv_exp);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_username.setOnClickListener(this);
        tv_sign.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        ll_user.post(new Runnable() {
            @Override
            public void run() {
                int height = ll_user.getHeight();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(2*height/3,2*height/3,0,0);
                iv_sex.setLayoutParams(lp);
            }
        });
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取当前应用程序所分配的最大内存
        int cacheSize = maxMemory / 5;//只分5分之一用来做图片缓存
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {//复写sizeof()方法
                // replaced by getByteCount() in API 12
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024; //这里是按多少KB来算
            }
        };
        initUser();
        initBadgeList();
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new RvAdapter(badgeList));
        initData(savedInstanceState);

        view.findViewById(R.id.rank_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RankActivity_Zy.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        }else{
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath()+"/avatar/image/"),
                    System.currentTimeMillis() + ".png");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }

    private void initUser() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (globalUser!=null){
                    if (globalUser.getNickname()!=null){
                        tv_username.setText(globalUser.getNickname());
                    }else {
                        tv_username.setText("点击设置用户名");
                    }
                    tv_exp.setText(String.valueOf(globalUser.getExp()));
                    tv_level.setText(String.valueOf(globalUser.getLevel()));
                    if (globalUser.getIntro()!=null){
                        tv_sign.setText("个性签名："+globalUser.getIntro());
                    }
                    switch (globalUser.getGender()){
                        case 0:
                            iv_sex.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            iv_sex.setImageResource(R.drawable.ic_male);
                            break;
                        case 2:
                            iv_sex.setImageResource(R.drawable.ic_female);
                            break;
                        default:
                            break;
                    }
                    if (globalUser.getAvatar().equals("default.png")){
                        iv_head_background.setVisibility(View.INVISIBLE);
                        Log.d("MeFragment","avatar==default");
                    }else {
                        setUserAvatar();
                    }
                }
            }
        });
    }

    private void setUserAvatar() {
        String avatarURI = "http://user.static.runin.everfun.me/avatar/"+globalUser.getAvatar();
        BitmapUtil.loadLruBitmap(avatarURI, globalAccount.getToken(),iv_head);
    }

    private void initBadgeList() {
        Badge badge1 = new Badge();
        badge1.setBadgeId(R.drawable.medal_1st_race_black);
        badgeList.add(badge1);
        Badge badge2 = new Badge();
        badge2.setBadgeId(R.drawable.medal_1st_run_black);
        badgeList.add(badge2);
        Badge badge3 = new Badge();
        badge3.setBadgeId(R.drawable.medal_champion_black);
        badgeList.add(badge3);
        Badge badge4 = new Badge();
        badge4.setBadgeId(R.drawable.medal_once_run10k_black);
        badgeList.add(badge4);
        Badge badge5 = new Badge();
        badge5.setBadgeId(R.drawable.medal_once_run20k_black);
        badgeList.add(badge5);
        Badge badge6 = new Badge();
        badge6.setBadgeId(R.drawable.medal_once_run20k_black);
        badgeList.add(badge6);
        Badge badge7 = new Badge();
        badge7.setBadgeId(R.drawable.medal_once_run30k_black);
        badgeList.add(badge7);
        Badge badge8 = new Badge();
        badge8.setBadgeId(R.drawable.medal_race_count2_black);
        badgeList.add(badge8);
        Badge badge9 = new Badge();
        badge9.setBadgeId(R.drawable.medal_race_count3_black);
        badgeList.add(badge9);
        Badge badge10 = new Badge();
        badge10.setBadgeId(R.drawable.medal_race_count8_black);
        badgeList.add(badge10);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        switch (view.getId()){
            case R.id.tv_username:
                intent.putExtra("data",false);
                startActivityForResult(intent,1);
                break;
            case R.id.tv_sign:
                intent.putExtra("data",true);
                startActivityForResult(intent,2);
                break;
            case R.id.iv_head:
                showChooseDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    newOne = data.getStringExtra("data_return");
                    globalUser.setNickname(newOne);
                    changeUI(1);
                }
                break;
            case 2:
                if (resultCode==RESULT_OK){
                    newOne = data.getStringExtra("data_return");
                    globalUser.setIntro(newOne);
                    changeUI(2);
                }
                break;
            case RESULT_CAPTURE:
                if (resultCode == RESULT_OK) {
                    startCropPhoto(Uri.fromFile(tempFile));
                }
                break;
            case RESULT_PICK:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    startCropPhoto(uri);
                }

                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        setPicToView(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void changeUI(final int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (i){
                    case 1:
                        tv_username.setText(globalUser.getNickname());
                        break;
                    case 2:
                        tv_sign.setText("个性签名:"+globalUser.getIntro());
                        break;
                    case 3:
                        Toast.makeText(getContext(),"头像上传失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(getContext(),"头像上传成功",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showChooseDialog() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if(ContextCompat.checkSelfPermission(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }else {
                                camera();
                            }
                        } else {
                            if(ContextCompat.checkSelfPermission(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                            }else {
                                album();
                            }
                        }
                    }
                }).show();
    }

    private void album() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), RESULT_PICK);
    }

    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", tempFile));
        startActivityForResult(intent, RESULT_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    camera();
                }else {
                    Log.d("UserInfoActivity","permission deny");
                }
                break;
            case 2:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    album();
                }else {
                    Log.d("UserInfoActivity","permission deny");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开截图界面
     * @param uri 原图的Uri
     */
    public void startCropPhoto(Uri uri) {

        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClipHeaderActivity.class);
        intent.setData(uri);
        intent.putExtra("side_length", 200);//裁剪图片宽高
        startActivityForResult(intent, CROP_PHOTO);

        //调用系统的裁剪
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", crop);
//        intent.putExtra("outputY", crop);
//        intent.putExtra("return-data", true);
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, CROP_PHOTO);
    }

    private void setPicToView(Intent picData) {
        Uri uri = picData.getData();
//        Bitmap bitmap = null;
        if (uri == null) {
            return;
        }
        cropFile = new File(uri.getPath());
        //这里处理图片保存和上传！
        iv_head.setImageURI(uri);
        iv_head_background.setVisibility(View.VISIBLE);
        final String addressURI = "http://api.runin.everfun.me/user/profile/set_avatar";
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data;name =\"avatar\"; filename=\"myAvatar.png"),
                        RequestBody.create(MediaType.parse("image/png"),cropFile)).build();
        Log.d("MeFragment","uri:"+uri);
        Log.d("MeFragment","addressURI"+addressURI);
        Log.d("MeFragment","requestBody"+requestBody.toString());
        Log.d("MeFragment","token:"+ globalAccount.getToken());
        HttpUtil.sendOkHttpRequest(addressURI, requestBody, globalAccount.getToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                changeUI(3);
                Log.d("MeFragment", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("MeFragment","response"+response);
                Log.d("MeFragment","responseData info:"+responseData);
                String avatar_id = JsonUtil.handleAvatarResponse(responseData);
                String avatarURL = "http://user.static.runin.everfun.me/avatar/"+avatar_id;
                Log.d("MeFragment","avatar_id:"+avatar_id);
                if (avatar_id!=null){
                    globalUser.setAvatar(avatar_id);
                    int i = DbHelper.updateAvatar(avatar_id);
                    if (i==1){
                        Log.d("MeFragment","头像数据库更新成功");
                    }else {
                        Log.d("MeFragment","头像数据库更新成功");
                    }
                    changeUI(4);
                    BitmapUtil.loadLruBitmap(avatarURL, globalAccount.getToken(),null);
                }else {
                    changeUI(3);
                }
            }
        });
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     *
     * @param dirPath
     * @return dirPath
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}
