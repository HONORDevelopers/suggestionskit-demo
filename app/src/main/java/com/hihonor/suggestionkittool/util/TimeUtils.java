/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.suggestionkittool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间戳工具类
 *
 * @author t00031915
 * @since 2024-07-10
 */
public class TimeUtils {
    private static final String TAG = "TimeUtils";
    private static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间戳转换为yyyy-MM-dd HH:mm:ss"的格式
     *
     * @param longTime 时间戳
     * @return 时间 yyyy-MM-dd HH:mm:ss"
     */
    public static String timeToFullString(Long longTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_FULL, Locale.CHINA);
        Date date;
        try {
            date = dateFormat.parse(dateFormat.format(new Date(longTime)));
            Logger.debug(TAG, "timeToString: %1$s" + dateFormat.format(date));
            return dateFormat.format(date);
        } catch (NumberFormatException e) {
            Logger.error(TAG, "number format exception");
        } catch (ParseException e) {
            Logger.error(TAG, "time parse error");
        }
        return "";
    }
}
