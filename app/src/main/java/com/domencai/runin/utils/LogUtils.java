package com.domencai.runin.utils;

import android.support.annotation.NonNull;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Domen、on 2016/11/28.
 */

public class LogUtils {
    private static final String TAG = "LogUtils";
    private static final String FILE_PREFIX = "KLog_";
    private static final String FILE_FORMAT = ".log";
    private final static String LOG_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/RunIn";


    public static void printFile(String msg) {
        if (save(getFileName(), msg)) {
            Log.d(TAG, "save log success ! ");
        } else {
            Log.e(TAG, "save log fails !");
        }
    }

    private static boolean save(@NonNull String fileName, String msg) {

        File dir = new File(LOG_PATH);
        if (dir.mkdirs()) {
            Log.w("LogUtils", "save: fails");
        }
        File file = new File(LOG_PATH, fileName);

        try {
            file.createNewFile();
            Log.w("LogUtils", "save: " + msg);
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStreamWriter.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private static String getFileName() {
        String now = new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date());
        return FILE_PREFIX + now + FILE_FORMAT;
    }

    public static void printJson(String msg) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        String[] lines = message.split(System.getProperty("line.separator"));
        for (String line : lines) {
            Log.w(TAG, "║ " + line);
        }
    }
}
