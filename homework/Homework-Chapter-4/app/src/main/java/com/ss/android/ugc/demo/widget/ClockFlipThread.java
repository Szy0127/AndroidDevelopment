package com.ss.android.ugc.demo.widget;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class ClockFlipThread extends HandlerThread implements Handler.Callback {


    private Handler mHandler;
    private Clock clock;
    private Rect dirty = new Rect(0,0,100,100);
    public ClockFlipThread(Clock clocki) {
        super(String.valueOf(clocki));
        clock = clocki;
    }

    @Override protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper(), this);
        //首次请求
        mHandler.sendEmptyMessage(1);
    }

    @Override public boolean handleMessage(@NonNull Message msg) {

            clock.invalidate(dirty);
            mHandler.sendEmptyMessageDelayed(1,1000);

        return true;
    }
}