package com.taihuoniao.fineix.tv.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.taihuoniao.fineix.tv.R;
import com.taihuoniao.fineix.tv.base.BaseFragment;
import com.taihuoniao.fineix.tv.bean.BuyGoodDetailsBean;
import com.taihuoniao.fineix.tv.common.App;
import com.taihuoniao.fineix.tv.common.CommonConstants;
import com.taihuoniao.fineix.tv.utils.QrCodeUtil;
import com.taihuoniao.fineix.tv.view.autoScrollViewpager.ScrollableView;
import com.taihuoniao.fineix.tv.view.autoScrollViewpager.ViewPagerAdapter;

import java.util.List;

/**
 * Created by taihuoniao on 2016/8/25 15:53
 * Email: 895745843@qq.com
 */
public class DetailsFragment extends BaseFragment implements ScrollableView.OnPageChangedListener {
    private ViewHolder holder;//headerview控件
    private ViewPagerAdapter<String> viewPagerAdapter;
    private BuyGoodDetailsBean mbuyGoodDetailsBean;

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
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.scrollableView.getLayoutParams();
//        layoutParams.width = App.getContext().getScreenWidth();
//        layoutParams.height = layoutParams.width * 422 / 750;
//        holder.scrollableView.setLayoutParams(layoutParams);

//        pullRefreshView.setAdapter(goodDetailsSceneListAdapter);
//        holder.detailContainer.setOnClickListener(this);

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
        if (holder.scrollableView != null) {
            holder.scrollableView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (holder.scrollableView != null) {
            holder.scrollableView.stop();
        }
    }

    //用来刷新页面
    public void refreshData(BuyGoodDetailsBean dataBean) {
        this.mbuyGoodDetailsBean = dataBean;
        if (dataBean == null) {
            return;
        }

        // 加载产品二维码
        String share_view_url = mbuyGoodDetailsBean.getShare_view_url();
        try {
            if (isAdded()) {
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_company);
                Bitmap bitmap = QrCodeUtil.create2DCode(bitmap1, share_view_url, holder.qrCodeImage.getWidth() - 5, holder.qrCodeImage.getHeight() - 5);
                if (bitmap != null) {
                    holder.qrCodeImage.setImageBitmap(bitmap);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (dataBean.getStage() != 9) {
//            holder.detailContainer.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.liangdianContainer.setVisibility(View.GONE);
        } else {
            holder.marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (viewPagerAdapter == null) {
            List<String> asset = mbuyGoodDetailsBean.getAsset();
            viewPagerAdapter = new ViewPagerAdapter<>(activity, asset);
            holder.scrollableView.setAdapter(viewPagerAdapter.setInfiniteLoop(true));
            holder.scrollableView.setOnPageChangeListener(this);
            holder.scrollableView.setAutoScrollDurationFactor(8);
            holder.scrollableView.setInterval(4000);
            holder.scrollableView.showIndicators();
            holder.scrollableView.start();

            if (holder.textView_currentPageWithTotal != null) {
                holder.textView_currentPageWithTotal.setText((1) + "/" + asset.size());
            }
        } else {
            viewPagerAdapter.notifyDataSetChanged();
        }
        holder.title.setText(dataBean.getTitle());
        holder.price.setText("¥" + dataBean.getSale_price());
        if (dataBean.getMarket_price() > dataBean.getSale_price()) {
            holder.marketPrice.setVisibility(View.VISIBLE);
            holder.marketPrice.setText("¥" + dataBean.getMarket_price());
        } else {
            holder.marketPrice.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(dataBean.getAdvantage())) {
            holder.liangdianContainer.setVisibility(View.GONE);
        } else {
            holder.liangdian.setText(dataBean.getAdvantage());
        }
//        holder.marketPrice2.setVisibility("1".equals(dataBean.getActive_summary().getOrder_reduce()) ? View.VISIBLE : View.GONE);
//        holder.marketPrice2.setVisibility("0".equals(dataBean.getExtra().getDisabled_app_reduce()) ? View.VISIBLE : View.GONE);

        holder.marketPrice2.setVisibility(View.GONE);

        if (dataBean.getStage() != 9) {
            holder.marketPrice.setText("此产品为用户标记，暂未销售。浮游正在努力上架产品中ing...");
            holder.marketPrice.setVisibility(View.VISIBLE);
            holder.marketPrice2.setVisibility(View.GONE);
        }

        holder.buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int position = viewPagerAdapter.getCount() - 1;
//                holder.scrollableView.setCurrentItem(position);
            }
        });
        holder.buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (holder.textView_closeCurrentActivity != null) {
            holder.textView_closeCurrentActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static class ViewHolder {
        private ScrollableView scrollableView;
//        private LinearLayout detailContainer;
        private TextView title;
        private TextView price;
        private TextView marketPrice;
        private TextView marketPrice2;
        private LinearLayout liangdianContainer;
        private TextView liangdian;
//        private RelativeLayout brandContainer;
//        private ImageView img;
//        private RoundedImageView brandImg;
//        private TextView brandName;
        private ImageView qrCodeImage;
        private TextView buttonLeft;
        private TextView buttonRight;
        private TextView textView_currentPageWithTotal;
        private TextView textView_closeCurrentActivity;

        ViewHolder(View view) {
            super();
            scrollableView = (ScrollableView) view.findViewById(R.id.scrollableView);
//            detailContainer = (LinearLayout) view.findViewById(R.id.detail_container);
            title = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.price);
            marketPrice = (TextView) view.findViewById(R.id.market_price);
            marketPrice2 = (TextView) view.findViewById(R.id.market_price2);
            liangdianContainer = (LinearLayout) view.findViewById(R.id.liangdian_container);
            liangdian = (TextView) view.findViewById(R.id.liangdian);
//            brandContainer = (RelativeLayout) view.findViewById(R.id.brand_container);
//            img = (ImageView) view.findViewById(R.id.img);
//            brandImg = (RoundedImageView) view.findViewById(R.id.brand_img);
//            brandName = (TextView) view.findViewById(R.id.brand_name);
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

//    public void OnKeyDown(int keyCode, KeyEvent event){
//        if( event.KEYCODE_DROP_UP== keyCode ){  //如果按下的是上键
//            mImgBtnArray1[ThirdIndx ].requestFocus;
//        }
//        if( event.KEYCODE_DROP_DOWN == keyCode ){  //如果按下的是下键
//            mImgBtnArray3[ ThirdIndx ].requestFocus;
//        }
//        if( event.KEYCODE_DROP_LEFT == keyCode ){  //如果按下的是左键
//            mImgBtnArray1[ ThirdIndx-1 ].requestFocus;
//        }
//        if( event.KEYCODE_DROP_RIGHT == keyCode ){  //如果按下的是右键
//            mImgBtnArray1[ ThirdIndx+1 ].requestFocus;
//        }
//    }

    public ViewHolder getViewHolder(){
        return holder;
    }

    @Override
    public void onPageSelected(int total, int position) {
        if (holder.textView_currentPageWithTotal != null) {
            holder.textView_currentPageWithTotal.setText((position + 1) + "/" + total);
        }
    }
}
