package com.motict.sdk.api;

import com.google.gson.Gson;
import com.motict.sdk.exception.ApiResponseException;
import com.motict.sdk.exception.BlockedUnusualActivityException;
import com.motict.sdk.exception.IPNotAllowedException;
import com.motict.sdk.exception.InvalidAuthException;
import com.motict.sdk.exception.InvalidAuthTokenException;
import com.motict.sdk.exception.InvalidPhoneNumberException;
import com.motict.sdk.exception.MaxAttemptExceededException;
import com.motict.sdk.exception.MissingAuthTokenException;
import com.motict.sdk.exception.ServiceUnavailableException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestMissedCallOTPApi {
    private final String apiKey;
    private final RequestMissedCallOTPCallback callback;

    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient();


    public RequestMissedCallOTPApi(String apiKey, RequestMissedCallOTPCallback callback) {
        this.apiKey = apiKey;
        this.callback = callback;
    }

    public void execute(String verifiedPhone) {
        final String requestBody = gson.toJson(new RequestMissedCallOTPRequest(verifiedPhone));


        Request request = new Request.Builder()
                .url("https://api.motict.com/v1/calls")
                .addHeader("Authorization", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaTypeApi.JSON))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onRequestMissedCallOTPFailed(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    final String body = response.body().string();

                    if (response.code() >= 400) {
                        callback.onRequestMissedCallOTPFailed(errorResponseToException(gson.fromJson(body, ErrorResponse.class)));
                        return;
                    }

                    callback.onRequestMissedCallOTPSuccess(gson.fromJson(body, RequestMissedCallOTPResponse.class));
                } catch (IOException e) {
                    callback.onRequestMissedCallOTPFailed(e);
                }
            }
        });
    }

    private Exception errorResponseToException(ErrorResponse errorResponse) {
        switch (errorResponse.getErrorCode()) {
            case 110:
                return new InvalidPhoneNumberException();
            case 111:
                return new MaxAttemptExceededException();
            case 112:
                return new BlockedUnusualActivityException();
            case 113:
                return new MissingAuthTokenException();
            case 114:
                return new InvalidAuthTokenException();
            case 115:
                return new ServiceUnavailableException();
            case 117:
                return new InvalidAuthException();
            case 118:
                return new IPNotAllowedException();
            default:
                return new ApiResponseException(errorResponse.getMessage());
        }
    }
}
