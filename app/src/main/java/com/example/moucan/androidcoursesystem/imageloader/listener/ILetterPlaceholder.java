package com.example.moucan.androidcoursesystem.imageloader.listener;

import androidx.annotation.ColorInt;

/**
 * @author xxZhu
 * @class LetterPlaceholder
 * @describe 需要
 */
public interface ILetterPlaceholder {
    int[] PLACEHOLDER_COLOR = new int[]{0xff94B7D9, 0xff9BBD99, 0xffCFBB61, 0xffD4A355, 0xffD59679, 0xffD29292};

    void fillColorRandom();

    @ColorInt
    int getPlaceholderColor();

}
