/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.util;

import android.util.Log;

/**
 * 日志打印
 *
 * @author t00031915
 * @since 2024-07-10
 */
public class Logger {
    private static final String TAG = "SuggestionsKit";

    private static final String COLON = ": ";

    private Logger() {
    }

    /**
     * debug级别日志
     *
     * @param tag 日志标签
     * @param messages 日志内容
     */
    public static void debug(String tag, String messages) {
        Log.d(TAG, tag + COLON + messages);
    }

    /**
     * info级别日志
     *
     * @param tag 日志标签
     * @param messages 日志内容
     */
    public static void info(String tag, String messages) {
        Log.i(TAG, tag + COLON + messages);
    }

    /**
     * warn级别日志
     *
     * @param tag 日志标签
     * @param messages 日志内容
     */
    public static void warn(String tag, String messages) {
        Log.w(TAG, tag + COLON + messages);
    }

    /**
     * error级别日志
     *
     * @param tag 日志标签
     * @param messages 日志内容
     */
    public static void error(String tag, String messages) {
        Log.e(TAG, tag + COLON + messages);
    }
}
