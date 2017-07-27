package com.domencai.runin.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片异步加载类，有图片内存缓存
 */
public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap> {

    private ImageView image;
    private static LruCache<String, Bitmap> lruCache;
    /**
     * 构造方法，需要把ImageView控件和LruCache 对象传进来
     * @param image 加载图片到此 {@code}ImageView
     * @param lruCache 缓存图片的对象
     */
    public AsyncImageLoader(ImageView image, LruCache<String, Bitmap> lruCache) {
        super();
        this.image = image;
        AsyncImageLoader.lruCache = lruCache;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Log.d("BitmapUtil","doInBackground");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtil.loadImage(params[0],params[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("BitmapUtil","params[0]="+params[0]);
        Log.d("BitmapUtil","params[1]="+params[1]);
        addBitmapToMemoryCache(params[0], bitmap);
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (image!=null){
            image.setImageBitmap(bitmap);
        }
        Log.d("BitmapUtil","onPostExecute");
    }
    //调用LruCache的put 方法将图片加入内存缓存中，要给这个图片一个key 方便下次从缓存中取出来
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Log.d("BitmapUtil","addBitmapToMemoryCache");
        Log.d("BitmapUtil","key="+key);
        if (getBitmapFromMemoryCache(key) == null) {
            lruCache.put(key, bitmap);
        }
    }
    //调用LruCache的get 方法从内存缓存中去图片
    public Bitmap getBitmapFromMemoryCache(String key) {
        Log.d("BitmapUtil","getBitmapFromMemoryCache");
        return lruCache.get(key);
    }
}
