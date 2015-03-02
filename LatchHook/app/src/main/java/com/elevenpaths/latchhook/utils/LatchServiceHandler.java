package com.elevenpaths.latchhook.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.elevenpaths.latchhook.services.LatchService;

public class LatchServiceHandler {

    private Context context;
    private Messenger serviceResponseHandler;
    private ServiceConnection serviceConnection;
    private boolean isBound;

    public void setServiceResponseHandler(Messenger serviceResponseHandler) {
        this.serviceResponseHandler = serviceResponseHandler;
    }

    public LatchServiceHandler(Context context) {
        this.context = context;
        this.serviceResponseHandler = null;
        this.serviceConnection = null;
        this.isBound = false;
    }

    public void queryLatchService(final Intent intent){

        if(serviceResponseHandler != null && !this.isBound) {

            this.serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    Messenger messenger = new Messenger(service);
                    try {
                        Message msg = Message.obtain();
                        msg.replyTo = LatchServiceHandler.this.serviceResponseHandler;

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(LatchService.PARAM_INTENT, intent);
                        msg.setData(bundle);

                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                public void onServiceDisconnected(ComponentName className) {
                }
            };

            this.context.bindService(new Intent(ConfigurationManager.LATCH_SERVICE_ACTION), this.serviceConnection, Context.BIND_AUTO_CREATE);
            this.isBound = true;
        }

    }

    public void unbound(){
        if(this.isBound) {
            this.context.unbindService(serviceConnection);
            this.isBound = false;
        }
    }

}
