package com.taihuoniao.fineix.tv.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taihuoniao.fineix.tv.MainActivity;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;
import com.taihuoniao.fineix.tv.utils.TypeConversionUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Stephen on 2017/12/19 15:47
 * Email: 895745843@qq.com
 */

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private int[] autoEvenWaitTimes = {1, 2, 3, 5, 10, 15, 20, 30};
    private int[] intervalWaitTimes = {3, 4, 5, 6, 7, 8, 9, 10, 15, 20};
    private LinearLayout linearLayoutScreenDisplay;
    private LinearLayout linearLayoutLoopSettingAutoStartWaitTime;
    private TextView editTextAutoEventWaitTime;
    private LinearLayout linearLayoutLoopSettingStartNextWaitTime;
    private TextView editTextIntervalWaitTime;
    private TextView linearLayoutLoopSettingAutoStartWaitTimeLeft;
    private TextView linearLayoutLoopSettingAutoStartWaitTimeRight;
    private TextView linearLayoutLoopSettingStartNextWaitTimeLeft;
    private TextView linearLayoutLoopSettingStartNextWaitTimeRight;
    private LinearLayout linearLayoutLogout;
    private int autoEventWaitTimeValueIndex = 3; // 默认设置为5分钟
    private int intervalWaitTimesValueIndex = 2; // 默认设置为5秒
    private WaittingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mDialog = new WaittingDialog(this);
        mDialog.show();
        initViews();
        setListener();
        readSettingInformation();
    }

    private void initViews() {
        linearLayoutScreenDisplay = (LinearLayout) findViewById(R.id.linearLayout_screen_display);
        linearLayoutLoopSettingAutoStartWaitTime = (LinearLayout) findViewById(R.id.linearLayout_loop_setting_auto_start_wait_time);
        editTextAutoEventWaitTime = (TextView) findViewById(R.id.editText_autoEvent_waitTime);
        linearLayoutLoopSettingStartNextWaitTime = (LinearLayout) findViewById(R.id.linearLayout_loop_setting_start_next_wait_time);
        editTextIntervalWaitTime = (TextView) findViewById(R.id.editText_Interval_waitTime);

        linearLayoutLoopSettingAutoStartWaitTimeLeft = (TextView) findViewById(R.id.linearLayout_loop_setting_auto_start_wait_time_left);
        linearLayoutLoopSettingAutoStartWaitTimeRight = (TextView) findViewById(R.id.linearLayout_loop_setting_auto_start_wait_time_right);
        linearLayoutLoopSettingStartNextWaitTimeLeft = (TextView) findViewById(R.id.linearLayout_loop_setting_start_next_wait_time_left);
        linearLayoutLoopSettingStartNextWaitTimeRight = (TextView) findViewById(R.id.linearLayout_loop_setting_start_next_wait_time_right);

        linearLayoutLogout = (LinearLayout) findViewById(R.id.linearLayout_logout);
    }

    @Override
    public void onBackPressed() {
        LogUtil.e(TAG, "---------onBackPressed()");
        saveSettingInformation();
    }

    /**
     * 读取设置信息
     */
    private void readSettingInformation() {
        String autoEventWaitTime = SPUtil.read(CommonConstants.AUTO_EVENT_WAIT_TIME);
        String intervalWaitTime = SPUtil.read(CommonConstants.INTERVAL_WAIT_TIME);

        LogUtil.e(TAG, " -----Setting readSettingInformation----autoEventWaitTime: " + autoEventWaitTime  + " | intervalWaitTime: " + intervalWaitTime);
        if (!TextUtils.isEmpty(autoEventWaitTime)) {
            getAutoEventWaitTimeIndex(autoEventWaitTime);
            CharSequence aautoEvenWaitTimes = getAautoEvenWaitTimes(autoEventWaitTimeValueIndex);
            editTextAutoEventWaitTime.setText(aautoEvenWaitTimes);
        } else {
            CharSequence aautoEvenWaitTimes = getAautoEvenWaitTimes(autoEventWaitTimeValueIndex);
            editTextAutoEventWaitTime.setText(aautoEvenWaitTimes);
        }
        if (!TextUtils.isEmpty(intervalWaitTime)) {
            getIntervalWaitTimeIndex(intervalWaitTime);
            CharSequence intervalWaitTimes = getIntervalWaitTimes(intervalWaitTimesValueIndex);
            editTextIntervalWaitTime.setText(intervalWaitTimes);
        } else {
            CharSequence intervalWaitTimes = getIntervalWaitTimes(intervalWaitTimesValueIndex);
            editTextIntervalWaitTime.setText(intervalWaitTimes);
        }
    }

    /**
     * 保存设置信息
     */
    private void saveSettingInformation() {
//        String aautoEvenWaitTimes = getAautoEvenWaitTimes(autoEventWaitTimeValueIndex);
//        String intervalWaitTimes = getIntervalWaitTimes(intervalWaitTimesValueIndex);
        SPUtil.write(CommonConstants.AUTO_EVENT_WAIT_TIME, String.valueOf(autoEvenWaitTimes[autoEventWaitTimeValueIndex]));
        SPUtil.write(CommonConstants.INTERVAL_WAIT_TIME, String.valueOf(intervalWaitTimes[intervalWaitTimesValueIndex]));

        String autoEventWaitTime = editTextAutoEventWaitTime.getText().toString();
        String intervalWaitTime = editTextIntervalWaitTime.getText().toString();
        LogUtil.e(TAG, "-----Setting saveSettingInformation----autoEventWaitTime: " + autoEventWaitTime  + " | intervalWaitTime: " + intervalWaitTime);
        clearAllActivityExceptCurrent(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDialog.dismiss();
        linearLayoutScreenDisplay.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (linearLayoutScreenDisplay.hasFocus()) {
                    ToastUtil.showInfo("暂不支持竖屏操作！");
                } else if(linearLayoutLoopSettingAutoStartWaitTime.hasFocus()){
                    linearLayoutLoopSettingAutoStartWaitTimeLeft.setPressed(true);
                } else if (linearLayoutLoopSettingStartNextWaitTime.hasFocus()) {
                    linearLayoutLoopSettingStartNextWaitTimeLeft.setPressed(true);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (linearLayoutScreenDisplay.hasFocus()) {
                    ToastUtil.showInfo("暂不支持竖屏操作！");
                } else if(linearLayoutLoopSettingAutoStartWaitTime.hasFocus()){
                    linearLayoutLoopSettingAutoStartWaitTimeRight.setPressed(true);
                } else if (linearLayoutLoopSettingStartNextWaitTime.hasFocus()) {
                    linearLayoutLoopSettingStartNextWaitTimeRight.setPressed(true);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (linearLayoutLogout.hasFocus()) {
                    requetLogout();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (linearLayoutScreenDisplay.hasFocus()) {
                    ToastUtil.showInfo("暂不支持竖屏操作！");
                } else if(linearLayoutLoopSettingAutoStartWaitTime.hasFocus()){
                    linearLayoutLoopSettingAutoStartWaitTimeLeft.setPressed(false);
                    if (autoEventWaitTimeValueIndex > 0) {
                        editTextAutoEventWaitTime.setText(getAautoEvenWaitTimes(--autoEventWaitTimeValueIndex));
                    }
                } else if (linearLayoutLoopSettingStartNextWaitTime.hasFocus()) {
                    linearLayoutLoopSettingStartNextWaitTimeLeft.setPressed(false);
                    if (intervalWaitTimesValueIndex > 0) {
                        editTextIntervalWaitTime.setText(getIntervalWaitTimes(--intervalWaitTimesValueIndex));
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(linearLayoutLoopSettingAutoStartWaitTime.hasFocus()){
                    linearLayoutLoopSettingAutoStartWaitTimeRight.setPressed(false);
                    if (autoEventWaitTimeValueIndex < autoEvenWaitTimes.length -1) {
                        editTextAutoEventWaitTime.setText(getAautoEvenWaitTimes(++autoEventWaitTimeValueIndex));
                    }
                } else if (linearLayoutLoopSettingStartNextWaitTime.hasFocus()) {
                    linearLayoutLoopSettingStartNextWaitTimeRight.setPressed(false);
                    if (intervalWaitTimesValueIndex < intervalWaitTimes.length - 1) {
                        editTextIntervalWaitTime.setText(getIntervalWaitTimes(++ intervalWaitTimesValueIndex));
                    }
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 获取等待时间
     * @param index
     * @return
     */
    private String getAautoEvenWaitTimes(int index){
        int autoEvenWaitTime = autoEvenWaitTimes[index];
        return TextUtils.replace(getResources().getString(R.string.autoEvenWaitTimes), new String[]{"$"}, new String[]{String.valueOf(autoEvenWaitTime)}).toString();
    }

    /**
     * 获取轮播间隔时间
     * @param index
     * @return
     */
    private String getIntervalWaitTimes(int index){
        int intervalWaitTime = intervalWaitTimes[index];
        return TextUtils.replace(getResources().getString(R.string.intervalWaitTimes), new String[]{"$"}, new String[]{String.valueOf(intervalWaitTime)}).toString();
    }

    private void getAutoEventWaitTimeIndex(String string){
        int convertInt = TypeConversionUtils.StringConvertInt(string);
        for(int i = 0; i < autoEvenWaitTimes.length; i++) {
            if (autoEvenWaitTimes[i] == convertInt) {
                autoEventWaitTimeValueIndex = i;
                return;
            }
        }
    }

    private void getIntervalWaitTimeIndex(String string) {
        int convertInt = TypeConversionUtils.StringConvertInt(string);
        for(int i = 0; i < intervalWaitTimes.length; i++) {
            if (intervalWaitTimes[i] == convertInt) {
                intervalWaitTimesValueIndex = i;
                return;
            }
        }
    }

    /**
     * 退出登录请求
     */
    private void requetLogout(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("from_to", "2");
        OkHttpUtil.sendRequest(URL.AUTH_LOGOUT, params, new HttpRequestCallback(){
            @Override
            public void onStart() {
                if (mDialog != null) mDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(String json) {
                mDialog.dismiss();
                if (TextUtils.isEmpty(json)) return;
                HttpResponseBean response = JsonUtil.fromJson(json, HttpResponseBean.class);
                if (response.isSuccess()) {//   退出成功跳转首页
                    ToastUtil.showSuccess("退出成功");
                    SPUtil.remove(CommonConstants.LOGIN_INFO);
                    startActivity(new Intent(SettingActivity.this, ActivityLogin.class));
                    SettingActivity.this.finish();
                } else {
                    ToastUtil.showError(response.getMessage());
                }
            }

            @Override
            public void onFailure(IOException e) {
                mDialog.dismiss();
                ToastUtil.showError(R.string.network_err);
            }
        });
    }

    private void setListener() {
        linearLayoutLogout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextView childAt = (TextView) linearLayoutLogout.getChildAt(0);
                childAt.setTextColor(Color.parseColor(hasFocus ? "#FFFFFFFF" : "#66FFFFFF"));
                childAt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(hasFocus ? R.mipmap.icon_setting_logout : R.mipmap.icon_setting_logout_normal), null, null, null);
            }
        });
    }
}
