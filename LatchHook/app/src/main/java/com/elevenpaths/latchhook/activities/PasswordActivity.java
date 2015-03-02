package com.elevenpaths.latchhook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

public class PasswordActivity extends Activity {

    public final static String EXTRA_UPDATE_PWD = "update_pwd";

    private String password;
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // get the password if its defined
        this.password = ConfigurationManager.getStringPreference(this, ConfigurationManager.PREFERENCE_PWD);

        if(this.getIntent().hasExtra(EXTRA_UPDATE_PWD)){
            // the user is updating the password
            this.update = true;
            ((LinearLayout)this.findViewById(R.id.layoutOldPwd)).setVisibility(View.VISIBLE);
            ((LinearLayout)this.findViewById(R.id.layoutRepeatPwd)).setVisibility(View.VISIBLE);

        }else if (this.password == null) {
            // first access of the user, there is no password defined
            ((LinearLayout) this.findViewById(R.id.layoutRepeatPwd)).setVisibility(View.VISIBLE);
        }

        ((Button)this.findViewById(R.id.buttonOk)).setOnClickListener(this.buttonClickOk());
    }

    private View.OnClickListener buttonClickOk() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PasswordActivity.this.password != null && !PasswordActivity.this.update) {
                    // validate pwd
                    String inputPassword = ((EditText)PasswordActivity.this.findViewById(R.id.editPwd)).getText().toString();
                    if(PasswordActivity.this.password.equals(inputPassword)){
                        PasswordActivity.this.goToNext();
                    } else {
                        Toast.makeText(PasswordActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                    }

                } else if(PasswordActivity.this.password != null && PasswordActivity.this.update){
                    //change pwd
                    String pwd0 = ((EditText)PasswordActivity.this.findViewById(R.id.editOldPwd)).getText().toString();
                    if(!pwd0.equals(PasswordActivity.this.password)){
                        Toast.makeText(PasswordActivity.this, "Invalid previous password", Toast.LENGTH_LONG).show();

                    } else {
                        String pwd1 = ((EditText) PasswordActivity.this.findViewById(R.id.editPwd)).getText().toString();
                        String pwd2 = ((EditText) PasswordActivity.this.findViewById(R.id.editRepeatPwd)).getText().toString();
                        if (!pwd1.isEmpty() && pwd1.equals(pwd2)) {
                            ConfigurationManager.setStringPreference(PasswordActivity.this, ConfigurationManager.PREFERENCE_PWD, pwd1);
                            PasswordActivity.this.goToNext();
                        } else {
                            Toast.makeText(PasswordActivity.this, "Empty or not matching password", Toast.LENGTH_LONG).show();
                        }
                    }

                } else if(PasswordActivity.this.password == null) {
                    // save pwd
                    String pwd1 = ((EditText)PasswordActivity.this.findViewById(R.id.editPwd)).getText().toString();
                    String pwd2 = ((EditText)PasswordActivity.this.findViewById(R.id.editRepeatPwd)).getText().toString();
                    if(!pwd1.isEmpty() && pwd1.equals(pwd2)){
                        ConfigurationManager.setStringPreference(PasswordActivity.this, ConfigurationManager.PREFERENCE_PWD, pwd1);
                        PasswordActivity.this.goToNext();
                    } else {
                        Toast.makeText(PasswordActivity.this, "Empty or not matching password", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Log.e(PasswordActivity.class.getName(), "Not expected case");
                }
            }
        };
    }

    private void goToNext(){
        String accountid = ConfigurationManager.getStringPreference(this, ConfigurationManager.PREFERENCE_ACCOUNT);
        if(accountid == null){
            this.startActivity(new Intent(this, LatchActivity.class));
        } else {
            this.startActivity(new Intent(this, AccountManagerActivity.class));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password, menu);
        return true;
    }
}
