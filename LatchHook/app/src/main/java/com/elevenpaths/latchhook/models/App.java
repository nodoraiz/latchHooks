package com.elevenpaths.latchhook.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class App {

    private String packageName;
    private boolean selected;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public App(String packageName, boolean selected) {
        this.packageName = packageName;
        this.selected = selected;
    }

    public static void sort(ArrayList<App> apps) {
        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App lhs, App rhs) {
                return lhs.getPackageName().compareTo(rhs.getPackageName());
            }
        });
    }
}
