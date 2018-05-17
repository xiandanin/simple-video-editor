package com.dyhdyh.plugin.video.editor.utils;

/**
 * @author dengyuhan
 *         created 2018/5/17 17:28
 */
public class FileUtils {

    public static String getFileNameNoEx(String filename) {
        try {
            if ((filename != null) && (filename.length() > 0)) {
                int dot = filename.lastIndexOf('.');
                if ((dot > -1) && (dot < (filename.length()))) {
                    return filename.substring(0, dot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
}
