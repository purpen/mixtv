package com.taihuoniao.fineix.tv.common;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.bean.LoginInfoBean;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Stephen on 2018/1/9 16:34
 * Email: 895745843@qq.com
 */

public class AutoLoginAPI {
    /**
     * 请求登陆接口
     */
    public static void requeLoginApi(String phone, String password) {
        HashMap<String, Object> params = ApiHelper.getclickLoginNetRequestParams(phone, password);
        OkHttpUtil.sendRequest(URL.AUTH_LOGIN, params, new HttpRequestCallback(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(String json) {
                HttpResponseBean<LoginInfoBean> response = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<LoginInfoBean>>() {});
                if (response.isSuccess()) {//登录界面登录成功
                    final LoginInfoBean loginInfo = response.getData();
                    SPUtil.write(CommonConstants.LOGIN_INFO, JsonUtil.toJson(loginInfo));
                    LogUtil.e("-------------> 默认登录成功");
                }

//                // 再次请求接口，用默认帐号
//                requeLoginApi(CommonConstants.TEST_ACCOUNT, CommonConstants.TEST_SECRETKEY);
            }

            @Override
            public void onFailure(IOException e) {
            }
        });
    }
}
