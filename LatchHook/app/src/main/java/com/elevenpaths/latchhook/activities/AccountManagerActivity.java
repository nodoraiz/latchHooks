package com.elevenpaths.latchhook.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchResponse;
import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.activities.adapters.AppsAdapter;
import com.elevenpaths.latchhook.models.App;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountManagerActivity extends Activity {

    private boolean started = false;

    class AsyncUnPair extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean result = false;

            String appid = ConfigurationManager.getStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_APPID);
            String secret = ConfigurationManager.getStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_SECRET);
            String account = ConfigurationManager.getStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_ACCOUNT);

            if(appid != null && !appid.isEmpty() && secret != null && !secret.isEmpty() && account != null && !account.isEmpty()) {
                Latch latch = new Latch(appid, secret);
                latch.unpair(account);
                ConfigurationManager.setStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_APPID, null);
                ConfigurationManager.setStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_SECRET, null);
                ConfigurationManager.setStringPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_ACCOUNT, null);
                result = true;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                AccountManagerActivity.this.startActivity(new Intent(AccountManagerActivity.this, LatchActivity.class));
            }else{
                Toast.makeText(AccountManagerActivity.this, "There was a problem with the unpair, try it again later.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        ((Button)this.findViewById(R.id.buttonUnpair)).setOnClickListener(this.buttonClickUnpair());
        ((Button)this.findViewById(R.id.buttonChangePwd)).setOnClickListener(this.buttonClickChangePwd());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.started){
            this.startActivity(new Intent(this, PasswordActivity.class));
        } else {
            this.started = true;
            this.loadApps();
        }
    }

    private Set<String> selectedApps;

    private void loadApps() {
        this.selectedApps = (Set<String>)ConfigurationManager.getObjectPreference(this, ConfigurationManager.PREFERENCE_LATCHED_ACTIVITIES, Set.class);
        if(this.selectedApps == null){
            this.selectedApps = new HashSet<String>();
        }

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = this.getPackageManager().queryIntentActivities(intent, 0);

        ArrayList<App> apps = new ArrayList<>(resolveInfoList.size());
        String packageName;
        for(ResolveInfo resolveInfo : resolveInfoList){
            packageName = resolveInfo.activityInfo.packageName;
            apps.add(new App(packageName, this.selectedApps.contains(packageName)));
        }

        App.sort(apps);

        AppsAdapter adapter = new AppsAdapter(this, apps);
        ListView listView = (ListView)this.findViewById(R.id.listAppItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this.itemListClick());
    }

    private AdapterView.OnItemClickListener itemListClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App app = (App) parent.getItemAtPosition(position);
                if(AccountManagerActivity.this.selectedApps.contains(app.getPackageName())){
                    AccountManagerActivity.this.selectedApps.remove(app.getPackageName());
                    view.setBackground(null);
                } else {
                    AccountManagerActivity.this.selectedApps.add(app.getPackageName());
                    view.setBackgroundColor(ConfigurationManager.getColorGreen(AccountManagerActivity.this));
                }
                app.setSelected(!app.isSelected());

                ConfigurationManager.setObjectPreference(AccountManagerActivity.this, ConfigurationManager.PREFERENCE_LATCHED_ACTIVITIES, AccountManagerActivity.this.selectedApps);
            }
        };
    }

    private View.OnClickListener buttonClickChangePwd() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManagerActivity.this, PasswordActivity.class);
                intent.putExtra(PasswordActivity.EXTRA_UPDATE_PWD, true);
                AccountManagerActivity.this.startActivity(intent);
            }
        };
    }

    private View.OnClickListener buttonClickUnpair() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncUnPair().execute();
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_manager, menu);
        return true;
    }
}
