package com.taihuoniao.fineix.tv.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.activity.ActivityLogin;
import com.taihuoniao.fineix.tv.activity.SettingActivity;
import com.taihuoniao.fineix.tv.adapter.CFragmentPagerAdapter;
import com.taihuoniao.fineix.tv.adapter.ListRecyclerViewAdapter;
import com.taihuoniao.fineix.tv.adapter.TitleRecyclerViewAdapter;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.HttpResponseBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.URL;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.fragment.ListFragment;
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
 * Created by Stephen on 2017/12/1 10:13
 * Email: 895745843@qq.com
 */

public class MyCustomLinearLayout extends LinearLayout implements View.OnFocusChangeListener, View.OnClickListener{
    private static String TAG = MyCustomLinearLayout.class.getSimpleName();
    private Context mContext;

    private RelativeLayout mRelativeLayout; // topBar
    private RecyclerView mRecyclerView; // 导航栏
    private ViewPager mViewPager; // 内容页面
    private LinearLayout imageViewReverse;
    private LinearLayout imageViewLogout;
    private LinearLayout imageViewSetting;

    private List<String> mStringList;
    private List<BaseFragment> mBaseFragments;
    private String[] TITLES = {"先锋智能", "数码电子", "户外出行", "运动健康", "文创玩品", "先锋设计", "家居日用", "厨房卫浴", "母婴成长", "品质饮食"};
    private String[] CAGEGORYS = {"32", "31", "34", "76", "33", "30", "81", "82", "78", "79"};
    private CFragmentPagerAdapter wellGoodsAdapter;
    private TitleRecyclerViewAdapter titleRecyclerViewAdapter;
    private int currentPostion; //当前页面位置
    private int columns = 5;
    private WaittingDialog mDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int autoEventWaitTime;


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
        imageViewSetting = (LinearLayout) mRelativeLayout.findViewById(R.id.imageView_main_titleBar_setting);

        imageViewReverse.setOnFocusChangeListener(this);
        imageViewLogout.setOnFocusChangeListener(this);
        imageViewReverse.setOnClickListener(this);
        imageViewLogout.setOnClickListener(this);
        imageViewSetting.setOnClickListener(this);

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
//                mRecyclerView.smoothScrollToPosition(position);
//                View childAt = mRecyclerView.getLayoutManager().getChildAt(position);
//                titleRecyclerViewAdapter.setIndicatorSelectedPosition(childAt);
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
//        mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(CommonConstants.BROADCAST_FILTER));
        readSettingInformation();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            LogUtil.e(TAG, "--------------> dispatchKeyEvent()");
            resumeRecyclerViewFocusedView();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mViewPager.hasFocus()) {

                        //  判断是不是第一行
                        RecyclerView recyclerView = getCurrentPageRecyclerView(mViewPager.getCurrentItem());
                        View focusedChild = recyclerView.getFocusedChild();
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(focusedChild);
                        if (childAdapterPosition < App.pageDisplayColumns) {
                            reFocusTitleRecyclerViewItem();
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
                        LogUtil.e(TAG, "--------------> dispatchKeyEvent() 按上键");
                        View viewByPosition = mRecyclerView.getLayoutManager().findViewByPosition(titleRecyclerViewAdapter.getLastSelectedPosition());
                        titleRecyclerViewAdapter.setIndicatorSelectedPosition(viewByPosition);
                        imageViewSetting.requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mRelativeLayout.hasFocus()) {
                        reFocusTitleRecyclerViewItem();
                        return true;
                    } else if (mRecyclerView.hasFocus()) {
                        LogUtil.e(TAG, "--------------> dispatchKeyEvent() 按下键");
                        View viewByPosition = mRecyclerView.getLayoutManager().findViewByPosition(titleRecyclerViewAdapter.getLastSelectedPosition());
                        titleRecyclerViewAdapter.setIndicatorSelectedPosition(viewByPosition);
                        reFocusViewPagerRecyclerViewItem();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (interceptLeftSlide()){
                        return true;
                    }
                    if (interceptLeftReCyclerViewSlide()) {
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
        }else if (v == imageViewReverse) {
            onFocusChangeWithImageReverse(hasFocus);
        } else if (v == imageViewLogout) {
            onFocusChangeWithImageViewLogout(hasFocus);
        } else if (v == mRecyclerView) {
            LogUtil.e(TAG, "================mRecyclerView:  hasFocus: " + hasFocus);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewReverse) {
//            ToastUtil.showInfo("暂不支持竖屏操作！");
            getContext().startActivity(new Intent(getContext(), SettingActivity.class));
        } else if (v == imageViewLogout) {
//            requetLogout();
        } else if (v == imageViewSetting) {
            getContext().startActivity(new Intent(getContext(), SettingActivity.class));
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

    // 禁止viewPager下recyclerView左滑
    private boolean interceptRightSlide() {
        if (mViewPager.hasFocus()) {
            RecyclerView currentPageRecyclerView = getCurrentPageRecyclerView();
            View focusedChild = currentPageRecyclerView.getFocusedChild();
            int childAdapterPosition = currentPageRecyclerView.getChildAdapterPosition(focusedChild);
            if ((childAdapterPosition + 1) % columns == 0) {
                return true;
            }
        }
        return false;
    }

    // 禁止viewPager下recyclerView左滑
    private boolean interceptLeftSlide() {
        if (mViewPager.hasFocus()) {
            RecyclerView currentPageRecyclerView = getCurrentPageRecyclerView();
            View focusedChild = currentPageRecyclerView.getFocusedChild();
            int childAdapterPosition = currentPageRecyclerView.getChildAdapterPosition(focusedChild);
            if (childAdapterPosition % columns == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 定时任务
     */
    private Runnable autoMoveFocusEventTask = new Runnable(){

        @Override
        public void run() {
            RecyclerView currentPageRecyclerView = getCurrentPageRecyclerView();
            if (currentPageRecyclerView == null) {
                LogUtil.e(TAG, "Error: currentPageRecyclerView is null !");
                return;
            }
            View currentPageRecyclerViewFocusedChild = currentPageRecyclerView.getFocusedChild();
            RecyclerView.LayoutManager layoutManager = currentPageRecyclerView.getLayoutManager();
            if (currentPageRecyclerViewFocusedChild == null) {
                currentPageRecyclerView.requestFocus();
                currentPageRecyclerViewFocusedChild = layoutManager.getChildAt(0);
                executeTask(currentPageRecyclerView, 0, currentPageRecyclerViewFocusedChild);
            } else {
                executeTask(currentPageRecyclerView, currentPageRecyclerView.getChildAdapterPosition(currentPageRecyclerViewFocusedChild), currentPageRecyclerViewFocusedChild);

//              取消自动移动到下一个View
//                int childAdapterPosition = currentPageRecyclerView.getChildAdapterPosition(currentPageRecyclerViewFocusedChild);
//                int itemCount = currentPageRecyclerView.getAdapter().getItemCount();
//
//                // recyclerView 最后item位置
//                if (childAdapterPosition == itemCount - 1) {
//                    if (currentPostion == mViewPager.getAdapter().getCount() - 1) {
//                        currentPostion = 0; //重置为0
//                        mViewPager.setCurrentItem(currentPostion);
//                        resetRecyclerItemPositionByViewPager(currentPostion);
//                    } else if (currentPostion < mViewPager.getAdapter().getCount() - 1) {
//                        currentPostion += 1; // 滑倒下一页
//                        mViewPager.setCurrentItem(currentPostion);
//                        resetRecyclerItemPositionByViewPager(currentPostion);
//                    }
//                } else if (childAdapterPosition < itemCount) {
//                    int position = ++childAdapterPosition;
//                    final View viewByPosition = layoutManager.getChildAt(position) == null ? layoutManager.findViewByPosition(position) : layoutManager.getChildAt(position);
//                    executeTask(currentPageRecyclerView, position, viewByPosition);
//                }
            }
        }
    };

    private void resetRecyclerItemPositionByViewPager(final int currentPostion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView2 = getCurrentPageRecyclerView(currentPostion);
                View focusedChild2 = recyclerView2.getLayoutManager().getChildAt(0);
                executeTask(recyclerView2, currentPostion, focusedChild2);
            }
        };
        mHandler.postDelayed(runnable, 2000);
    }

    private RecyclerView getCurrentPageRecyclerView(){
        ListFragment item = (ListFragment) wellGoodsAdapter.getItem(currentPostion);
        return item.getRecyclerView();
    }

    private RecyclerView getCurrentPageRecyclerView(int currentItem) {
        ListFragment item = (ListFragment) wellGoodsAdapter.getItem(currentItem);
        return item.getRecyclerView();
    }

    private void executeTask(RecyclerView recyclerView, int recyclerViewItemtPosition, final View recyclerViewItem) {
        if (recyclerViewItem != null) {
            recyclerView.scrollToPosition(recyclerViewItemtPosition);
            recyclerViewItem.requestFocus();
            mHandler.postDelayed(autoPerFormClickEventTask = genericAutoPerFormClickEvnt(recyclerViewItem), 1000);
        }
    }
    
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            isWaitUserOpera = false;
//            mHandler.postDelayed(autoMoveFocusEventTask, 2000);
//        }
//    };

    /**
     * 无人操作倒计时
     */
    public void resumeRecyclerViewFocusedView(){
        if (isWaitUserOpera) {
            clearTimeTask();
            mHandler.postDelayed(autoMoveFocusEventTask, autoEventWaitTime);
        }
    }

    private boolean isWaitUserOpera = true;

    /**
     * 让焦点重新回到titleRecyclerView最后一次选中的Item上
     */
    private void reFocusTitleRecyclerViewItem() {
        int lastSelectedPosition = titleRecyclerViewAdapter.getLastSelectedPosition();
        if (lastSelectedPosition != -1) {
            View viewByPosition = mRecyclerView.getLayoutManager().findViewByPosition(lastSelectedPosition);
            if (viewByPosition != null) {
                viewByPosition.requestFocus();
            }
        } else {
            mRecyclerView.requestFocus();
        }
    }

    /**
     * 让焦点重新回到viewPager 下 recyclerView 最后一次选中的Item上
     */
    private void reFocusViewPagerRecyclerViewItem() {
        RecyclerView recyclerView = getCurrentPageRecyclerView(mViewPager.getCurrentItem());
        ListRecyclerViewAdapter adapter = (ListRecyclerViewAdapter) recyclerView.getAdapter();
        int lastSelectedPosition = adapter.getLastSelectedPosition();
        if (lastSelectedPosition != -1) {
            View viewByPosition = recyclerView.getLayoutManager().findViewByPosition(lastSelectedPosition);
            if (viewByPosition != null) {
                viewByPosition.requestFocus();
            }
        } else {
            mViewPager.requestFocus();
        }
    }

    /**
     * imageViewReverse焦点改变时
     * @param hasFocus true/false
     */
    private void onFocusChangeWithImageReverse(boolean hasFocus) {
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
    }

    /**
     * imageViewLogout焦点改变时
     * @param hasFocus true/false
     */
    private void onFocusChangeWithImageViewLogout(boolean hasFocus) {
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

    private void readSettingInformation() {
        String autoEventWaitTime2 = SPUtil.read(CommonConstants.AUTO_EVENT_WAIT_TIME);
        if (!TextUtils.isEmpty(autoEventWaitTime2)) {
            autoEventWaitTime = (TypeConversionUtils.StringConvertInt(autoEventWaitTime2) * 1000 * 60);
        } else {
            autoEventWaitTime = CommonConstants.AUTO_EVENT_WAIT_TIMES  * 1000 * 60;
        }
        LogUtil.e(TAG, " -----Setting readSettingInformation----autoEventWaitTime: " + autoEventWaitTime);
    }

    private AutoPerFormClickEventTask autoPerFormClickEventTask;

    private AutoPerFormClickEventTask genericAutoPerFormClickEvnt(View recyclerViewItem){
       clearTimeTask();
        autoPerFormClickEventTask = null;
        return new AutoPerFormClickEventTask(recyclerViewItem);
    }

    class AutoPerFormClickEventTask implements Runnable{
        private View recyclerViewItem;

        public AutoPerFormClickEventTask(View recyclerViewItem) {
            this.recyclerViewItem = recyclerViewItem;
        }

        @Override
        public void run() {
            recyclerViewItem.performClick();
        }
    }

    /**
     * 清除定时任务
     */
    public void clearTimeTask(){
        mHandler.removeCallbacks(autoPerFormClickEventTask);
        mHandler.removeCallbacks(autoMoveFocusEventTask);
    }

    // 禁止导航栏下recyclerView左滑
    private boolean interceptLeftReCyclerViewSlide() {
        if (mRecyclerView.hasFocus()) {
            View focusedChild = mRecyclerView.getFocusedChild();
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(focusedChild);
            if (childAdapterPosition == 0) {
                return true;
            }
        }
        return false;
    }
}
