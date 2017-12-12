package com.taihuoniao.fineix.tv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taihuoniao.fineix.tv.bean.ProductBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.common.GlobalCallBack;
import com.stephen.tv.R;
import com.taihuoniao.fineix.tv.utils.GlideUtil;

import java.util.List;

/**
 * Created by Stephen on 2017/3/3 21:20
 * Email: 895745843@qq.com
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.VH> {

    private LayoutInflater mLayoutInflater;
    private GlobalCallBack mGlobalCallBack;
    private List<ProductBean.RowsEntity> list;
    private int focusPosition = -1;

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
    public void onBindViewHolder(ListRecyclerViewAdapter.VH holder, int position) {
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
//        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    focusPosition = adapterPosition;
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView name;
        TextView price;

        public VH(View itemView) {
            super(itemView);
            productImg = (ImageView)itemView. findViewById(R.id.product_img);
            name = (TextView)itemView. findViewById(R.id.name);
            price = (TextView)itemView. findViewById(R.id.price);
        }
    }

    public void putList(List<ProductBean.RowsEntity> categoryListItems){
        this.list = categoryListItems;
        notifyDataSetChanged();
    }

    /**
     * 返回当前聚焦view的位置
     * @return position
     */
    public int getFocusPosition(){
        return focusPosition;
    }
}
