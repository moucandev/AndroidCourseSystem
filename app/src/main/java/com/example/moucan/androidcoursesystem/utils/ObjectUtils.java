package com.example.moucan.androidcoursesystem.utils;

/**
 * @packagename moucan.androidcoursesystem.utils
 * @desc: 对象操作
 */

public class ObjectUtils {
    /**
     * Returns true if a and b are equal.
     * 如果a和b相等，返回TRUE
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(Object a, Object b) {
        return a == b || (a == null ? b == null : a.equals(b));
    }
}
