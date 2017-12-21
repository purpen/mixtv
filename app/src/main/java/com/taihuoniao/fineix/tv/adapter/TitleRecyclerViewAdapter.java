/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 * Github:https://github.com/hejunlin2013/TVSample
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taihuoniao.fineix.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.utils.LogUtil;


/**
 * Created by hejunlin on 2015/10/16.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class TitleRecyclerViewAdapter extends RecyclerView.Adapter<TitleRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = TitleRecyclerViewAdapter.class.getSimpleName();

    // 数据集
    private String[] mDataList;
    private Context mContext;
    private OnBindListener onBindListener;


    private View focusedView;
    private View selectedViewIndicate;
    private int lastSelectedPosition = -1;

    public interface OnBindListener {
        void onBind(View view, int i);
    }

    public TitleRecyclerViewAdapter(Context context) {
        super();
        mContext = context;
    }

    public TitleRecyclerViewAdapter(Context context, String[] dataList) {
        super();
        mContext = context;
        this.mDataList = dataList;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int resId = R.layout.detail_menu_item;
        View view = LayoutInflater.from(mContext).inflate(resId, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        if (mDataList == null || mDataList.length == 0) {
            return;
        }
        viewHolder.mView.setBackgroundColor(Color.parseColor("#00000000"));
        viewHolder.mView.setVisibility(View.GONE);
        viewHolder.mTextView.setText(mDataList[i]);
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                itemViewOnFocusChange(v,hasFocus);
                if ( v == focusedView) {
                    return;
                }
                if (hasFocus) {
                    if (onBindListener != null) {
                        onBindListener.onBind(null, viewHolder.getAdapterPosition());
                    }
                    focusedView = v;
                    v.requestFocus();
                    selectedViewIndicate = v;
                    lastSelectedPosition = viewHolder.getAdapterPosition();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_menu_title);
            mView =  itemView.findViewById(R.id.view_menu_indicate);
        }
    }

    /**
     * 修改下面指示标状态
     * @param v
     */
    public void setIndicatorSelectedPosition(View v) {
        LogUtil.e(TAG, "------------setIndicatorSelectedPosition 1");
        if (v == null) {
            return;
        }
        LogUtil.e(TAG, "------------setIndicatorSelectedPosition 2");
        View view = v.findViewById(R.id.view_menu_indicate);
        LogUtil.e(TAG, "------------setIndicatorSelectedPosition 3");
        view.setBackgroundColor(Color.parseColor("#FFBE28"));
        LogUtil.e(TAG, "------------setIndicatorSelectedPosition 4");
        view.setVisibility(View.VISIBLE);
        LogUtil.e(TAG, "------------setIndicatorSelectedPosition 5" + (view.getVisibility() == View.VISIBLE));
//        if (view != null) {
//            if (view == selectedViewIndicate) {
//                // do nothing
//            } else if (selectedViewIndicate == null) {
//                view.setBackgroundColor(Color.parseColor("#FFBE28"));
//            } else {
//                selectedViewIndicate.findViewById(R.id.view_menu_indicate).setBackgroundColor(Color.parseColor("#00FFFFFF"));
//                view.setBackgroundColor(Color.parseColor("#FFBE28"));
//            }
//            selectedViewIndicate = v;
//        }
    }

    public int getLastSelectedPosition(){
        return lastSelectedPosition;
    }

    /**
     * itemView 焦点改变时,改变字体颜色
     * @param v v
     * @param hasFocus hasFcus
     */
    private void itemViewOnFocusChange(View v, boolean hasFocus){
        TextView textView = (TextView) v.findViewById(R.id.tv_menu_title);
        textView.setTextColor(Color.parseColor(hasFocus ? "#FFFFFF" : "#C2C2C2"));
        View view = v.findViewById(R.id.view_menu_indicate);
        if (hasFocus) {
            view.setVisibility(View.GONE);
        }

//        if (!hasFocus && selectedViewIndicate == v) {
//            view.setVisibility(View.VISIBLE);
//        }
    }
}
