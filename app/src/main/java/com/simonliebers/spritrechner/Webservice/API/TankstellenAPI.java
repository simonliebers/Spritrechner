package com.simonliebers.spritrechner.Webservice.API;

import android.location.Location;
import android.os.AsyncTask;

import com.simonliebers.spritrechner.General.Constants;
import com.simonliebers.spritrechner.General.Position;
import com.simonliebers.spritrechner.Webservice.DetailsConverter;
import com.simonliebers.spritrechner.Webservice.ListConverter;
import com.simonliebers.spritrechner.Webservice.Tankstellen;
import com.simonliebers.spritrechner.Webservice.TankstellenDetails;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TankstellenAPI {

    private static String apiKey = "cb3071d3-61a6-2457-8927-733e1f74f17f";
    private Tankstellen tankstellen;
    private TankstellenDetails tankstellenDetails;
    private OkHttpClient client = new OkHttpClient();
    private String url;
    private Response response;
    double radius = 5;

    OnResultListener resultListener;

    public TankstellenAPI(OnResultListener listener){
        this.resultListener = listener;
    }

    public Tankstellen getTankstellen(Position position, Constants.Type type, Constants.Sort sort){

        url = "https://creativecommons.tankerkoenig.de/json/list.php?lat=" + position.latitude + "&lng=" + position.longitude + "&rad=" + radius + "&sort=" + sort.toString() + "&type=" + type.toString() + "&apikey=" + apiKey;
        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                tankstellen = null;
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if(response.code() == 200){
                    tankstellen = ListConverter.fromJsonString(response.body().string());
                }
                countDownLatch.countDown();
            }
        });

        try{
            countDownLatch.await();
        } catch (InterruptedException e){

        }

        resultListener.callback(tankstellen);
        return tankstellen;
    }

    public TankstellenDetails getTankstellenDetails(String id){

        id = "24a381e3-0d72-416d-bfd8-b2f65f6e5802";

        url = "https://creativecommons.tankerkoenig.de/json/detail.php?id=" + id + "&apikey=" + apiKey;
        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                tankstellenDetails = null;
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if(response.code() == 200){
                    tankstellenDetails = DetailsConverter.fromJsonString(response.body().string());
                }
                countDownLatch.countDown();
            }
        });

        try{
            countDownLatch.await();
        } catch (InterruptedException e){

        }

        return tankstellenDetails;
    }

}
