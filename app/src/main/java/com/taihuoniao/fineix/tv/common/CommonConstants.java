package com.taihuoniao.fineix.tv.common;

/**
 * Created by Stephen on 2017/11/14 17:46
 * Email: 895745843@qq.com
 */

public class CommonConstants {
    public static final String LOGIN_INFO = "login_info";

    public static String[] TITLES = {"先锋智能", "数码电子", "户外出行", "运动健康", "文创玩品", "先锋设计", "家居日用", "厨房卫浴", "母婴成长", "品质饮食"};
    public static String[] CAGEGORYS = {"32", "31", "34", "76", "33", "30", "81", "82", "78", "79"};

    public static final String SCREENORIENTATION = "screenOrientation";
    public static final String SCREENORIENTATION_LANDSCAPE = "landscape";
    public static final String SCREENORIENTATION_PORTRAIT = "portrait";


    public static final int SCREENORIENTATION_LANDSCAPE_COLUMNS = 5;
    public static final int SCREENORIENTATION_PORTRAIT_COLUMNS = 3;

    public static final String BROADCAST_FILTER = "AutoKeyEvent";
    public static final String BROADCAST_FILTER_AUTO_LOAD_DATA = "AutoLoadNextData";
    public static final String BROADCAST_FILTER_ONLY_LOAD_ONE = "AutoLoadOneData";

    public static final long DELAYMILLIS_MAINPAGE = 1000 * 10;
    public static final long DELAYMILLIS_DETAILSPAGE = 1000 * 10;
    public static final long DELAYMILLIS_INTERVAL_TIME = 1000 * 4;

    /**
     * 测试帐号
     */
    public static final String TEST_ACCOUNT = "15001120509";
    public static final String TEST_SECRETKEY = "123456";

    /**
     * 等待时间设置
     */
    public static final String AUTO_EVENT_WAIT_TIME = "AutoEventWaitTime";
    public static final String INTERVAL_WAIT_TIME = "IntervalWaitTime";
}
