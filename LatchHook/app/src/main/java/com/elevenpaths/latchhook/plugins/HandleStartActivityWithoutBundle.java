package com.elevenpaths.latchhook.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;

import com.elevenpaths.latchhook.plugins.base.Plugin;
import com.elevenpaths.latchhook.services.LatchService;
import com.elevenpaths.latchhook.utils.ConfigurationManager;
import com.elevenpaths.latchhook.utils.LatchServiceHandler;
import com.elevenpaths.latchhook.utils.MessageHandler;
import com.saurik.substrate.MS;

import java.lang.reflect.Method;

public class HandleStartActivityWithoutBundle extends Plugin {

    @Override
    public String getPluginName() {
        return HandleStartActivityWithoutBundle.class.getName();
    }

    @Override
    public String getClassNameToHook() {
        return "android.app.Activity";
    }

    @Override
    public Method getMethodNameToHook(Class hookedClass) throws NoSuchMethodException {
        return hookedClass.getMethod("startActivity", Intent.class);
    }

    @Override
    public Object modifyAction(MS.MethodAlteration hookedMethod, Object capturedInstance, Object... args) throws Throwable {

        final MS.MethodAlteration finalMethod = hookedMethod; // startActivity method
        final Context finalContext = (Context) capturedInstance; // instance of android.app.Activity
        final Intent finalIntent = (Intent) args[0]; // instance of Intent passed to the startActivity method of Activity

        final LatchServiceHandler latchServiceHandler = new LatchServiceHandler(finalContext.getApplicationContext());
        Messenger serviceResponseHandler = new Messenger(new MessageHandler() {
            @Override
            public void handleMessage(Message msg) {
                boolean latchOpen = msg.getData().getBoolean(LatchService.PARAM_OPEN);
                try {
                    if (!latchOpen) {
                        Intent intent = new Intent();
                        intent.setClassName(ConfigurationManager.PACKAGE_NAME, ConfigurationManager.UNATHORIZED_ACTION_ACTIVITY_NAME);
                        finalContext.startActivity(intent);
                    } else {
                        finalMethod.invoke(finalContext, finalIntent);
                    }
                }catch (Throwable t){
                    t.printStackTrace();
                }
                latchServiceHandler.unbound();
            }
        });
        latchServiceHandler.setServiceResponseHandler(serviceResponseHandler);
        latchServiceHandler.queryLatchService((Intent)args[0]); // ask to LatchService for the status, the response will be handled by the serviceResponseHandler
        return null; // now the startActivity asked for the Launcher can be dropped
    }
}
