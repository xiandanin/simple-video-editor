package com.dyhdyh.plugin.video.editor.ffmpeg;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author dengyuhan
 *         created 2018/5/17 15:45
 */
public class RxFFmpeg {
    private static boolean loadBinary;

    public static Observable<FFmpegResult> execute(final Context context, final String[] cmd, final File outputFile, final long duration) {
        final FFmpeg ffmpeg = FFmpeg.getInstance(context);
        return Observable.create(new ObservableOnSubscribe<FFmpegResult>() {
            @Override
            public void subscribe(ObservableEmitter<FFmpegResult> emitter) throws Exception {
                try {
                    if (!loadBinary) {
                        ffmpeg.loadBinary(new SimpleFFmpegLoadBinaryResponseHandler() {

                            @Override
                            public void onSuccess() {
                                loadBinary = true;
                            }
                        });
                    }
                    ffmpeg.execute(cmd, new RxJavaFFmpegExecuteResponseHandler(outputFile, duration, emitter));
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        })
                .observeOn(AndroidSchedulers.mainThread());


    }
}
