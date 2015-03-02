package com.elevenpaths.latchhook.activities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.models.App;
import com.elevenpaths.latchhook.utils.ConfigurationManager;

import java.util.ArrayList;

public class AppsAdapter extends ArrayAdapter<App> {

    public AppsAdapter(Context context, ArrayList<App> apps) {
        super(context, 0, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        App app = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_item, parent, false);
        }
        // Lookup view for data population
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.layoutAppItem);
        TextView textView = (TextView) convertView.findViewById(R.id.textAppItem);
        // Populate the data into the template view using the data object
        textView.setText(app.getPackageName());
        if(app.isSelected()) {
            linearLayout.setBackgroundColor(ConfigurationManager.getColorGreen(this.getContext()));
//            linearLayout.invalidate();
        } else {
            linearLayout.setBackground(null);
//            linearLayout.invalidate();
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
