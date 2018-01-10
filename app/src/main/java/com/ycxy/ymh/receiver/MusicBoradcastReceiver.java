package com.ycxy.ymh.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

/**
 * Created by Y&MH on 2018-1-10.
 */

public class MusicBoradcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                //Bluetooth headset is now disconnected
                // handleHeadsetDisconnected();
                onHEADSET_plugoutListener.setOnHEADSET_PLUGOUTListener();
            }
        } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
            if (intent.hasExtra("state")) {
                int state = intent.getIntExtra("state", 0);
                if (state == 1) {
                    onHEADSET_pluginListener.setOnHEADSET_PLUGINListener();
                }
                if (state == 0) {
                    onHEADSET_plugoutListener.setOnHEADSET_PLUGOUTListener();
                }
            }
        }
    }

    /**
     * 耳机插入事件监听
     */
    private OnHEADSET_PLUGINListener onHEADSET_pluginListener;

    public void setOnHEADSET_PLUGINListener(OnHEADSET_PLUGINListener onHEADSET_pluginListener) {
        this.onHEADSET_pluginListener = onHEADSET_pluginListener;
    }

    public interface OnHEADSET_PLUGINListener {
        void setOnHEADSET_PLUGINListener();
    }

    /**
     * 耳机拔出事件监听
     */
    private OnHEADSET_PLUGOUTListener onHEADSET_plugoutListener;

    public void setOnHEADSET_PLUGOUTListener(OnHEADSET_PLUGOUTListener onHEADSET_plugoutListener) {
        this.onHEADSET_plugoutListener = onHEADSET_plugoutListener;
    }

    public interface OnHEADSET_PLUGOUTListener {
        void setOnHEADSET_PLUGOUTListener();
    }
}
