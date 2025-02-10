package com.wujm1.tradesystem.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

/**
 * @author wujiaming
 * @date 2025-02-10 18:39
 */
public class OkClientUtils {

    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url).get().build();
        return client.newCall(request).execute().body().string();
    }
}
