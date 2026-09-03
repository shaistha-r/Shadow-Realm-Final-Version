package com.example.loginapp;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override public Response intercept(Chain chain) throws IOException {
        String token = SecureStore.getToken(context);
        Request.Builder builder = chain.request().newBuilder();
        String path = chain.request().url().encodedPath();
        if (!path.endsWith("/users/login") && token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
