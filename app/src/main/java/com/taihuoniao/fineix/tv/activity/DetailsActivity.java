
package com.taihuoniao.fineix.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.BuyGoodDetailsBean;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.bean.ProductBean;
import com.taihuoniao.fineix.tv.common.ApiHelper;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.fragment.DetailsFragment;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stephen on 2017/11/9 18:34
 * Email: 895745843@qq.com
 */

public class DetailsActivity extends BaseActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private WaittingDialog mDialog;
    private DetailsFragment fragment;
    private String id;
    private String categoryId;
    private List<String> ids;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        id = getIntent().getStringExtra("id");
        categoryId = getIntent().getStringExtra("categoryId");
        mDialog = new WaittingDialog(DetailsActivity.this);
        fragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.buyDetails_fragment);
        registerReceiver(mBroadcastReceiver, new IntentFilter(CommonConstants.BROADCAST_FILTER_AUTO_LOAD_DATA));

        getProductDetails(id);

        init();
    }

    /**
     * 产品详情
     */
    private void getProductDetails(String id){
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
                    LogUtil.e(TAG, "更新第" + (categoryIndex + 1) + "页 第" + (idIndex + 1) + "数据" );
                    fragment.refreshData(buyGoodDetailsBean);
                }else {
                    ToastUtil.showError(buyGoodDetailsBean2.getMessage());
                }
            }

            @Override
            public void onFailure(IOException e) {
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
//                sendBroadcast(new Intent(CommonConstants.BROADCAST_FILTER));
//                DetailsActivity.this.finish();
                autoLoadNextData();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private int categoryIndex;
    private int idIndex;

    /**
     * 产品列表
     */
    private void getLProductList(String categoryId){
        HashMap<String, Object> stringObjectHashMap = ApiHelper.getgetProductListRequestParams(null, null, categoryId, null, null, String.valueOf(1), String.valueOf(8), null, null, null, "0", null);
        stringObjectHashMap.put("size", "15");
        OkHttpUtil.sendRequest(URL.URLSTRING_PRODUCTSLIST, stringObjectHashMap, new HttpRequestCallback(){

            @Override
            public void onSuccess(String json) {
                HttpResponseBean<ProductBean> productBean = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<ProductBean>>() {});
                if (productBean.isSuccess()) {
                    List<ProductBean.RowsEntity> rows = productBean.getData().getRows();
                    ids = new ArrayList<>();
                    for (ProductBean.RowsEntity rowsEntity:rows) {
                        ids.add(rowsEntity.get_id());
                    }
                    idIndex = getIdIndex(ids);
                    LogUtil.e(TAG, "当前是第" + (categoryIndex + 1) + "页 第" + (idIndex + 1) + "数据" );
                }
            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }

    /**
     * 获取当前category所在位置
     */
    private void getCategoryIndex(){
        for(int i = 0; i < CommonConstants.CAGEGORYS.length; i ++) {
            if ((CommonConstants.CAGEGORYS[i]).equals(categoryId)) {
                categoryIndex = i;
                return ;
            }
        }
    }

    /**
     * 获取当前id所在位置
     */
    private int getIdIndex(List<String> rows){
        for(int i = 0; i < rows.size(); i ++) {
            if ((rows.get(i)).equals(id)) {
                return i;
            }
        }
        return 0;
    }

    private void autoLoadNextData(){
        if (idIndex < 0 || idIndex >= ids.size()) {
            LogUtil.e(TAG, "--------------> 加载有误！");
            return;
        }

        // 最后一条了
        if (idIndex == ids.size() - 1) {
            categoryIndex++ ;
            int i = categoryIndex % CommonConstants.CAGEGORYS.length;
            String cagegoryId = CommonConstants.CAGEGORYS[i];
            getLProductList(cagegoryId);
        } else {
            idIndex++;
            String id = ids.get(idIndex);
            getProductDetails(id);
        }
    }

    /**
     * 初始化操作
     */
    private void init(){
        getCategoryIndex();
        getLProductList(categoryId);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                fragment.showProductQrCode();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(TAG, "------------>收到广播， 更新数据");
//            isWaitUserOpera = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoLoadNextData();
                }
            }, 3000);

        }
    };
}
