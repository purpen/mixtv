package com.taihuoniao.fineix.tv.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.BuyGoodDetailsBean;
import com.taihuoniao.fineix.tv.bean.ViewPagerDataBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.utils.DpUtil;
import com.taihuoniao.fineix.tv.utils.LogUtil;
import com.taihuoniao.fineix.tv.utils.QrCodeUtil;
import com.taihuoniao.fineix.tv.view.autoScrollViewpager.ScrollableView;
import com.taihuoniao.fineix.tv.view.autoScrollViewpager.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taihuoniao on 2016/8/25 15:53
 * Email: 895745843@qq.com
 */
public class DetailsFragment extends BaseFragment implements ScrollableView.OnPageChangedListener {
    private ViewHolder holder;//headerview控件
    private ViewPagerAdapter<String> viewPagerAdapter;
    private BuyGoodDetailsBean mbuyGoodDetailsBean;
    private Bitmap qrCodeBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initView() {
        View header;
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_PORTRAIT)) {
            header = View.inflate(getActivity(), R.layout.header_fragment_buy_gooddetail_portrait, null);
        } else {
            header = View.inflate(getActivity(), R.layout.header_fragment_buy_gooddetail, null);
        }

        holder = new ViewHolder(header);
        return header;
    }

    @Override
    protected void initList() {
        if (App.screenOrientation.equals(CommonConstants.SCREENORIENTATION_PORTRAIT)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.scrollableView.getLayoutParams();
            layoutParams.width = App.getContext().getScreenWidth();
            layoutParams.height = layoutParams.width * 422 / 750;
            holder.scrollableView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void requestNet() {

    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    //用来刷新页面
    public void refreshData(BuyGoodDetailsBean dataBean, boolean isAutoScroll) {
        this.mbuyGoodDetailsBean = dataBean;
        if (dataBean == null) {
            return;
        }
        if (viewPagerAdapter == null) {
            LogUtil.e(TAG, "------------------ 初始化 viewPager");
            List<ViewPagerDataBean> viewPagerDataBeans = convertUsefulDataList(mbuyGoodDetailsBean);
            viewPagerAdapter = new ViewPagerAdapter<>(activity, viewPagerDataBeans);
            holder.scrollableView.setAdapter(viewPagerAdapter.setInfiniteLoop(true));
            holder.scrollableView.setOnPageChangeListener(this);
            holder.scrollableView.setAutoScrollDurationFactor(8);
            holder.scrollableView.setInterval(4000);
            holder.scrollableView.showIndicators();
            holder.scrollableView.start();
            refreshUi(viewPagerDataBeans.get(0).getInfoBean());
        } else {
            LogUtil.e(TAG, "------------------ 加载数据 viewPager + " + mbuyGoodDetailsBean.getAsset().size() + "条");
            viewPagerAdapter.addList(convertUsefulDataList(mbuyGoodDetailsBean));
            viewPagerAdapter.notifyDataSetChanged();
            if (isAutoScroll) {
                startAutoScroll();
            }
        }
//        holder.buttonLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                int position = viewPagerAdapter.getCount() - 1;
////                holder.scrollableView.setCurrentItem(position);
//            }
//        });
//        holder.buttonRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        if (holder.textView_closeCurrentActivity != null) {
//            holder.textView_closeCurrentActivity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getActivity().finish();
//                }
//            });
//        }
    }

    public static class ViewHolder {
        private ScrollableView scrollableView;
        private TextView title;
        private TextView price;
        private TextView marketPrice;
        private TextView marketPrice2;
        private LinearLayout liangdianContainer;
        private TextView liangdian;
        private ImageView qrCodeImage;
        private TextView buttonLeft;
        private TextView buttonRight;
        private TextView textView_currentPageWithTotal;
        private TextView textView_closeCurrentActivity;

        ViewHolder(View view) {
            super();
            scrollableView = (ScrollableView) view.findViewById(R.id.scrollableView);
            title = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.price);
            marketPrice = (TextView) view.findViewById(R.id.market_price);
            marketPrice2 = (TextView) view.findViewById(R.id.market_price2);
            liangdianContainer = (LinearLayout) view.findViewById(R.id.liangdian_container);
            liangdian = (TextView) view.findViewById(R.id.liangdian);
            qrCodeImage = (ImageView) view.findViewById(R.id.imageView_product_QRcode);
            buttonLeft = (TextView) view.findViewById(R.id.textView_button_left);
            buttonRight = (TextView) view.findViewById(R.id.textView_button_right);
            textView_currentPageWithTotal = (TextView) view.findViewById(R.id.textView_currentPageWithTotal);
            textView_closeCurrentActivity = (TextView) view.findViewById(R.id.textView_close_activity);
        }

        public TextView getButtonLeft() {
            return buttonLeft;
        }

        public TextView getButtonRight() {
            return buttonRight;
        }
    }

    public ViewHolder getViewHolder(){
        return holder;
    }

    @Override
    public void onPageSelected(int total, int position, Object o) {
        LogUtil.e(TAG, "------- onPageSelected() id: " + mbuyGoodDetailsBean.get_id() + " | total: " + total + " | position: " + position);
        if (o != null && o instanceof ViewPagerDataBean) {
            ViewPagerDataBean.InfoBean infoBean = ((ViewPagerDataBean) o).getInfoBean();
            refreshUi(infoBean);
        }
        if (holder.textView_currentPageWithTotal != null) {
            holder.textView_currentPageWithTotal.setText((position + 1) + "/" + viewPagerAdapter.getSize());
        }
        if (total - position < 10) {
            LogUtil.e(TAG, "--------------发送广播");
            getContext().sendBroadcast(new Intent(CommonConstants.BROADCAST_FILTER_ONLY_LOAD_ONE));
        }
    }

    public ScrollableView getScrollableView(){
        return holder.scrollableView;
    }

    private Dialog mDialog;

    /**
     * 展示产品二维码大图
     */
    public void showProductQrCode(){
        stopAutoScroll();
        ImageView linearLayout = getQrCodeImageView();
        linearLayout.setImageBitmap(qrCodeBitmap);
        if (mDialog == null) {
            mDialog = new Dialog(getActivity(), R.style.ProductQRCodeDialog);
            mDialog.setContentView((View) linearLayout.getParent());
            mDialog.setCanceledOnTouchOutside(true);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 隐藏产品二维码大图
     */
    public void dismissProductQrCode(){
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private ImageView qrCodeImageView;
    private ImageView getQrCodeImageView() {
        if (qrCodeImageView != null) {
            return qrCodeImageView;
        }
        qrCodeImageView = new ImageView(getActivity());
        qrCodeImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        int width = DpUtil.dp2px(getActivity(), 100);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width * 2, width * 2);
        qrCodeImageView.setLayoutParams(params);

        TextView textView = new TextView(getActivity());
        textView.setText("请扫码购买");
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margins = DpUtil.dp2px(getActivity(), 5);
        textViewParams.setMargins(0, margins, 0, margins);
        textView.setLayoutParams(textViewParams);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(qrCodeImageView);
        linearLayout.addView(textView);
        return qrCodeImageView;
    }

    /**
     * 停止自动轮播
     */
    private void stopAutoScroll(){
        if (holder.scrollableView != null) {
            holder.scrollableView.stop();
        }
    }

    /**
     * 开始自动轮播
     */
    private void startAutoScroll(){
        if (holder.scrollableView != null) {
            holder.scrollableView.start();
        }
    }

    /**
     * 刷新UI
     */
    private void refreshUi(ViewPagerDataBean.InfoBean infoBean){
        if (infoBean == null) {
            return;
        }
        // 加载产品二维码
        String share_view_url = infoBean.getShare_view_url();
        try {
            if (isAdded()) {
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_company);
                qrCodeBitmap = QrCodeUtil.create2DCode(bitmap1, share_view_url, holder.qrCodeImage.getWidth() - 5, holder.qrCodeImage.getHeight() - 5);
                if (qrCodeBitmap != null) {
                    holder.qrCodeImage.setImageBitmap(qrCodeBitmap);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (infoBean.getStage() != 9) {
            holder.price.setVisibility(View.GONE);
            holder.liangdianContainer.setVisibility(View.GONE);
        } else {
            holder.marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (holder.textView_currentPageWithTotal != null) {
            holder.textView_currentPageWithTotal.setText((1) + "/" + infoBean.getImageCount());
        }
        holder.title.setText(infoBean.getTitle());
        holder.price.setText("¥" + infoBean.getSale_price());
        if (infoBean.getMarket_price() > infoBean.getSale_price()) {
            holder.marketPrice.setVisibility(View.VISIBLE);
            holder.marketPrice.setText("¥" + infoBean.getMarket_price());
        } else {
            holder.marketPrice.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(infoBean.getAdvantage())) {
            holder.liangdianContainer.setVisibility(View.GONE);
        } else {
            holder.liangdian.setText(infoBean.getAdvantage());
        }
        holder.marketPrice2.setVisibility(View.GONE);
        if (infoBean.getStage() != 9) {
            holder.marketPrice.setText("此产品为用户标记，暂未销售。浮游正在努力上架产品中ing...");
            holder.marketPrice.setVisibility(View.VISIBLE);
            holder.marketPrice2.setVisibility(View.GONE);
        }
        holder.marketPrice.setVisibility(View.GONE);
    }

    /**
     * 重新组合数据集
     * @param dataBean dataBean
     * @return list
     */
    private List<ViewPagerDataBean> convertUsefulDataList(BuyGoodDetailsBean dataBean){
        List<ViewPagerDataBean> list = new ArrayList<>();
        List<String> asset = mbuyGoodDetailsBean.getAsset();
        for(int i = 0; i < asset.size(); i++) {
            ViewPagerDataBean viewPagerDataBean;
            if (i == 0) {
                ViewPagerDataBean.InfoBean infoBean = new ViewPagerDataBean.InfoBean(dataBean.getShare_view_url(), dataBean.getStage(), dataBean.get_id(), dataBean.getTitle(), dataBean.getSale_price(), dataBean.getMarket_price(), dataBean.getAdvantage(), asset.size());
                viewPagerDataBean = new ViewPagerDataBean(asset.get(i), infoBean);
            } else {
                viewPagerDataBean = new ViewPagerDataBean(asset.get(i));
            }
            list.add(viewPagerDataBean);
        }
        return list;
    }
}
