package com.dyhdyh.plugin.video.editor.view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * @author dengyuhan
 *         created 2018/5/17 20:35
 */
public  abstract class ClickableColorSpan extends ClickableSpan {


    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.BLUE);
        ds.setUnderlineText(true);
    }
}