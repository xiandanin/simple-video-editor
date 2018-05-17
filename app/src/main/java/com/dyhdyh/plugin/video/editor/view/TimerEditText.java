package com.dyhdyh.plugin.video.editor.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.dyhdyh.plugin.video.editor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author dengyuhan
 *         created 2018/5/17 16:58
 */
public class TimerEditText extends RelativeLayout {
    private final int[] TIME_IDS = new int[]{
            R.id.ed_start_hour,
            R.id.ed_start_minute,
            R.id.ed_start_second,
            R.id.ed_end_hour,
            R.id.ed_end_minute,
            R.id.ed_end_second,
    };
    EditText[] mEditTimeText = new EditText[TIME_IDS.length];
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public TimerEditText(Context context) {
        this(context, null);
    }

    public TimerEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        View.inflate(context, R.layout.layout_timer_edit, this);
        int count = TIME_IDS.length;
        for (int i = 0; i < count; i++) {
            mEditTimeText[i] = findViewById(TIME_IDS[i]);
            final int index = i;
            mEditTimeText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("------->", s + "-->beforeTextChanged");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("------->", s + "-->onTextChanged");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("------->", s + "-->afterTextChanged");
                    final int length = s.length();
                    if (length == 2) {
                        mEditTimeText[index].clearFocus();
                        int next = index + 1;
                        if (next < length) {
                            mEditTimeText[next].requestFocus();
                        }
                    }
                }
            });
        }
    }

    public void clear() {
        for (int i = 0; i < mEditTimeText.length; i++) {
            mEditTimeText[i].setText("");
        }
    }

    public long getStartTime() {
        try {
            return format.parse(getFormatStartTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getEndTime() {
        try {
            return format.parse(getFormatEndTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getFormatStartTime() {
        return String.format("%s:%s:%s", getEditTimeText(0), getEditTimeText(1), getEditTimeText(2));
    }

    public String getFormatEndTime() {
        return String.format("%s:%s:%s", getEditTimeText(3), getEditTimeText(4), getEditTimeText(5));
    }


    private String getEditTimeText(int index) {
        final Editable text = mEditTimeText[index].getText();
        return TextUtils.isEmpty(text) ? "00" : text.length() == 1 ? "0" + text : text.toString();
    }

}
