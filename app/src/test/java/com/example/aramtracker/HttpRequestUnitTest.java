package com.example.aramtracker;

import android.util.Log;

import com.jayway.jsonpath.JsonPath;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequestUnitTest {
    private final static String CHAMPION_INFO_LINK = "https://ddragon.leagueoflegends.com/cdn/12.8.1/data/en_US/champion.json";
    @Test
    public void test_allChampionsId() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CHAMPION_INFO_LINK)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            Set<String> data = JsonPath.read(result, "$.data.keys()");
            System.out.println(data.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
