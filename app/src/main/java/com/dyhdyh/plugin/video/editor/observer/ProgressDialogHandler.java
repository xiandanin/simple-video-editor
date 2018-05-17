package com.dyhdyh.plugin.video.editor.observer;

import android.content.Context;

import com.dyhdyh.subscriber.handler.LoadingHandler;
import com.dyhdyh.widget.loading.dialog.LoadingDialog;

/**
 * @author dengyuhan
 *         created 2018/5/17 17:35
 */
public class ProgressDialogHandler implements LoadingHandler<Float> {
    protected Context mContext;
    protected LoadingDialog mDialog;


    public ProgressDialogHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void show(Float params) {
        float progress = params == null ? 0f : params.floatValue();
        String message = (int) (progress * 100) + "%";
        if (mDialog == null) {
            mDialog = LoadingDialog.make(mContext);
            mDialog.show();
        }
        mDialog.setMessage(progress <= 0f ? "" : message);
    }

    @Override
    public void cancel() {
        LoadingDialog.cancel();
    }
}
