package com.taihuoniao.fineix.tv.bean;

/**
 * Created by Stephen on 2017/12/18 20:48
 * Email: 895745843@qq.com
 */

public class ViewPagerDataBean {
    private String imageUrl;
    private InfoBean infoBean;

    public ViewPagerDataBean(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ViewPagerDataBean(String imageUrl, InfoBean infoBean) {
        this.imageUrl = imageUrl;
        this.infoBean = infoBean;
    }

    public static class InfoBean{
        private String share_view_url;
        private int stage;
        private String _id;
        private String title;
        private double sale_price;
        private double market_price;
        private String advantage;
        private int imageCount; // 图片的数量

        public InfoBean(String share_view_url, int stage, String _id, String title, double sale_price, double market_price, String advantage, int imageCount) {
            this.share_view_url = share_view_url;
            this.stage = stage;
            this._id = _id;
            this.title = title;
            this.sale_price = sale_price;
            this.market_price = market_price;
            this.advantage = advantage;
            this.imageCount = imageCount;
        }

        public String getShare_view_url() {
            return share_view_url;
        }

        public int getStage() {
            return stage;
        }

        public String get_id() {
            return _id;
        }

        public String getTitle() {
            return title;
        }

        public double getSale_price() {
            return sale_price;
        }

        public double getMarket_price() {
            return market_price;
        }

        public String getAdvantage() {
            return advantage;
        }
        public int getImageCount() {
            return imageCount;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public InfoBean getInfoBean() {
        return infoBean;
    }
}
