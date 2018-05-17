package com.dyhdyh.plugin.video.editor.ffmpeg;

import java.io.File;

/**
 * @author dengyuhan
 *         created 2018/5/17 16:39
 */
public class FFmpegResult {
    private boolean success;
    private String message;
    private File outputFile;
    private float progress;

    public static FFmpegResult create(boolean success,String message, File outputFile, float progress) {
        FFmpegResult result = new FFmpegResult();
        result.setSuccess(success);
        result.setMessage(message);
        result.setOutputFile(outputFile);
        result.setProgress(progress);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
