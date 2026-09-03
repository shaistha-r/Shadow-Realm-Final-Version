package com.example.loginapp;

import android.content.Context;

public final class OnlineSession {
    private static final String PREF = "online_session";
    private OnlineSession() {}
    private static android.content.SharedPreferences p(Context c) { return c.getSharedPreferences(PREF, Context.MODE_PRIVATE); }

    public static void saveRoom(Context c, String roomCode, boolean host) {
        p(c).edit().putString("roomCode", roomCode).putBoolean("host", host)
                .putBoolean("voted", false).putBoolean("nightActed", false).putInt("round", 1).apply();
    }
    public static String room(Context c) { return p(c).getString("roomCode", null); }
    public static boolean host(Context c) { return p(c).getBoolean("host", false); }
    public static boolean voted(Context c) { return p(c).getBoolean("voted", false); }
    public static void markVoted(Context c) { p(c).edit().putBoolean("voted", true).apply(); }
    public static boolean nightActed(Context c) { return p(c).getBoolean("nightActed", false); }
    public static int round(Context c) { return p(c).getInt("round", 1); }
    public static void syncRound(Context c, int round) {
        if (p(c).getInt("round", 1) != round) p(c).edit().putInt("round", round).putBoolean("voted", false).putBoolean("nightActed", false).apply();
    }
    public static void markNightActed(Context c) { p(c).edit().putBoolean("nightActed", true).apply(); }
    public static void clear(Context c) { p(c).edit().clear().apply(); }
}
