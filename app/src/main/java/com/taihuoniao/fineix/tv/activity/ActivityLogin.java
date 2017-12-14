package com.taihuoniao.fineix.tv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.MainActivity;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.bean.LoginInfoBean;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;

public class ActivityLogin extends BaseActivity implements View.OnClickListener {
    private EditText etPhone;
    private EditText etPassword;
    private Button btLogin;
    private WaittingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_password);
        btLogin = (Button) findViewById(R.id.bt_login);
        btLogin.setOnClickListener(this);
        mDialog = new WaittingDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                clickLoginEvent();
                break;
        }
    }

    /***
     * 点击登录按钮
     */
    private void clickLoginEvent(){
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showInfo("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showInfo("请输入密码");
            return;
        }
        requeLoginApi(phone, password);
    }

    /**
     * 接口参数
     * @param phone 手机号
     * @param password 密码
     * @return
     */
    public static HashMap<String, Object> getclickLoginNetRequestParams(String phone, String password) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("from_to", "2");     //登录渠道
        params.put("password", password);
        return params;
    }

    /**
     * 请求登陆接口
     */
    private void requeLoginApi(String phone, String password) {
        HashMap<String, Object> params = getclickLoginNetRequestParams(phone, password);
        OkHttpUtil.sendRequest(URL.AUTH_LOGIN, params, new HttpRequestCallback(){
            @Override
            public void onStart() {
                btLogin.setEnabled(false);
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                super.onStart();
            }

            @Override
            public void onSuccess(String json) {
                btLogin.setEnabled(true);
                mDialog.dismiss();
                HttpResponseBean<LoginInfoBean> response = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<LoginInfoBean>>() {
                });
                if (response.isSuccess()) {//登录界面登录成功
                    final LoginInfoBean loginInfo = response.getData();
                    SPUtil.write(CommonConstants.LOGIN_INFO, JsonUtil.toJson(loginInfo));
                    startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                    ActivityLogin.this.finish();
                    return;
                }
//                ToastUtil.showError(response.getMessage());

                // 再次请求接口，用默认帐号
                requeLoginApi(CommonConstants.TEST_ACCOUNT, CommonConstants.TEST_SECRETKEY);
            }

            @Override
            public void onFailure(IOException e) {
                mDialog.dismiss();
                ToastUtil.showError(R.string.network_err);
            }
        });
    }
}
