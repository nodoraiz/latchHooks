package com.elevenpaths.latchhook.utils;

import android.os.Handler;
import android.os.Message;

public abstract class MessageHandler extends Handler {

    public abstract void handleMessage(Message msg);

}
