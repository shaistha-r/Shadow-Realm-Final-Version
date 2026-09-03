package com.example.loginapp;

import android.content.Context;
import android.widget.Toast;

public final class Ui {
    private Ui() {}
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
