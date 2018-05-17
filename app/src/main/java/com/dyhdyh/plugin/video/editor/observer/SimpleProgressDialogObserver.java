package com.dyhdyh.plugin.video.editor.observer;

import android.content.Context;

import com.dyhdyh.plugin.video.editor.ffmpeg.FFmpegResult;
import com.dyhdyh.subscriber.handler.LoadingHandler;
import com.dyhdyh.subscriber.rxjava2.observer.BaseObserver;
import com.dyhdyh.subscribers.loadingbar.handler.SimpleToastErrorHandler;

/**
 * @author dengyuhan
 *         created 2018/5/17 17:33
 */
public class SimpleProgressDialogObserver extends BaseObserver<FFmpegResult, Float, CharSequence> {


    public SimpleProgressDialogObserver(Context context) {
        super(new ProgressDialogHandler(context), new SimpleToastErrorHandler(context, "处理失败"));
    }

    @Override
    public void onNext(FFmpegResult result) {
        LoadingHandler<Float> handler = getLoadingHandler();
        if (result.isSuccess()) {
            super.onNext(result);
        } else {
            if (handler != null) {
                handler.show(result.getProgress());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        e.printStackTrace();
    }
}
