package com.elevenpaths.latchhook.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchResponse;
import com.elevenpaths.latchhook.utils.ConfigurationManager;
import com.elevenpaths.latchhook.utils.LatchWrapper;
import com.elevenpaths.latchhook.utils.MessageHandler;

public class LatchService extends Service {

    public static final String PARAM_INTENT = "intent";
    public static final String PARAM_OPEN = "open";

    public LatchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(this.createHandler()).getBinder();
    }

    private Messenger replyToMessenger;
    private Handler createHandler(){
        return new MessageHandler(){
            @Override
            public void handleMessage(Message msg) {
                replyToMessenger = msg.replyTo;
                Intent intent = (Intent) msg.getData().getParcelable(PARAM_INTENT);
                new LatchStatusChecker().execute(intent);
            }
        };
    }

    class LatchStatusChecker extends AsyncTask<Intent, Void, Void>{

        @Override
        protected Void doInBackground(Intent... params) {

            boolean latchOpen = true;
            boolean useHooks = ConfigurationManager.getBooleanPreference(LatchService.this, ConfigurationManager.PREFERENCE_HOOKS_ENABLED, false);
            if(useHooks){
                latchOpen = LatchWrapper.checkLatchOpenForIntent(LatchService.this, params[0]);
            }

            Message response = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putBoolean(PARAM_OPEN, latchOpen);
            response.setData(bundle);

            try {
                replyToMessenger.send(response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}