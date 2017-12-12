package com.taihuoniao.fineix.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.taihuoniao.fineix.tv.utils.LogUtil;

/**
 * Created by Stephen on 12/06/2017.
 * Emial: 895745843@qq.com
 */
public class ScreenSaverBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent i = new Intent();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(context, MainActivity.class);
            context.startActivity(i);
            LogUtil.e("-------------> 监听屏保成功");
            Toast.makeText(context, "屏幕监听广播", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LogUtil.i("Output:", e.toString());
        }
    }
}
