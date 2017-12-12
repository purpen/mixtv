package com.taihuoniao.fineix.tv.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.utils.LogUtil;

/**
 * Created by Stephen on 2017/12/5 20:27
 * Email: 895745843@qq.com
 */

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.e("----------------------> onCreate() " + getClass().getSimpleName());
        super.onCreate(savedInstanceState);

        // 判断横竖屏操作
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_LANDSCAPE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
