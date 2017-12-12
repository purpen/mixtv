package com.taihuoniao.fineix.tv.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.taihuoniao.fineix.tv.utils.LogUtil;

import java.io.IOException;

/**
 * Created by Stephen on 2017/1/9 15:00
 * Email: 895745843@qq.com
 */

public class NetWorkHandler extends Handler {
    public static final int CALLBACK_SUCCESS = 200;
    public static final int CALLBACK_FAILURE = 400;
    private HttpRequestCallback callback;

    public NetWorkHandler(HttpRequestCallback callback) {
        super(Looper.getMainLooper());
        this.callback = callback;
    }

    public NetWorkHandler(Context context, HttpRequestCallback callback) {
        super(Looper.getMainLooper());
        this.callback = callback;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CALLBACK_SUCCESS:
                if (msg.obj instanceof String){
                    callback.onSuccess((String) msg.obj);
                    LogUtil.e(msg.obj.toString());
                }else if (msg.obj instanceof Bitmap){
                    callback.onSuccess((Bitmap) msg.obj);
                }

                break;
            case CALLBACK_FAILURE:
                callback.onFailure((IOException) msg.obj);
                LogUtil.e("------Error : " + msg.obj.toString());
                break;
        }
    }
}
