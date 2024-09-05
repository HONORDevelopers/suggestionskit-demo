/*
 * Copyright (c) Honor Device Co., Ltd. 2024-20224. All rights reserved.
 */

package com.hihonor.suggestionkittool.util;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 文件处理工具类
 *
 * @author t00031915
 * @since 2024-07-12
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 获取文件规范路径
     *
     * @param file 文件
     * @return 文件规范路径
     */
    public static String getCanonicalPath(File file) {
        if (file == null) {
            return "";
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            Logger.error(TAG, "get file path IOException.");
            return "";
        }
    }



    /**
     * 文件读取
     *
     * @param file 文件
     * @return 文件内容
     */
    public static String readFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String result = "";
            while ((result = br.readLine()) != null) {
                stringBuilder.append(result);
            }
        } catch (FileNotFoundException e) {
            Logger.info(TAG, "readFile FileNotFoundException " + e.fillInStackTrace());
        } catch (IOException e) {
            Logger.info(TAG, "readFile error ");
        } finally {
            if (br != null) {
                close(br);
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 关闭流
     *
     * @param closeable 流
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Logger.error(TAG, "IOException");
            }
        }
    }
}
