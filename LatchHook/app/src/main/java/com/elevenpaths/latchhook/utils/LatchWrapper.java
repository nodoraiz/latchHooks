package com.elevenpaths.latchhook.utils;

import android.content.Context;
import android.content.Intent;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchResponse;

public class LatchWrapper {

    /**
     * Returns TRUE if the user can open the activity. This will be in the next cases:
     * - If the latch is open
     * - If the latch is closed but the intent is not pointing to an observed app
     *
     * @param context
     * @param intent Intent with the packageName
     * @return
     */
    public static boolean checkLatchOpenForIntent(Context context, Intent intent){
        String appid = ConfigurationManager.getStringPreference(context, ConfigurationManager.PREFERENCE_APPID);
        String secret = ConfigurationManager.getStringPreference(context, ConfigurationManager.PREFERENCE_SECRET);
        String accountid = ConfigurationManager.getStringPreference(context, ConfigurationManager.PREFERENCE_ACCOUNT);

        boolean latchOn = true;
        if(accountid != null && !accountid.isEmpty() && ConfigurationManager.isLatcheable(context, intent)) {
            Latch latch = new Latch(appid, secret);
            LatchResponse latchResponse = latch.status(accountid);

            latchOn = (latchResponse.getData().getAsJsonObject("operations").has(appid)
                    && latchResponse.getData().getAsJsonObject("operations").getAsJsonObject(appid).has("status")
                    && latchResponse.getData().getAsJsonObject("operations").getAsJsonObject(appid).get("status").getAsString().equals("on"));
        }

        return latchOn;
    }

}
