package com.ycxy.ymh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ycxy.ymh.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class LongRunningReceiver extends BroadcastReceiver {

    private static final String TAG = "LongRunningReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context,"时间到",Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new MessageEvent());

    }
}
