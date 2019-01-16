package com.example.moucan.androidcoursesystem.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @packagename moucan.androidcoursesystem.utils
 * @desc: io操作
 */

public class IOUtils {
    /**
     * Close closeable object
     * 关闭可以关闭的对象
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LogUtils.d("IOUtils",e.toString());
            }
        }
    }

}