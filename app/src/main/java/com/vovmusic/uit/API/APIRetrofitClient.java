package com.vovmusic.uit.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRetrofitClient { // Retrofit Client dùng để định nghĩa các url giao tiếp khi call API
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder() // Cấu hình giao thức mạng, tương tác mạng với phía server
                .readTimeout(10000, TimeUnit.MILLISECONDS) // Đọc từ server về, chờ quá lâu mà dữ liệu ko trả server về thì set time cho nó là 10000 mili giây = 10 giây
                .writeTimeout(10000, TimeUnit.MILLISECONDS) // Viết lên server, chờ quá lâu mà dữ liệu ko trả server về thì set time cho nó là 10000 mili giây = 10 giây
                .connectTimeout(10000, TimeUnit.MILLISECONDS) // Đợi quá 10 giây thì ngắt kết nối
                .retryOnConnectionFailure(true) // Trường hợp bị lỗi mạng hoặc mất kết nối thì sẽ cố gắng kết nối lại nếu để (true)
                .protocols(Arrays.asList(Protocol.HTTP_1_1)) // Kết nối giao thức với phía server
                .build();
        Gson gson = new GsonBuilder().setLenient().create(); // Dử liệu trả từ phía server về sau đó ta convert sang GSON
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient) // Cấu hình như trên
                .addConverterFactory(GsonConverterFactory.create(gson)) // Convert dữ liệu từ API thành GSON để dễ dàng tạo class model
                .build();

        return retrofit;
    }
}
