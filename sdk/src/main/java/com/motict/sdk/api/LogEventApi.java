package com.motict.sdk.api;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogEventApi {
    private final String apiKey;
    private final LogEventCallback callback;


    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient();


    public LogEventApi(String apiKey, LogEventCallback callback) {
        this.apiKey = apiKey;
        this.callback = callback;
    }

    public void execute(
            String sid,
            String osVersion,
            String appVersion,
            String appPackageName,
            String eventName,
            Date eventTimestamp,
            String latitude,
            String longitude,
            String errorMessage
    ) {
        final String requestBody = gson.toJson(new LogEventRequest(
                sid,
                osVersion,
                appVersion,
                appPackageName,
                eventName,
                eventTimestamp,
                latitude,
                longitude,
                errorMessage
        ));

        Request request = new Request.Builder()
                .url("https://api.motict.com/v1/calls/webhook/sdk")
                .addHeader("Authorization", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaTypeApi.JSON))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onLogEventFailed(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    final String body = response.body().string();

                    if (response.code() >= 400)
                        callback.onLogEventFailed(gson.fromJson(body, ErrorResponse.class).toException());


                    callback.onLogEventSuccess(body);
                } catch (IOException e) {
                    callback.onLogEventFailed(e);
                }
            }
        });
    }
}
