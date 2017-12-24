
package com.taihuoniao.fineix.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;

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
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;
import com.taihuoniao.fineix.tv.utils.TypeConversionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private boolean isNeedLoadNextData;
    private boolean isAutoScroll;

    private static final int FIND_INDEX_BY_ID = 0x10010;
    private static final int FIRST_ITME = 0x10011;
    private static final int LAST_ITME = 0x10012;
    private static final int LOAD_ID_LOADING = 0x20001;
    private static final int LOAD_ID_FINISHED = 0x20000;
    private static final int LOAD_CATEGORY_LOADING = 0x20002;
    private static final int LOAD_CATEGORY_FINISH = 0x20003;
    private static int DEFAULT_LOAD_ITEM_COUNT = 3; //默认加载三个产品
    private int loadStatus;
    private long mTimeLast;
    private long mTimeSpace;
    private int categoryIndex; //分类索引
    private int idIndex; //di索引
    private int autoEventWaitTime;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_ID_FINISHED:
                    break;
                case LOAD_CATEGORY_FINISH:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        id = getIntent().getStringExtra("id");
        categoryId = getIntent().getStringExtra("categoryId");
        mDialog = new WaittingDialog(DetailsActivity.this);
        fragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.buyDetails_fragment);
        IntentFilter filter = new IntentFilter(CommonConstants.BROADCAST_FILTER_AUTO_LOAD_DATA);
        filter.addAction(CommonConstants.BROADCAST_FILTER_ONLY_LOAD_ONE);
        registerReceiver(mBroadcastReceiver, filter);

        getProductDetails(id);

        init();

        readSettingInformation();
    }

    /**
     * 产品详情
     */
    private void getProductDetails(String id){
        HashMap<String, Object> stringObjectHashMap = ApiHelper.getGoodsDetailsRequestParams(id);
        OkHttpUtil.sendRequest(URL.GOOD_DETAILS, stringObjectHashMap, new HttpRequestCallback(){
            @Override
            public void onSuccess(String json) {
                HttpResponseBean<BuyGoodDetailsBean> buyGoodDetailsBean2 = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<BuyGoodDetailsBean>>() { });
                if (buyGoodDetailsBean2.isSuccess()) {
                    BuyGoodDetailsBean buyGoodDetailsBean = buyGoodDetailsBean2.getData();
                    dismissDialog();
                    fragment.refreshData(buyGoodDetailsBean, isAutoScroll);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.e(TAG, "----------------> dispatchKeyEvent() ");
        resetTimerTask();
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if (delayResponseKeyEvent()) return true;
//            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//                clickLeftKey();
//                return true;
//            }
//            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                clickRightKey();
//                return true;
//            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 产品列表
     */
    private void getLProductList(String categoryId, final int flag){
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
                    switch (flag) {
                        case FIRST_ITME: // 加载第一条
                            if (ids.size() > 0) {
                                idIndex = 0;
                            }
                            break;
                        case LAST_ITME: //加载最后一条
                            if (ids.size() > 0) {
                                idIndex = ids.size() - 1;
                            }
                            break;
                        case FIND_INDEX_BY_ID:

                            // 默认加载数据
                            idIndex = getIdIndex(ids);
                            loadNextData(DEFAULT_LOAD_ITEM_COUNT);
                            break;
                    }

                    if (isNeedLoadNextData) {
                        getProductDetails(ids.get(idIndex));
                        isNeedLoadNextData = false;
                    }
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
    private int getCategoryIndex(){
        for(int i = 0; i < CommonConstants.CAGEGORYS.length; i ++) {
            if ((CommonConstants.CAGEGORYS[i]).equals(categoryId)) {
                return i;
            }
        }
        return 0;
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

    /**
     * 初始化操作
     */
    private void init(){
        mDialog = new WaittingDialog(DetailsActivity.this);
        categoryIndex = getCategoryIndex();
        getLProductList(categoryId, FIND_INDEX_BY_ID);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.e(TAG, "----------------> onKeyDown() ");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                fragment.showProductQrCode();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
//            case KeyEvent.KEYCODE_DPAD_LEFT:
//                clickLeftKey();
//                return true;
//            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                clickRightKey();
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(TAG, "------------>收到广播， 更新数据");
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case CommonConstants.BROADCAST_FILTER_AUTO_LOAD_DATA:
                    stopAutoScroll();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoLoadNextData();
                        }
                    }, 1000);
                    break;
                case CommonConstants.BROADCAST_FILTER_ONLY_LOAD_ONE:
                    if (loadStatus == LOAD_ID_FINISHED) {
                        loadNextData(1);
                    }
                    break;
            }
        }
    };

    /**
     * 停止自动轮播
     */
    private void stopAutoScroll(){
        if (fragment != null) {
            fragment.stopAutoScroll();
        }
    }

    /**
     * 开始自动轮播
     */
    private void startAutoScroll(){
        if (fragment != null) {
            fragment.startAutoScroll();
        }
    }

    /**
     * 自动加载下一条数据
     */
    private void autoLoadNextData(){
        fragment.dismissProductQrCode();
        isAutoScroll = true;
        if (idIndex < 0 || idIndex >= ids.size()) {
            LogUtil.e(TAG, "--------------> 加载有误！");
            return;
        }

        // 最后一条了
        if (idIndex == ids.size() - 1) {
            int  i = categoryIndex;
            categoryIndex = ++i % CommonConstants.CAGEGORYS.length;
            String cagegoryId = CommonConstants.CAGEGORYS[categoryIndex];
            isNeedLoadNextData = true;
            getLProductList(cagegoryId, FIRST_ITME);
        } else {
            idIndex++;
            String id = ids.get(idIndex);
            getProductDetails(id);
        }
    }


    private void clickLeftKey(){
        showDialog();
        LogUtil.e(TAG, "----------加载上一页");
        isAutoScroll = false;

        // 第一条
        if (idIndex == 0) {
            int i = categoryIndex;
            if (i == 0) {
                categoryIndex = CommonConstants.CAGEGORYS.length - 1;
            } else {
                categoryIndex--;
            }
            String cagegoryId = CommonConstants.CAGEGORYS[categoryIndex];
            isNeedLoadNextData = true;
            getLProductList(cagegoryId, LAST_ITME);
        } else {
            idIndex--;
            String id = ids.get(idIndex);
            getProductDetails(id);
        }
    }

    private void clickRightKey(){
        showDialog();
        LogUtil.e(TAG, "----------加载下一页");
        isAutoScroll = false;
        // 最后一条了
        if (idIndex == ids.size() - 1) {
            int  i = categoryIndex;
            categoryIndex = ++i % CommonConstants.CAGEGORYS.length;
            String cagegoryId = CommonConstants.CAGEGORYS[categoryIndex];
            isNeedLoadNextData = true;
            getLProductList(cagegoryId, FIRST_ITME);
        } else {
            idIndex++;
            String id = ids.get(idIndex);
            getProductDetails(id);
        }
    }

    private void showDialog(){
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dismissDialog(){
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 加载下一条数据
     */
    private void loadNextData(int loadItemCount){
        if (loadItemCount == 0) {
            // do nothing
            loadStatus = LOAD_ID_FINISHED;
        } else {
            loadStatus = LOAD_ID_LOADING;
            if (idIndex < 0 || idIndex >= ids.size()) {
                LogUtil.e(TAG, "--------------> 加载有误！");
                return;
            }

            // 最后一条了
            if (idIndex == ids.size() - 1) {
                int  tempIndex = categoryIndex;
                categoryIndex = ++tempIndex % CommonConstants.CAGEGORYS.length;
                String cagegoryId = CommonConstants.CAGEGORYS[categoryIndex];

                // 加载下一页的第一条

                loadStatus = LOAD_CATEGORY_LOADING;
                loadProductList(cagegoryId, loadItemCount);
            } else {
                idIndex++;
                String id = ids.get(idIndex);
                loadProductDetails(id, loadItemCount);
            }
        }
    }


    private void loadProductList(String categoryId, final int loadItemCount){
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
                    loadStatus = LOAD_CATEGORY_FINISH;
                    idIndex = 0;
                    loadProductDetails(ids.get(idIndex), loadItemCount);
                }
            }

            @Override
            public void onFailure(IOException e) {}
        });
    }

    private void loadProductDetails(String id, final int loadItemCount){
        loadStatus = LOAD_ID_LOADING;
        HashMap<String, Object> stringObjectHashMap = ApiHelper.getGoodsDetailsRequestParams(id);
        OkHttpUtil.sendRequest(URL.GOOD_DETAILS, stringObjectHashMap, new HttpRequestCallback(){
            @Override
            public void onSuccess(String json) {
                HttpResponseBean<BuyGoodDetailsBean> buyGoodDetailsBean2 = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<BuyGoodDetailsBean>>() { });
                if (buyGoodDetailsBean2.isSuccess()) {
                    BuyGoodDetailsBean buyGoodDetailsBean = buyGoodDetailsBean2.getData();
                    fragment.refreshData(buyGoodDetailsBean, isAutoScroll);
                    int tempItemCount = loadItemCount;
                    loadNextData(--tempItemCount );
                }
            }

            @Override
            public void onFailure(IOException e) {}
        });
    }

    /**
     * 处理按键响应时间，两次间隔小鱼80毫秒则不响应事件
     * @return true/false
     */
    private boolean delayResponseKeyEvent() {
        long nowTime = SystemClock.elapsedRealtime();
        long mTimeDelay = nowTime - this.mTimeLast;
        this.mTimeLast = nowTime;
        if(this.mTimeSpace <= 80L && mTimeDelay <= 80L) {
            this.mTimeSpace += mTimeDelay;
            return true;
        }
        this.mTimeSpace = 0L;
        return false;
    }

    /**
     * 定时任务
     */
    private Runnable autoKeyEventTask = new Runnable(){

        @Override
        public void run() {
            startAutoScroll();
        }
    };

    /**
     * 重置定时任务-倒计时操作
     */
    private void resetTimerTask(){
        stopAutoScroll();
        mHandler.removeCallbacks(autoKeyEventTask);
        mHandler.postDelayed(autoKeyEventTask, autoEventWaitTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(autoKeyEventTask);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void readSettingInformation() {
        String autoEventWaitTime2 = SPUtil.read(CommonConstants.AUTO_EVENT_WAIT_TIME);
        if (!TextUtils.isEmpty(autoEventWaitTime2)) {
            autoEventWaitTime = (TypeConversionUtils.StringConvertInt(autoEventWaitTime2) * 1000 * 60); //分钟
        } else {
            autoEventWaitTime = CommonConstants.AUTO_EVENT_WAIT_TIMES * 1000 * 60;
        }
        LogUtil.e(TAG, " -----Setting readSettingInformation----autoEventWaitTime: " + autoEventWaitTime);
    }
}
