package com.taihuoniao.fineix.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taihuoniao.fineix.tv.R;


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
        if (v == null) {
            return;
        }
        View view = v.findViewById(R.id.view_menu_indicate);
        view.setBackgroundColor(Color.parseColor("#FFBE28"));
        view.setVisibility(View.VISIBLE);
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
    }
}
