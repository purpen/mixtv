package com.taihuoniao.fineix.tv.view.autoScrollViewpager;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.activity.DetailsActivity;
import com.taihuoniao.fineix.tv.bean.ViewPagerDataBean;
import com.taihuoniao.fineix.tv.common.WaittingDialog;
import com.taihuoniao.fineix.tv.utils.GlideUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.ToastUtil;

import java.util.List;

public class ViewPagerAdapter<T> extends RecyclingPagerAdapter {
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private List<ViewPagerDataBean> list;
    private int size;
    private boolean isInfiniteLoop;
    private WaittingDialog dialog;

    public int getSize() {
        return size;
    }

    public ViewPagerAdapter(final Activity activity, List<ViewPagerDataBean> list) {
        this.activity = activity;
        this.list = list;
        this.size = (list == null ? 0 : list.size());
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        if (size == 0) {
            return 0;
        }
        return isInfiniteLoop ? Integer.MAX_VALUE : size;
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? (position) % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(activity);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setTag(R.id.glide_image_tag, holder);
        } else {
            holder = (ViewHolder) view.getTag(R.id.glide_image_tag);
        }
        int position1 = getPosition(position);
        final ViewPagerDataBean content = list.get(position1);
        if (TextUtils.isEmpty(content.getImageUrl())) {
            ToastUtil.showError("图片链接为空");
        } else {
            GlideUtil.displayImage(content.getImageUrl(), holder.imageView);
        }
        if (activity instanceof DetailsActivity) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: 2017/11/21 无须点击操作
//                    MainApplication.picList = (List<String>) list;
//                    Intent intent = new Intent(activity, BannerActivity.class);
//                    activity.startActivity(intent);
                }
            });
        }
        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ViewPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }


    /**
     * 替换数据
     * @param list
     */
    public void setList(List<ViewPagerDataBean> list) {
        if (this.list != null) {
            this.list.clear();
        }
        this.list = list;
        this.size = (list == null ? 0 : list.size());
    }

    /**
     * 添加数据
     * @param list
     */
    public void addList(List<ViewPagerDataBean> list) {
        if (this.list == null) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }
        this.size = (list == null ? 0 : this.list.size());
        notifyDataSetChanged();
    }

    /**
     * 返回数据集
     * @return
     */
    public List<ViewPagerDataBean> getList(){
        return list;
    }
}