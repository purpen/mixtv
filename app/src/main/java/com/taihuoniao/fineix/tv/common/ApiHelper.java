package com.taihuoniao.fineix.tv.common;

import java.util.HashMap;

/**
 * Created by Stephen on 2017/12/14 19:33
 * Email: 895745843@qq.com
 */

public class ApiHelper {

    /**
     * 产品列表参数
     * @param title title
     * @param sort sort
     * @param category_id category_id
     * @param brand_id brand_id
     * @param category_tag_ids category_tag_ids
     * @param page page
     * @param size size
     * @param ids ids
     * @param ignore_ids ignore_ids
     * @param stick stick
     * @param fine fine
     * @param stage stage
     * @return
     */
    public static HashMap<String, Object> getgetProductListRequestParams(String title, String sort, String category_id, String brand_id, String category_tag_ids, String page, String size, String ids, String ignore_ids, String stick, String fine, String stage) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("sort", sort);
        params.put("category_id", category_id);
        params.put("brand_id", brand_id);
        params.put("brand_id", brand_id);
        params.put("category_tags", category_tag_ids);
        params.put("page", page);
        params.put("size", size);
        params.put("ids", ids);
        params.put("ignore_ids", ignore_ids);
        params.put("stick", stick);
        params.put("fine", fine);
        params.put("stage", stage);
        return params;
    }

    public static HashMap<String, Object> getGoodsDetailsRequestParams(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return params;
    }

    /**
     * 接口参数
     * @param phone 手机号
     * @param password 密码
     * @return
     */
    public static HashMap<String, Object> getclickLoginNetRequestParams(String phone, String password) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("from_to", "2");     //登录渠道
        params.put("password", password);
        return params;
    }
}
