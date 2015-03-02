package com.elevenpaths.latchhook;

import com.elevenpaths.latchhook.plugins.*;

public class PluginManager {

    static void initialize(){

        new HandleStartActivityForResultWithoutBundle().apply();
        new HandleStartActivityForResultWithBundle().apply();
        new HandleStartActivityWithoutBundle().apply();
        new HandleStartActivityWithBundle().apply();

    }

}
