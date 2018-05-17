package com.dyhdyh.plugin.video.editor.ffmpeg;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.ObservableEmitter;

/**
 * @author dengyuhan
 *         created 2018/5/17 15:46
 */
public class RxJavaFFmpegExecuteResponseHandler implements FFmpegExecuteResponseHandler {
    private static final String TAG = "RxJavaFFmpegExecute";

    private Pattern pattern = Pattern.compile("time=([\\d\\w:]{8}[\\w.][\\d]+)");
    private ObservableEmitter<FFmpegResult> mEmitter;
    private File mOutputFile;
    private long mDuration;

    public RxJavaFFmpegExecuteResponseHandler(File outputFile, long duration, ObservableEmitter<FFmpegResult> emitter) {
        this.mOutputFile = outputFile;
        this.mEmitter = emitter;
        this.mDuration = duration;
    }

    @Override
    public void onSuccess(String message) {
        mEmitter.onNext(FFmpegResult.create(true, message, mOutputFile, 1f));
    }

    @Override
    public void onProgress(String message) {
        mEmitter.onNext(FFmpegResult.create(false, message, mOutputFile, getProgress(message, mDuration)));
    }

    @Override
    public void onFailure(String message) {
        mEmitter.onError(new FFmpegExecuteException(message));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {
        mEmitter.onComplete();
    }

    private float getProgress(String message, long duration) {
        try {
            if (message.contains("speed")) {
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()){
                    String tempTime = String.valueOf(matcher.group(1));
                    Log.d(TAG, "getProgress: tempTime " + tempTime);
                    String[] arrayTime = tempTime.split("[:|.]");
                    long currentTime =
                            TimeUnit.HOURS.toMillis(Long.parseLong(arrayTime[0]))
                                    + TimeUnit.MINUTES.toMillis(Long.parseLong(arrayTime[1]))
                                    + TimeUnit.SECONDS.toMillis(Long.parseLong(arrayTime[2]))
                                    + Long.parseLong(arrayTime[3]);

                    float percent = (float) currentTime / duration;


                    Log.d(TAG, "currentTime -> " + currentTime + "s % -> " + percent);

                    return percent;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0f;
    }
}
