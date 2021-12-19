package com.vovmusic.uit.API;

public class APIService { // Class này là class trung gian sẽ kết nối 2 class kia lại với nhau
    private static final String url = "https://vovmusic.000webhostapp.com/server/"; // Server chùa của mình nè Hihi 😂

    public static DataService getService() { // Dữ liệu trả về cho DataService
        // Gửi cấu hình lên
        return APIRetrofitClient.getClient(url).create(DataService.class);
    }
}