package com.taihuoniao.fineix.tv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.activity.DetailsActivity;
import com.taihuoniao.fineix.tv.adapter.ListRecyclerViewAdapter;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.bean.ProductBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.GlobalCallBack;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.RecycleViewItemDecoration;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by Stephen on 2017/4/12 17:00
 * Email: 895745843@qq.com
 */

public class ListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private WaittingDialog mDialog;
    private String categoryId;
    private int currentPage = 1;
    private ListRecyclerViewAdapter mListAdapter;

    @Override
    protected void requestNet() {
        getLProductList();
        mRecyclerView.setFocusable(true);
        mRecyclerView.requestFocus();
    }

    @Override
    protected View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_list, null);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
        }
        initRecyclerView(view);
        mRecyclerView.setFocusable(false);
        mDialog = new WaittingDialog(getActivity());
        return view;
    }

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView)view. findViewById(R.id.list_fragment);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), App.pageDisplayColumns, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewItemDecoration(getActivity(), caculateItemBorderWidth()));
        mListAdapter = new ListRecyclerViewAdapter(getActivity(), new GlobalCallBack() {
            @Override
            public void callBack(Object o) {
                if (o != null && o instanceof ProductBean.RowsEntity) {
                    ProductBean.RowsEntity rowsEntity = (ProductBean.RowsEntity) o;
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("id", rowsEntity.get_id());
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mListAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 产品列表
     */
    private void getLProductList(){
        HashMap<String, Object> stringObjectHashMap = getgetProductListRequestParams(null, null, categoryId, null, null, String.valueOf(currentPage), String.valueOf(8), null, null, null, "0", null);
        stringObjectHashMap.put("size", "15");
        OkHttpUtil.sendRequest(URL.URLSTRING_PRODUCTSLIST, stringObjectHashMap, new HttpRequestCallback(){
            @Override
            public void onStart() {
                if (mDialog != null) mDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(String json) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                HttpResponseBean<ProductBean> productBean = JsonUtil.json2Bean(json, new TypeToken<HttpResponseBean<ProductBean>>() {});
                LogUtil.e( "----------" + productBean.isSuccess());
                if (productBean.isSuccess()) {
                    mListAdapter.putList(productBean.getData().getRows());
                }
            }

            @Override
            public void onFailure(IOException e) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                ToastUtil.showError(R.string.network_err);
            }
        });
    }

    public HashMap<String, Object> getgetProductListRequestParams(String title, String sort, String category_id, String brand_id, String category_tag_ids, String page, String size, String ids, String ignore_ids, String stick, String fine, String stage) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("sort", sort);
        params.put("category_id", category_id);
        params.put("brand_id", brand_id);
        params.put("brand_id", brand_id);
        params.put("category_tags", category_tag_ids);
        params.put("page", page);
        params.put("size", size);
        params.put("ids", ids);
        params.put("ignore_ids", ignore_ids);
        params.put("stick", stick);
        params.put("fine", fine);
        params.put("stage", stage);
        return params;
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }

    /*
     *  计算item之间的间隔(竖屏)
     *  itemBorderWidth = ( 整个屏幕的宽 - itemWidth * 5 - RecyclerView marginWidth ) / 5 * 2
     */
    private float caculateItemBorderWidthPortrait() {
        int screenWidth = App.getContext().getScreenWidth();
        int itemRecyclerViewWidth = getResources().getDimensionPixelSize(R.dimen.recyclerViewItemWidthSize2);
        int recyclerViewMarginWidth = getResources().getDimensionPixelSize(R.dimen.dp20) + getResources().getDimensionPixelSize(R.dimen.dp20);

        float itemBorderWidth = (screenWidth - itemRecyclerViewWidth * 3 - recyclerViewMarginWidth) /3 / 2;
        LogUtil.e("------------- screenWidth : " + screenWidth + " | itemRecyclerViewWidth : " + itemRecyclerViewWidth + " | recyclerViewMarginWidth : " + recyclerViewMarginWidth);
        LogUtil.e("------------- itemBorderWidth : " + itemBorderWidth);
        return itemBorderWidth;
    }

    /*
 *  计算item之间的间隔(横屏)
 *  itemBorderWidth = ( 整个屏幕的宽 - itemWidth * 5 - RecyclerView marginWidth ) / 5 * 2
 */
    private float caculateItemBorderWidthLandScape() {
        int screenWidth = App.getContext().getScreenWidth();
        int itemRecyclerViewWidth = getResources().getDimensionPixelSize(R.dimen.recyclerViewItemWidthSize);
        int recyclerViewMarginWidth = getResources().getDimensionPixelSize(R.dimen.dp20) + getResources().getDimensionPixelSize(R.dimen.dp20);

        float itemBorderWidth = (screenWidth - itemRecyclerViewWidth * 5 - recyclerViewMarginWidth) / 5 / 2;
        LogUtil.e("------------- screenWidth : " + screenWidth + " | itemRecyclerViewWidth : " + itemRecyclerViewWidth + " | recyclerViewMarginWidth : " + recyclerViewMarginWidth);
        LogUtil.e("------------- itemBorderWidth : " + itemBorderWidth);
        return itemBorderWidth;
    }

    private float caculateItemBorderWidth() {
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_PORTRAIT)) {
            return caculateItemBorderWidthPortrait();
        } else {
            return caculateItemBorderWidthLandScape();
        }
    }

    @Override
    public void onStop() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
        super.onStop();
    }
}
