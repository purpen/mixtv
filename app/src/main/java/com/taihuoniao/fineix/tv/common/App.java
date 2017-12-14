package com.taihuoniao.fineix.tv.common;

import android.app.Application;

import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;

/**
 * Created by Stephen on 2017/11/14 17:41
 * Email: 895745843@qq.com
 */

public class App extends Application {
    private static App mApp;
    public static String screenOrientation = CommonConstants.SCREENORIENTATION_LANDSCAPE;
    public static int pageDisplayColumns = CommonConstants.SCREENORIENTATION_LANDSCAPE_COLUMNS;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initScreenOrientation();
    }

    public static App getContext(){
        return mApp;
    }

    public static String getStringById(int id){
        return mApp.getResources().getString(id);
    }

    public static String[] getStringArray(int id){
        return mApp.getResources().getStringArray(id);
    }

    public static int getStatusBarHeight() {
        int resourceId = mApp.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId <= 0) ? 0 : mApp.getResources().getDimensionPixelSize(resourceId);
    }

    public int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 初始化屏幕方向（横屏/竖屏）
     */
    private void initScreenOrientation() {
        screenOrientation = SPUtil.read(CommonConstants.SCREENORIENTATION);
        if (CommonConstants.SCREENORIENTATION_LANDSCAPE.equals(screenOrientation)) {
            pageDisplayColumns = CommonConstants.SCREENORIENTATION_LANDSCAPE_COLUMNS;
        } else if (CommonConstants.SCREENORIENTATION_PORTRAIT.equals(screenOrientation)) {
            pageDisplayColumns = CommonConstants.SCREENORIENTATION_PORTRAIT_COLUMNS;
        } else {
            pageDisplayColumns = CommonConstants.SCREENORIENTATION_LANDSCAPE_COLUMNS;
            screenOrientation = CommonConstants.SCREENORIENTATION_LANDSCAPE;
        }
        LogUtil.e("---------------> ScreenOrientation: " + screenOrientation);
    }
}
