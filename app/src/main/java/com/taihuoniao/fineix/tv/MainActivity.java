
package com.taihuoniao.fineix.tv;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.LoginInfoBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.AutoLoginAPI;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends BaseActivity {
    private long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((userId = LoginInfoBean.getUserId()) < 0) {
//            startActivity(new Intent(MainActivity.this, ActivityLogin.class));
//            this.finish();
//            return;
            // 请求接口，用默认帐号
            AutoLoginAPI.requeLoginApi(CommonConstants.TEST_ACCOUNT, CommonConstants.TEST_SECRETKEY);
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String message = newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE ? "屏幕设置为：横屏" : "屏幕设置为：竖屏";
        ToastUtil.showSuccess(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_LANDSCAPE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
