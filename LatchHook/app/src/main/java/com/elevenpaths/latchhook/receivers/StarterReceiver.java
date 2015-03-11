package com.elevenpaths.latchhook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elevenpaths.latchhook.services.AppMonitorService;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

public class StarterReceiver extends BroadcastReceiver {

    public StarterReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {

        //The monitor service only will be started if the Substrate hooks are disabled
        boolean useHooks = ConfigurationManager.getBooleanPreference(context, ConfigurationManager.PREFERENCE_HOOKS_ENABLED, false);
        if(!useHooks) {
            context.startService(new Intent(context, AppMonitorService.class));
        }
    }

    /**
     * Broadcast and intent that will be captured by this BroadcastReceiver
     *
     * @param context
     */
    public static void startWatcher(Context context){
        Intent broadcastIntent = new Intent(ConfigurationManager.WATCHER_ACTION);
        context.sendBroadcast(broadcastIntent);
    }
}
