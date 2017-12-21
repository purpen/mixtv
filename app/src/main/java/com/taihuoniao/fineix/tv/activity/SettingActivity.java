package com.taihuoniao.fineix.tv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.taihuoniao.fineix.tv.MainActivity;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.TypeConversionUtils;

/**
 * Created by Stephen on 2017/12/19 15:47
 * Email: 895745843@qq.com
 */

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private EditText editTextAutoEventWaitTime;
    private EditText editTextIntervalWaitTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        readSettingInformation();
    }

    private void initViews() {
        editTextAutoEventWaitTime = (EditText) findViewById(R.id.editText_autoEvent_waitTime);
        editTextIntervalWaitTime = (EditText) findViewById(R.id.editText_Interval_waitTime);
    }


    @Override
    public void onBackPressed() {
        LogUtil.e(TAG, "---------onBackPressed()");
        saveSettingInformation();
    }

    private void readSettingInformation() {
        String autoEventWaitTime = SPUtil.read(CommonConstants.AUTO_EVENT_WAIT_TIME);
        String intervalWaitTime = SPUtil.read(CommonConstants.INTERVAL_WAIT_TIME);

        LogUtil.e(TAG, " -----Setting readSettingInformation----autoEventWaitTime: " + autoEventWaitTime  + " | intervalWaitTime: " + intervalWaitTime);
        if (TypeConversionUtils.StringConvertDouble(autoEventWaitTime) > 0) {
            editTextAutoEventWaitTime.setHint(autoEventWaitTime);
        }
        if (TypeConversionUtils.StringConvertDouble(intervalWaitTime) > 0) {
            editTextIntervalWaitTime.setHint(intervalWaitTime);
        }
    }

    private void saveSettingInformation() {
        String autoEventWaitTime = editTextAutoEventWaitTime.getText().toString();
        if (TypeConversionUtils.StringConvertDouble(autoEventWaitTime) > 0) {
            SPUtil.write(CommonConstants.AUTO_EVENT_WAIT_TIME, autoEventWaitTime);
            LogUtil.e(TAG, "-----Setting saveSettingInformation---- 保存成功  autoEventWaitTime: " + autoEventWaitTime);
        }
        String intervalWaitTime = editTextIntervalWaitTime.getText().toString();
        if (TypeConversionUtils.StringConvertDouble(intervalWaitTime) > 0) {
            SPUtil.write(CommonConstants.INTERVAL_WAIT_TIME, intervalWaitTime);
            LogUtil.e(TAG, "-----Setting saveSettingInformation---- 保存成功  intervalWaitTime: " + intervalWaitTime);
        }

        LogUtil.e(TAG, "-----Setting saveSettingInformation----autoEventWaitTime: " + autoEventWaitTime  + " | intervalWaitTime: " + intervalWaitTime);
        if (TextUtils.isEmpty(autoEventWaitTime) && TextUtils.isEmpty(intervalWaitTime)) {
            // do nothing
        } else {
            clearAllActivityExceptCurrent(this);
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
