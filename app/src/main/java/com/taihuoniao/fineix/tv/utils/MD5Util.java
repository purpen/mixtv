package com.taihuoniao.fineix.tv.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.taihuoniao.fineix.tv.common.App;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by taihuoniao on 2016/3/28.
 */
public class MD5Util {
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuilder strBuf = new StringBuilder();
            for (byte anEncryption : encryption) {
                if (Integer.toHexString(0xff & anEncryption).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & anEncryption));
                } else {
                    strBuf.append(Integer.toHexString(0xff & anEncryption));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @NonNull
    public static List<HashMap<String,Object>> getSignedNameValuePairs(Map<String, Object> params) {
        params.put("app_type", "2");
        params.put("client_id", "1415289600");
        params.put("uuid", Util.getUUID(App.getContext()));
        params.put("channel", "10");
        params.put("time", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("sign", generateSign(sortHashMapToList(params)));

        Set<Map.Entry<String, Object>> entrySets = params.entrySet();
        List<HashMap<String,Object>> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, Object> next : entrySets) {
            HashMap<String,Object> nameValuePair = new HashMap<String,Object>();
            nameValuePair.put(next.getKey(), next.getValue());
            nameValuePairs.add(nameValuePair);
        }
//        LogUtil.e( "------------------------完整版参数："  +nameValuePairs.toString());
        return nameValuePairs;
    }

    /**
     *  排序
     */
    private static List<Map.Entry<String, Object>> sortHashMapToList(Map<String,Object> hashMap){
        List<Map.Entry<String, Object>> mapList = new ArrayList<>(hashMap.entrySet());
        Collections.sort(mapList, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> lhs, Map.Entry<String, Object> rhs) {
                return (lhs.getKey()).compareTo(rhs.getKey());
            }
        });
        return mapList;
    }

    private static String generateSign(List<HashMap.Entry<String, Object>> list){
        StringBuilder sign = new StringBuilder();
        boolean firstIn = true;
                for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, Object> nameValuePair = list.get(i);
            String name = nameValuePair.getKey();
            Object value = nameValuePair.getValue();
            if (name.equals("tmp") || name.equals("id_card_a_tmp") || name.equals("business_card_tmp")
                    || name.equals("link") || name.equals("cover_url") || name.equals("banners_url") || value == null
                    || TextUtils.isEmpty(value.toString()) ||name.equals("avatar_tmp")) {
                continue;
            }
            if (firstIn) {
                sign.append(name).append("=").append(value);
                firstIn = false;
            } else {
                sign.append("&").append(name).append("=").append(value);
            }
        }
        return getMD5(getMD5(sign.toString() + "545d9f8aac6b7a4d04abffe51415289600"));
    }
}
