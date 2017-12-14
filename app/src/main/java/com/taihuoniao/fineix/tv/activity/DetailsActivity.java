
package com.taihuoniao.fineix.tv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.BuyGoodDetailsBean;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.fragment.DetailsFragment;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stephen on 2017/11/9 18:34
 * Email: 895745843@qq.com
 */

public class DetailsActivity extends BaseActivity {
    private WaittingDialog mDialog;
    private DetailsFragment fragment;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        id = getIntent().getStringExtra("id");
        mDialog = new WaittingDialog(DetailsActivity.this);
        fragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.buyDetails_fragment);

        getProductDetails();
    }

    /**
     * 产品详情
     */
    private void getProductDetails(){
        HashMap<String, Object> stringObjectHashMap = getgoodsDetailsRequestParams(id);
        OkHttpUtil.sendRequest(URL.GOOD_DETAILS, stringObjectHashMap, new HttpRequestCallback(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(String json) {
                HttpResponseBean<BuyGoodDetailsBean> buyGoodDetailsBean2 = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<BuyGoodDetailsBean>>() { });
                if (buyGoodDetailsBean2.isSuccess()) {
                    BuyGoodDetailsBean buyGoodDetailsBean = buyGoodDetailsBean2.getData();
                    fragment.refreshData(buyGoodDetailsBean);
                }else {
                    ToastUtil.showError(buyGoodDetailsBean2.getMessage());
                }
            }

            @Override
            public void onFailure(IOException e) {
                mDialog.dismiss();
                ToastUtil.showError(R.string.network_err);
            }
        });
    }

    public HashMap<String, Object> getgoodsDetailsRequestParams(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return params;
    }

    private long mTimeLast;
    private long mTimeSpace;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            calculateTimer();
            long nowTime = SystemClock.elapsedRealtime();
            long mTimeDelay = nowTime - this.mTimeLast;
            this.mTimeLast = nowTime;
            if(this.mTimeSpace <= 80L && mTimeDelay <= 80L) {
                this.mTimeSpace += mTimeDelay;
                return true;
            }
            this.mTimeSpace = 0L;
        }
        return super.dispatchKeyEvent(event);
    }

    private Timer timer;
    private long currentMillis;

    @Override
    protected void onResume() {
        super.onResume();
        calculateTimer();
    }

    /**
     * 倒计时操作
     */
    private void calculateTimer(){
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((currentMillis += 1000) == CommonConstants.DELAYMILLIS_DETAILSPAGE) {
                    executeTask();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
        currentMillis = 0;
    }

    /**
     * 自动执行任务
     */
    private void executeTask(){
        fragment.getScrollableView().stop();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(CommonConstants.BROADCAST_FILTER));
                DetailsActivity.this.finish();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
