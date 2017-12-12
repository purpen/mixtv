package com.taihuoniao.fineix.tv.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taihuoniao.fineix.tv.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by taihuoniao on 2016/3/14.
 */
public abstract class BaseFragment<T> extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    protected Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.e("taihuoniao", "---------->onCreate()" + getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (savedInstanceState != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment == null || fragment.getTag() == null || TextUtils.isEmpty(fragment.getTag())) {
                    continue;
                }
                if (TextUtils.equals(TAG, fragment.getTag())) {
                    fm.beginTransaction().show(fm.findFragmentByTag(TAG)).commit();
                } else {
                    fm.beginTransaction().hide(fm.findFragmentByTag(fragment.getTag())).commit();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        ButterKnife.bind(this, view);
        initList();
        requestNet();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        installListener();
    }

    protected void installListener() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        activity = null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected abstract void requestNet();

    protected void initList() {
    }

    protected abstract View initView();

    protected void refreshUI() {

    }

    protected void refreshUI(ArrayList<T> list) {

    }

    @Override
    public void onDestroyView() {
        clearNet();
        super.onDestroyView();
    }

    protected void addNet(Call httpHandler) {
        if (handlerList == null) {
            handlerList = new ArrayList<>();
        }
        handlerList.add(httpHandler);
    }

    private void clearNet() {
        if (handlerList != null) {
            for (Call httpHandler : handlerList) {
                if (httpHandler != null) {
//                    httpHandler.cancel();
                }
            }
        }
    }

    private List<Call> handlerList;
}
