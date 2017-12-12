package com.taihuoniao.fineix.tv.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stephen.tv.R;
import com.taihuoniao.fineix.tv.activity.ActivityLogin;
import com.taihuoniao.fineix.tv.adapter.CFragmentPagerAdapter;
import com.taihuoniao.fineix.tv.adapter.TitleRecyclerViewAdapter;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.fragment.ListFragment;
import com.taihuoniao.fineix.tv.utils.OkHttpUtil;
import com.taihuoniao.fineix.tv.utils.JsonUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.SPUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stephen on 2017/12/1 10:13
 * Email: 895745843@qq.com
 */

public class MyCustomLinearLayout extends LinearLayout implements View.OnFocusChangeListener, View.OnClickListener {
    private static String TAG = MyCustomLinearLayout.class.getSimpleName();
    private Context mContext;

    private RelativeLayout mRelativeLayout; // topBar
    private RecyclerView mRecyclerView; // 导航栏
    private ViewPager mViewPager; // 内容页面
    private LinearLayout imageViewReverse;
    private LinearLayout imageViewLogout;

    private List<String> mStringList;
    private List<BaseFragment> mBaseFragments;
    private String[] TITLES = {"先锋智能", "数码电子", "户外出行", "运动健康", "文创玩品", "先锋设计", "家居日用", "厨房卫浴", "母婴成长", "品质饮食"};
    private String[] CAGEGORYS = {"32", "31", "34", "76", "33", "30", "81", "82", "78", "79"};
    private CFragmentPagerAdapter wellGoodsAdapter;
    private TitleRecyclerViewAdapter titleRecyclerViewAdapter;
    private int currentPostion; //当前页面位置
    private int columns = 5;
    private WaittingDialog mDialog;


    public MyCustomLinearLayout(Context context) {
        this(context, null);
    }

    public MyCustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            mContext = context;
            init();
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_LANDSCAPE)) {
            inflater.inflate(R.layout.layout_mycustomlinearlayout, this, true);
        } else {
            inflater.inflate(R.layout.layout_mycustomlinearlayout_2, this, true);
        }
        columns = App.pageDisplayColumns;
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative);
        mRecyclerView = (RecyclerView) findViewById(R.id.ry_menu_item);
        mViewPager = (ViewPager) findViewById(R.id.viewPager_man_list);

        imageViewReverse = (LinearLayout) mRelativeLayout.findViewById(R.id.imageView_reverse);
        imageViewLogout = (LinearLayout) mRelativeLayout.findViewById(R.id.imageView_logout);

        imageViewReverse.setOnFocusChangeListener(this);
        imageViewLogout.setOnFocusChangeListener(this);
        imageViewReverse.setOnClickListener(this);
        imageViewLogout.setOnClickListener(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager((FragmentActivity)mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        titleRecyclerViewAdapter = new TitleRecyclerViewAdapter(mContext, TITLES);
        titleRecyclerViewAdapter.setOnBindListener(new TitleRecyclerViewAdapter.OnBindListener() {
            @Override
            public void onBind(View view, int i) {
                mViewPager.setCurrentItem(i, false);
                currentPostion = i;
            }
        });
        mRecyclerView.setAdapter(titleRecyclerViewAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initTabLayout();
        FragmentActivity context = (FragmentActivity) getContext();
        wellGoodsAdapter = new CFragmentPagerAdapter(context.getSupportFragmentManager(), mStringList, mBaseFragments);
        mViewPager.setAdapter(wellGoodsAdapter);
        mViewPager.setCurrentItem(0, false);
        mRelativeLayout.setOnFocusChangeListener(this);
        mRecyclerView.setOnFocusChangeListener(this);
        mViewPager.setOnFocusChangeListener(this);
        this.setOnFocusChangeListener(this);
        mDialog = new WaittingDialog(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mViewPager.hasFocus()) {

                        //  判断是不是第一行
                        ListFragment item = (ListFragment) wellGoodsAdapter.getItem(mViewPager.getCurrentItem());
                        RecyclerView recyclerView = item.getRecyclerView();
                        View focusedChild = recyclerView.getFocusedChild();
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(focusedChild);
                        if (childAdapterPosition < App.pageDisplayColumns) {
                            mRecyclerView.requestFocus();
                        } else {
                            int i = childAdapterPosition - App.pageDisplayColumns;
                            LogUtil.e(TAG, "----------- childAdapterPosition " + i);
                            View viewByPosition = recyclerView.getLayoutManager().findViewByPosition(i);
                            if (viewByPosition != null) {
                                recyclerView.scrollToPosition(i);
                                viewByPosition.requestFocus();
                            }
                        }
                        return true;
                    } else if (mRecyclerView.hasFocus()) {
                        imageViewReverse.requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mRelativeLayout.hasFocus()) {
                        mRecyclerView.requestFocus();
                        return true;
                    } else if (mRecyclerView.hasFocus()) {
                        mViewPager.requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (interceptLeftSlide()){
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mRecyclerView.hasFocus()) {
                        View focusedChild = mRecyclerView.getFocusedChild();
                        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(focusedChild);
                        if (childAdapterPosition == mRecyclerView.getLayoutManager().getItemCount() - 1) {
                            return true;
                        }
                    }
                    if (interceptRightSlide()) {
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this && hasFocus) {
            mRecyclerView.requestFocus();
        } else if (v == mRecyclerView && hasFocus) {
            // 获取导航栏的第一个条目

//            int lastSelectedPosition = titleRecyclerViewAdapter.getLastSelectedPosition();
//            if (lastSelectedPosition >= 0) {
//                mRecyclerView.getLayoutManager().getChildAt(lastSelectedPosition).requestFocus();
//            }

        } else if (v == mViewPager && hasFocus) {
            // 获取ViewPager (当前)下recylerview 的第一个item

//            ListFragment item = (ListFragment) wellGoodsAdapter.getItem(mViewPager.getCurrentItem());
//            RecyclerView recyclerView = item.getRecyclerView();
//            View child = recyclerView.getLayoutManager().findViewByPosition(0);
//            if (child != null) {
//                child.requestFocus();
//            }

        } else if (v == imageViewReverse) {
            ImageView imageView = (ImageView) imageViewReverse.getChildAt(0);
            TextView textView = (TextView) imageViewReverse.getChildAt(1);
            if (hasFocus) {
                imageView.setVisibility(VISIBLE);
                textView.setText("切换竖屏");
                textView.setVisibility(VISIBLE);
                imageViewReverse.setBackgroundResource(R.mipmap.bg_button);
            } else {
                imageView.setVisibility(GONE);
                textView.setVisibility(GONE);
                imageViewReverse.setBackgroundResource(R.mipmap.icon_reverse_screen);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageViewReverse.getLayoutParams();
                layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            }

        } else if (v == imageViewLogout) {
            ImageView imageView = (ImageView) imageViewLogout.getChildAt(0);
            TextView textView = (TextView) imageViewLogout.getChildAt(1);
            if (hasFocus) {
                imageView.setVisibility(VISIBLE);
                textView.setText("退出登录");
                textView.setVisibility(VISIBLE);

                imageViewLogout.setBackgroundResource(R.mipmap.bg_button);
            } else {
                imageView.setVisibility(GONE);
                textView.setVisibility(GONE);

                imageViewLogout.setBackgroundResource(R.mipmap.icon_logout);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageViewLogout.getLayoutParams();
                layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewReverse) {
            ToastUtil.showInfo("暂不支持竖屏操作！");
        } else if (v == imageViewLogout) {
            requetLogout();
        }
    }

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

    private RecyclerView getCurrentPageRecyclerView(){
        ListFragment item = (ListFragment) wellGoodsAdapter.getItem(currentPostion);
        return item.getRecyclerView();
    }

    // 禁止viewPager下recyclerView左滑
    private boolean interceptRightSlide() {
        if (mViewPager.hasFocus()) {
            RecyclerView currentPageRecyclerView = getCurrentPageRecyclerView();
            int itemCount = currentPageRecyclerView.getLayoutManager().getItemCount();
            View focusedChild = currentPageRecyclerView.getFocusedChild();
            int childAdapterPosition = currentPageRecyclerView.getChildAdapterPosition(focusedChild);
//            if ((childAdapterPosition == itemCount - 1) && (currentPostion == mViewPager.getAdapter().getCount() - 1)) {
//                return true;
//            }
            if ((childAdapterPosition + 1) % columns == 0) {
                return true;
            }
        }
        return false;
    }

    // 禁止viewPager下recyclerView右滑
    private boolean interceptLeftSlide() {
        if (mViewPager.hasFocus()) {
            RecyclerView currentPageRecyclerView = getCurrentPageRecyclerView();
            View focusedChild = currentPageRecyclerView.getFocusedChild();
            int childAdapterPosition = currentPageRecyclerView.getChildAdapterPosition(focusedChild);
//            if ((childAdapterPosition == 0) && (currentPostion == 0)) {
//                return true;
//            }
            if (childAdapterPosition % columns == 0) {
                return true;
            }
        }
        return false;
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
                    mContext.startActivity(new Intent(mContext, ActivityLogin.class));
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
}
