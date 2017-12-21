package com.taihuoniao.fineix.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.bean.ProductBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.GlobalCallBack;
import com.taihuoniao.fineix.tv.utils.GlideUtil;

import java.util.List;

/**
 * Created by Stephen on 2017/3/3 21:20
 * Email: 895745843@qq.com
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.VH> /*implements  RecyclerView.ChildDrawingOrderCallback*/{

    private LayoutInflater mLayoutInflater;
    private GlobalCallBack mGlobalCallBack;
    private List<ProductBean.RowsEntity> list;
    private int lastSelectedPosition = -1;

    public ListRecyclerViewAdapter(Context context, GlobalCallBack globalCallBack) {
        this.mGlobalCallBack = globalCallBack;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ListRecyclerViewAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_PORTRAIT)) {
            return new ListRecyclerViewAdapter.VH(mLayoutInflater.inflate(R.layout.item_recyclerview_product2, null));
        } else {
            return new ListRecyclerViewAdapter.VH(mLayoutInflater.inflate(R.layout.item_recyclerview_product, null));
        }
    }

    @Override
    public void onBindViewHolder(final ListRecyclerViewAdapter.VH holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        final ProductBean.RowsEntity rowsEntity = list.get(adapterPosition);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGlobalCallBack != null) {
                    mGlobalCallBack.callBack(rowsEntity);
                }
            }
        });
        if (list.size() != 0) {
            GlideUtil.displayImage(list.get(adapterPosition).getCover_url(), holder.productImg);
            holder.name.setText(list.get(adapterPosition).getTitle());
            holder.price.setText("¥" + list.get(adapterPosition).getSale_price());
        }
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                itemViewOnFocusChange(v,hasFocus);
                itemViewOnFocusChange2(v,hasFocus);
                if (hasFocus) {
                    lastSelectedPosition = adapterPosition;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView name;
        TextView price;
        LinearLayout linearLayout_information;

        public VH(View itemView) {
            super(itemView);
            productImg = (ImageView)itemView. findViewById(R.id.product_img);
            name = (TextView)itemView. findViewById(R.id.name);
            price = (TextView)itemView. findViewById(R.id.price);
            linearLayout_information = (LinearLayout) itemView. findViewById(R.id.linearLayout_information);
        }
    }

    public void putList(List<ProductBean.RowsEntity> categoryListItems){
        this.list = categoryListItems;
        notifyDataSetChanged();
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
        TextView price = (TextView)v. findViewById(R.id.price);
        price.setTextColor(Color.parseColor(hasFocus ? "#FFFFFF" : "#FFBE28"));
        LinearLayout linearLayout_information = (LinearLayout) v. findViewById(R.id.linearLayout_information);
        linearLayout_information.setBackgroundColor(Color.parseColor(hasFocus ? "#A0772C":"#212121"));
    }

    /**
     * itemView 焦点改变时，itemView 放大动画
     * @param v v
     * @param hasFocus hasFcus
     */
    private void itemViewOnFocusChange2(View v, boolean hasFocus){
        float scaleX = hasFocus ? 1.17f : 1.0f;
        float scaleY = hasFocus ? 1.17f : 1.0f;
        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.animate(v).scaleX(scaleX).scaleY(scaleY).translationZ(1).start();
        } else {
            ViewCompat.animate(v).scaleX(scaleX).scaleY(scaleY).start();
            ViewGroup parent = (ViewGroup) v.getParent();
            parent.requestLayout();
            parent.invalidate();
        }
    }
}
