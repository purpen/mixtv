
package com.taihuoniao.fineix.tv.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseActivity;
import com.taihuoniao.fineix.tv.bean.BuyGoodDetailsBean;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.fragment.DetailsFragment;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;

/*
 * Details activity class that loads LeanbackDetailsFragment class
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
//                if (mDialog != null) mDialog.show();
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
//                 if (mDialog != null && !isFinishing()) {
//                    mDialog.dismiss();
//                }
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

    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
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
}
