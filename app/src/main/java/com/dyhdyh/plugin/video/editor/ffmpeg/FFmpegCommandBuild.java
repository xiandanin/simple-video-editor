package com.dyhdyh.plugin.video.editor.ffmpeg;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author dengyuhan
 *         created 2018/5/17 16:49
 */
public class FFmpegCommandBuild {

    public static String[] cut(long startMillis, long cutDurationMillis, String input, String output) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return log(new String[]{
                "-ss", dateFormat.format(startMillis), "-t", dateFormat.format(cutDurationMillis),
                "-i", input, "-vcodec", "copy", "-acodec", "copy", "-y", output
        });
    }

    public static String[] log(String[] command) {
        StringBuffer sb = new StringBuffer();
        for (String str : command) {
            sb.append(str);
            sb.append(" ");
        }
        Log.d("FFmpegCommand", sb.toString());
        return command;
    }
}
