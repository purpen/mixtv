package com.taihuoniao.fineix.tv.common;

/**
 * Created by Stephen on 2017/11/9 16:42
 * Email: 895745843@qq.com
 */

public class URL {
    public static final String BASE_URL = "https://api.taihuoniao.com/app/api"; //线上
//    public static final String BASE_URL = "https://m.taihuoniao.com/app/api";//生产环境
//    public static final String BASE_URL = "http://t.taihuoniao.com/app/api";  // 测试环境

    // 公共接口
    public static final String H5_URL                                       = "https://m.taihuoniao.com";
    public static final String AUTH_LOGIN                                   = BASE_URL + "/auth/login";
    public static final String AUTH_LOGOUT                                  = BASE_URL + "/auth/logout";

    public static final String URLSTRING_PRODUCTSLIST                       = BASE_URL + "/product/getlist";

    public static final String GATEWAY_FIND                                 = BASE_URL + "/gateway/find";
    public static final String GOOD_DETAILS                                 = BASE_URL + "/product/view";

    // 分类接口

    /*
       先锋智能
       数码电子
       户外出行
       运动健康
       文创玩品
       先锋设计
       家居日用
       厨房卫浴
       母婴成长
       品质饮食
     */
}
