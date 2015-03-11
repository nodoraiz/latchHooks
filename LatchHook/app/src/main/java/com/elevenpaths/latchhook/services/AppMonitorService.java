package com.elevenpaths.latchhook.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.elevenpaths.latchhook.activities.UnauthorizedAction;
import com.elevenpaths.latchhook.utils.ConfigurationManager;
import com.elevenpaths.latchhook.utils.LatchWrapper;

import java.util.Observable;
import java.util.Observer;

public class AppMonitorService extends Service {

    private static Thread thread;

    public AppMonitorService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // stop previous running thread
        if (this.thread != null) {
            this.thread.interrupt();
        }

        // start monit thread
        this.thread = new ThreadAppMonithor();
        this.thread.start();

        return Service.START_STICKY;
    }

    private class ThreadAppMonithor extends Thread {

        private String lastPackageName;

        public ThreadAppMonithor(){
            this.lastPackageName = "";
        }

        @Override
        public void run() {

            int milisBetweenChecks = ConfigurationManager.getIntPreference(AppMonitorService.this, ConfigurationManager.PREFERENCE_MILIS_BETWEEN_CHECKS_IN_THREAD_MONITOR, 100);
            while(!this.isInterrupted() ) {
                try {
                    // get the name of the packageName running in foreground
                    ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo runningTaskInfo = activityManager.getRunningTasks(1).get(0);
                    String packageName = runningTaskInfo.topActivity.getPackageName();

                    // check Latch when a new app goes to foreground
                    if (!lastPackageName.equals(packageName)) {
                        lastPackageName = packageName;
                        this.checkLatchOnPackageName(packageName);
                    }

                    // if the hooks are enabled then the loop is broken and the thread monitor finished
                    if( ConfigurationManager.getBooleanPreference(AppMonitorService.this, ConfigurationManager.PREFERENCE_HOOKS_ENABLED, false) ) {
                        break;
                    }

                    // sleep before the next check
                    Thread.sleep(milisBetweenChecks);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        private void checkLatchOnPackageName(String packageName){

            // create a virtual intent based on the detected app running in foreground
            Intent appDetectedIntent = new Intent();
            appDetectedIntent.setComponent(new ComponentName(packageName, ""));

            boolean latchOpen = LatchWrapper.checkLatchOpenForIntent(AppMonitorService.this, appDetectedIntent);
            if (!latchOpen) {
                // if the latch is closed for the app then the UnauthorizedAction activity is started
                //  to take the foreground and don't letting the user to use it
                Intent unauthIntent = new Intent(AppMonitorService.this, UnauthorizedAction.class);
                unauthIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppMonitorService.this.startActivity(unauthIntent);
            }
        }
    }
}
