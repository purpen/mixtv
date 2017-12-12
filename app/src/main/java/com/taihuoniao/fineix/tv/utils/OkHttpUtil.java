package com.taihuoniao.fineix.tv.utils;

import android.os.Message;
import android.text.TextUtils;

import com.taihuoniao.fineix.tv.common.HttpRequestCallback;
import com.taihuoniao.fineix.tv.common.NetWorkHandler;
import com.taihuoniao.fineix.tv.common.URL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Stephen on 2017/1/4 16:48
 * Email: 895745843@qq.com
 */

public class OkHttpUtil {

    public static final String POST="POST";
    public static final String GET="GET";
    public static final String PUT="PUT";
    public final static int CONNECT_TIMEOUT = 30;
    public final static int READ_TIMEOUT = 30;
    public final static int WRITE_TIMEOUT = 30;

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    private static final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();


    public static Call sendRequest(String type, String requestUrl, HashMap<String, String> params, HttpRequestCallback callback) {
        if (null == params) throw new IllegalArgumentException("argument params can not be null ");
        if (TextUtils.isEmpty(requestUrl)) throw new IllegalArgumentException("argument requestUrl can not be null");
        if (null==callback) throw new IllegalArgumentException("argument callback can not be null");
        String url;
        if (!requestUrl.contains("http")) {
            url = URL.BASE_URL + requestUrl;
        } else {
            url = requestUrl;
        }
        Request request=getRequest(type,url,params);

        if (null==request) return null;

        Call call = mOkHttpClient.newCall(request);

        callback.onStart();
        final NetWorkHandler handler = new NetWorkHandler(callback);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what=NetWorkHandler.CALLBACK_FAILURE;
                message.obj=e;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message message = Message.obtain();
                    message.what=NetWorkHandler.CALLBACK_SUCCESS;
                    message.obj=response.body().string();
                    handler.sendMessage(message);
                } else {
//                    LogUtil.e("response.isSuccessful()==false,url="+ url);
                }
            }
        });

        return call;
    }

    /**
     * 包含文件参数的表单上传
     * @param requestUrl
     * @param params
     * @param callback
     * @return
     */
    public static Call sendRequest(String requestUrl, HashMap<String, Object> params, HttpRequestCallback callback) {
        if (null == params) throw new IllegalArgumentException("argument params can not be null ");
        if (TextUtils.isEmpty(requestUrl)) throw new IllegalArgumentException("argument requestUrl can not be null");
        if (null==callback) throw new IllegalArgumentException("argument callback can not be null");
        LogUtil.e("请求接口为" + requestUrl + "\n" + "请求参数为" + params.toString());
        String url;
        if (!requestUrl.contains("http")) {
            url = URL.BASE_URL + requestUrl;
        } else {
            url = requestUrl;
        }

        Request request=getRequest(url,params);

        if (null==request) return null;

        Call call = mOkHttpClient.newCall(request);

        callback.onStart();
        final NetWorkHandler handler = new NetWorkHandler(callback);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what=NetWorkHandler.CALLBACK_FAILURE;
                message.obj=e;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message message = Message.obtain();
                    message.what=NetWorkHandler.CALLBACK_SUCCESS;
                    message.obj=response.body().string();
                    LogUtil.e("请求结果为" + message.obj.toString());
                    handler.sendMessage(message);
                } else {
                    LogUtil.e("response.isSuccessful()==false");
                }
            }
        });

        return call;
    }


    private static Request getRequest(String url, HashMap<String, Object> params) {
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Connection", "close")
                .post(/*getMultipartBody(params)*/getMultipartBody(getSignedList(params)))
                .build();
        return request;
    }

    private static Request getRequest(String type, String url, HashMap<String,String> params) {
        Request request=null;
        switch (type){
            case POST:
                request = new Request.Builder()
                        .url(url)
                        .post(getFormBody(params))
                        .build();
                break;
            case GET:
                request = new Request.Builder().url(getUrl(url,params)).build();
                break;
            case PUT:
                request= new Request.Builder()
                        .url(url)
                        .put(getFormBody(params))
                        .build();
                break;
            default:
                break;
        }

        return request;
    }


    private static RequestBody getMultipartBody(HashMap<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        Set<Map.Entry<String,Object>> entries = params.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            if (entry.getValue() instanceof File){
                builder.addFormDataPart(entry.getKey(),"tmp.jpg", RequestBody.create(MEDIA_TYPE_JPG,(File) entry.getValue()));
            }else if(entry.getValue() instanceof String){
                builder.addFormDataPart(entry.getKey(), (String)entry.getValue());
            }
        }
        return builder.build();
    }


    /**
     * 拼接get参数
     * @param url
     * @param params
     * @return
     */
    private static String getUrl(String url, HashMap<String,String> params) {
        StringBuilder builder = new StringBuilder(url);
        if (params.isEmpty()) return builder.toString();
        builder.append("?");
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String,String> entry:entries){
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return builder.deleteCharAt(builder.lastIndexOf("&")).toString();
    }


    private static FormBody getFormBody(HashMap<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private static RequestBody getMultipartBody(List<HashMap<String, Object>> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(int i = 0; i < params.size(); i++) {
            HashMap<String, Object> stringObjectHashMap = params.get(i);
            Set<Map.Entry<String,Object>> entries = stringObjectHashMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof File){
                    builder.addFormDataPart(entry.getKey(),"tmp.jpg", RequestBody.create(MEDIA_TYPE_JPG,(File) entry.getValue()));
                }else if(entry.getValue() instanceof String){
                    builder.addFormDataPart(entry.getKey(), (String)entry.getValue());
                }
            }
        }
        return builder.build();
    }

    /**
     * 获取签名参数
     * @param params
     * @return
     */
    private static List<HashMap<String, Object>> getSignedList(Map<String, Object> params){
        if (params == null) {
            params = new HashMap<>();
        }
        List<HashMap<String, Object>> signedNameValuePairs = MD5Util.getSignedNameValuePairs(params);
        for(int i = signedNameValuePairs.size() - 1; i >= 0; i--) {
            HashMap<String, Object> stringObjectHashMap = signedNameValuePairs.get(i);
            Set<Map.Entry<String, Object>> entries = stringObjectHashMap.entrySet();
            Map.Entry<String, Object> next = entries.iterator().next();
            Object value = next.getValue();
            if (value == null || TextUtils.isEmpty(value.toString())){
                signedNameValuePairs.remove(stringObjectHashMap);
            }
        }
        LogUtil.e( "------------------------删减版参数："  +signedNameValuePairs.toString());
        return signedNameValuePairs;
    }

    /**
     * 移除不必打印的key-value
     */
    private static List<HashMap<String, Object>> removedUnnecessaryNameValuePairs(List<HashMap<String, Object>> nameValuePairs){
        if (nameValuePairs == null) {
            return new ArrayList<>();
        }
        for(int i = nameValuePairs.size() - 1; i >= 0; i--) {
            HashMap<String, Object> stringObjectHashMap = nameValuePairs.get(i);
            Set<Map.Entry<String, Object>> entries = stringObjectHashMap.entrySet();
            Map.Entry<String, Object> next = entries.iterator().next();
            String name = next.getKey();
            if ("app_type".equals(name) || "client_id".equals(name) || "uuid".equals(name) || "channel".equals(name) || "time".equals(name) || "sign".equals(name)) {
                nameValuePairs.remove(stringObjectHashMap);
            }
        }
        return nameValuePairs;
    }

    /******************************************************************************************************/
    private static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    public static Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        if (callback != null) {
            call.enqueue(callback);
        }
        return call;
    }

    public static Call post(String requestUrl, Map<String, Object> params, HttpRequestCallback callBack) {
        if (callBack != null) {
            callBack.onStart();
        }
        return post(getSignedList(params), requestUrl, callBack, true);
    }

    private static Call post(final List<HashMap<String, Object>> list, final String requestUrl, HttpRequestCallback callBack, boolean isShowProgress) {
        final NetWorkHandler handler = new NetWorkHandler(callBack);
        LogUtil.e("请求接口为" + requestUrl + "\n" + "请求参数为" + list.toString());
        String requestUrlreal = requestUrl;
        if (!requestUrl.contains("http")) {
            requestUrlreal = URL.BASE_URL + requestUrl;
        }
        return post(requestUrlreal, JsonUtil.toJson(list), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = NetWorkHandler.CALLBACK_FAILURE;
                LogUtil.e("请求接口为" + requestUrl + "\n" + "请求失败");
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = NetWorkHandler.CALLBACK_SUCCESS;
                msg.obj = response.body().string();
                LogUtil.e("请求接口为" + requestUrl + "\n" + "请求参数为" + removedUnnecessaryNameValuePairs(list).toString() + "\n" + "返回数据为" + msg.obj);
                handler.sendMessage(msg);
            }
        });
    }
}
