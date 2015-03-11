package com.elevenpaths.latchhook.activities.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.receivers.StarterReceiver;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

public class HookModeDialog extends Dialog {

    public HookModeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_hook_mode);

        this.setTitle(R.string.actAm_ObserverMode);

        // add UI events
        ((RadioGroup)this.findViewById(R.id.radioGroupHookMethod)).setOnCheckedChangeListener(this.radioChecked());
        ((Button) this.findViewById(R.id.buttonOk)).setOnClickListener(this.clickButtonOkCancel());
        ((Button) this.findViewById(R.id.buttonCancel)).setOnClickListener(this.clickButtonOkCancel());

        // set the radio option based on the previous configuration
        boolean useHooks = ConfigurationManager.getBooleanPreference(this.getContext(), ConfigurationManager.PREFERENCE_HOOKS_ENABLED, false);
        if(useHooks) {
            ((RadioButton) this.findViewById(R.id.radioHook)).setChecked(true);
        } else {
            ((RadioButton) this.findViewById(R.id.radioThread)).setChecked(true);
        }

        // set the time between checks in the monitor thread
        Integer milisBetweenChecks = ConfigurationManager.getIntPreference(this.getContext(), ConfigurationManager.PREFERENCE_MILIS_BETWEEN_CHECKS_IN_THREAD_MONITOR, 100);
        ((EditText) this.findViewById(R.id.editTextMilis)).setText(milisBetweenChecks.toString());
    }

    private RadioGroup.OnCheckedChangeListener radioChecked() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // show or hide the thread sleep miliseconds configuration based on the radio option selected
                if(checkedId == R.id.radioHook) {
                    ((LinearLayout)HookModeDialog.this.findViewById(R.id.layoutMilis)).setVisibility(View.GONE);
                } else if (checkedId == R.id.radioThread) {
                    ((LinearLayout)HookModeDialog.this.findViewById(R.id.layoutMilis)).setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private View.OnClickListener clickButtonOkCancel() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.buttonOk) {
                    if (((RadioButton) HookModeDialog.this.findViewById(R.id.radioHook)).isChecked()) {
                        ConfigurationManager.setBooleanPreference(HookModeDialog.this.getContext(), ConfigurationManager.PREFERENCE_HOOKS_ENABLED, true);
                    } else {
                        int milisBetweenChecks = 100;
                        try {
                            milisBetweenChecks = Integer.parseInt(((EditText) HookModeDialog.this.findViewById(R.id.editTextMilis)).getText().toString());
                        }catch (Exception e){ }

                        ConfigurationManager.setIntPreference(HookModeDialog.this.getContext(), ConfigurationManager.PREFERENCE_MILIS_BETWEEN_CHECKS_IN_THREAD_MONITOR, milisBetweenChecks);
                        ConfigurationManager.setBooleanPreference(HookModeDialog.this.getContext(), ConfigurationManager.PREFERENCE_HOOKS_ENABLED, false);
                        StarterReceiver.startWatcher(HookModeDialog.this.getContext());
                    }
                }

                HookModeDialog.this.dismiss();
            }
        };
    }
}
