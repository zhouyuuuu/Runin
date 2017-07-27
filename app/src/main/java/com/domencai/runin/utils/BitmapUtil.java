package com.domencai.runin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.domencai.runin.fragment.MeFragment;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 位图工具类
 * 
 * @author jst
 */
public class BitmapUtil {


	/**
	 * 图片等比例压缩
	 *
	 * @param filePath
	 * @param reqWidth 期望的宽
	 * @param reqHeight 期望的高
	 * @return
	 */
	public static Bitmap decodeSampledBitmap(String filePath, int reqWidth,
											 int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 计算InSampleSize
	 * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
	 * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			int ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
			// inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
			if (ratio < 3)
				inSampleSize =  ratio;
			else if (ratio < 6.5)
				inSampleSize = 4;
			else if (ratio < 8)
				inSampleSize = 8;
			else
				inSampleSize =  ratio;
		}

		return inSampleSize;
	}

	/**
	 * 图片缩放到指定宽高
	 * 
	 * 非等比例压缩，图片会被拉伸
	 * 
	 * @param bitmap 源位图对象
	 * @param w 要缩放的宽度
	 * @param h 要缩放的高度
	 * @return 新Bitmap对象
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, false);
		return newBmp;
	}

	/*
	 * 下载图片转为Bitmap格式
	 */
	public static Bitmap loadImage(String urlString,String token){
		Log.d("BitmapUtil","loadImage");
		Log.d("BitmapUtil","urlString="+urlString);
		Log.d("BitmapUtil","token="+token);
		final Bitmap[] bitmap = {null};
		RequestBody requestBody = new FormBody.Builder().build();
		Log.d("BitmapUtil","urlString+requestBody="+urlString+"+"+requestBody.toString());
		HttpUtil.sendOkHttpRequest(urlString, requestBody, token, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("BitmapUtil","onFailure exception"+e.toString());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("BitmapUtil","onResponse");
				InputStream is = response.body().byteStream();
				bitmap[0] = BitmapFactory.decodeStream(is);
				if (bitmap[0] !=null){
					Log.d("BitmapUtil","bitmap[0]!=null");
				}else {
					Log.d("BitmapUtil","bitmap[0]==null");
				}
			}
		});
		while (bitmap[0]==null){}
		return bitmap[0];
	}

		private static LruCache<String, Bitmap> mLruCache = MeFragment.mLruCache ;
		/**
		 * 以内存形式缓存图片，关闭程序时回收
		 * @param urlStr 所需要加载的图片的url，以String形式传进来，可以把这个url作为缓存图片的key
		 * @param image ImageView 控件
		 */
		public static void loadLruBitmap(String urlStr,String token, ImageView image) {
			AsyncImageLoader asyncLoader = new AsyncImageLoader(image, mLruCache);//什么一个异步图片加载对象
			Bitmap bitmap = asyncLoader.getBitmapFromMemoryCache(urlStr);//首先从内存缓存中获取图片
			if (bitmap != null) {
				if (image!=null){
					image.setImageBitmap(bitmap);//如果缓存中存在这张图片则直接设置给ImageView
				}
			} else {
//				image.setImageResource(R.drawable.ic_default_head);//否则先设置成默认的图片
				Log.d("BitmapUtil","loadLruBitmap urlStr="+urlStr);
				asyncLoader.execute(urlStr,token);//然后执行异步任务AsycnTask 去网上加载图片
			}
		}

}
