package com.taihuoniao.fineix.tv.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stephen.tv.R;
import com.taihuoniao.fineix.tv.activity.ActivityLogin;
import com.taihuoniao.fineix.tv.adapter.CFragmentPagerAdapter;
import com.taihuoniao.fineix.tv.adapter.TitleRecyclerViewAdapter;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stephen on 2017/3/3 20:36
 * Email: 895745843@qq.com
 */

public class MainFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout relative;
    private ViewPager viewPagerManList;
    private WaittingDialog mDialog;
    private LinearLayout imageViewReverse;
    private LinearLayout imageViewLogout;

    private List<String> mStringList;
    private List<BaseFragment> mBaseFragments;
    private String[] TITLES = {"先锋智能", "数码电子", "户外出行", "运动健康", "文创玩品", "先锋设计", "家居日用", "厨房卫浴", "母婴成长", "品质饮食"};
    private String[] CAGEGORYS = {"32", "31", "34", "76", "33", "30", "81", "82", "78", "79"};


    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void requestNet() {

    }

    @Override
    protected View initView() {
        final View view = View.inflate(getActivity(), R.layout.fragment_main, null);

        if (view != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.findViewById(R.id.myCustomLinearLayout).requestFocus();
                }
            }, 300);
            return view;
        }
//        mEpisodeListView = (EpisodeListView)view. findViewById(R.id.episodelistview);
        findViews(view);
        setOnFocusListener();
        setClickListener();
        mDialog = new WaittingDialog(getActivity());
//        initEpisodeListView();

        RecyclerView titleRecyclerView = (RecyclerView) view.findViewById(R.id.ry_menu_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        titleRecyclerView.setLayoutManager(linearLayoutManager);

        TitleRecyclerViewAdapter titleRecyclerViewAdapter = new TitleRecyclerViewAdapter(getActivity(), TITLES);
        titleRecyclerViewAdapter.setOnBindListener(new TitleRecyclerViewAdapter.OnBindListener() {
            @Override
            public void onBind(View view, int i) {
                Toast.makeText(getActivity(), TITLES[i], Toast.LENGTH_SHORT).show();
            }
        });
        titleRecyclerView.setAdapter(titleRecyclerViewAdapter);
        return view;
    }

    private void setOnFocusListener() {
        imageViewReverse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Drawable background = imageViewReverse.getBackground();
                    TextView textView = (TextView) imageViewReverse.getChildAt(0);
                    textView.setCompoundDrawablesWithIntrinsicBounds(background, null, null, null);
                    textView.setText("切换竖屏");

                    imageViewReverse.setBackgroundResource(R.mipmap.bg_button);

//                    ViewGroup.LayoutParams layoutParams = imageViewReverse.getLayoutParams();
//                    layoutParams.width = DpUtil.dp2px(getActivity(), 150);
//                    layoutParams.height = DpUtil.dp2px(getActivity(), 57);

                } else {
                    TextView textView = (TextView) imageViewReverse.getChildAt(0);
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    textView.setText("");

                    imageViewReverse.setBackgroundResource(R.mipmap.icon_reverse_screen);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageViewReverse.getLayoutParams();
                    layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
            }
        });
        imageViewLogout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Drawable background = imageViewLogout.getBackground();
                    TextView textView = (TextView) imageViewLogout.getChildAt(0);
                    textView.setCompoundDrawablesWithIntrinsicBounds(background, null, null, null);
                    textView.setText("退出登录");

                    imageViewLogout.setBackgroundResource(R.mipmap.bg_button);
                } else {
                    TextView textView = (TextView) imageViewLogout.getChildAt(0);
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    textView.setText("");

                    imageViewLogout.setBackgroundResource(R.mipmap.icon_logout);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageViewLogout.getLayoutParams();
                    layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
            }
        });
    }

    private void setClickListener() {
        imageViewReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showInfo("横竖屏切换");
            }
        });
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requetLogout();
            }
        });
    }

    private void findViews(View view) {
        relative = (RelativeLayout) view.findViewById(R.id.relative);
        imageViewReverse = (LinearLayout) relative.findViewById(R.id.imageView_reverse);
        imageViewLogout = (LinearLayout) relative.findViewById(R.id.imageView_logout);
        viewPagerManList = (ViewPager) view.findViewById(R.id.viewPager_man_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initTabLayoutAndViewPager() {
        CFragmentPagerAdapter wellGoodsAdapter = new CFragmentPagerAdapter(getChildFragmentManager(), mStringList, mBaseFragments);
        viewPagerManList.setAdapter(wellGoodsAdapter);
        viewPagerManList.setCurrentItem(0, false);
    }

    /**
     * 默认加载 两条
     */
    private void initTabLayout() {
        mStringList = new ArrayList<>();
        mBaseFragments = new ArrayList<>();

        for(int i = 0; i < TITLES.length; i++) {
            mStringList.add(TITLES[i]);
            ListFragment fragment = new ListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", CAGEGORYS[i]);
            fragment.setArguments(bundle);
            mBaseFragments.add(fragment);
        }
    }

    @Override
    protected void initList() {
//        initTitleBar();
//        initTabLayout();
//        initTabLayoutAndViewPager();
    }

    private void initTitleBar(){

    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    public static HashMap<String, String> getcategoryListRequestParams(String page, String domain, String show_all) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", page);
        params.put("size", 300 + "");
        params.put("show_all", show_all);
        params.put("domain", domain);
        params.put("use_cache", "1");
        return params;
    }

    @NonNull
    public static HashMap<String, String> getcategoryListRequestParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "10");
        params.put("domain", "13");//情景主题
        params.put("use_cache", "1");
        return params;
    }

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
                    startActivity(new Intent(getActivity(), ActivityLogin.class));
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

    @Override
    public void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
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
