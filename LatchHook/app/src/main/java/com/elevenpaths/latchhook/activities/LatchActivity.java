package com.elevenpaths.latchhook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchResponse;
import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

public class LatchActivity extends Activity {

    private boolean started = false;

    private String getInputAppid(){
        return ((EditText)LatchActivity.this.findViewById(R.id.editTextAppid)).getText().toString();
    }

    private String getInputSecret(){
        return ((EditText)LatchActivity.this.findViewById(R.id.editTextSecret)).getText().toString();
    }

    class AsyncPair extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean result = false;

            String appid = getInputAppid();
            String secret = getInputSecret();

            if(appid != null && !appid.isEmpty() && secret != null && !secret.isEmpty()) {
                Latch latch = new Latch(appid, secret);
                LatchResponse response = latch.pair(((EditText) LatchActivity.this.findViewById(R.id.editTextToken)).getText().toString());
                try {
                    String accountid = response.getData() != null && response.getData().has("accountId") ? response.getData().get("accountId").getAsString() : null;
                    if (accountid != null && !accountid.isEmpty()) {
                        ConfigurationManager.setStringPreference(LatchActivity.this, ConfigurationManager.PREFERENCE_ACCOUNT, accountid);
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                // store the values of the appid and secret
                ConfigurationManager.setStringPreference(LatchActivity.this, ConfigurationManager.PREFERENCE_APPID, getInputAppid());
                ConfigurationManager.setStringPreference(LatchActivity.this, ConfigurationManager.PREFERENCE_SECRET, getInputSecret());
                LatchActivity.this.startActivity(new Intent(LatchActivity.this, AccountManagerActivity.class));
            }else{
                Toast.makeText(LatchActivity.this, "Invalid Appid, secret or pair code", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latch);

        //get the appid
        String appid = ConfigurationManager.getStringPreference(this, ConfigurationManager.PREFERENCE_APPID);
        if(appid == null){
            appid = ConfigurationManager.APPID;
        }
        if(appid != null) {
            ((EditText) this.findViewById(R.id.editTextAppid)).setText(appid);
        }

        //get the secret
        String secret = ConfigurationManager.getStringPreference(this, ConfigurationManager.PREFERENCE_SECRET);
        if(secret == null){
            secret = ConfigurationManager.SECRET;
        }
        if(secret != null){
            ((EditText)this.findViewById(R.id.editTextSecret)).setText(secret);
        }

        ((Button)this.findViewById(R.id.buttonPair)).setOnClickListener(this.buttonClickPair());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.started){
            startActivity(new Intent(this, PasswordActivity.class));
        }else{
            this.started = true;
        }
    }

    private View.OnClickListener buttonClickPair() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncPair().execute();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_latch, menu);
        return true;
    }
}
