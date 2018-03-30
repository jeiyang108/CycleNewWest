package com.project.lyt.cyclenewwest.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.lyt.cyclenewwest.Model.GreenWay;
import com.project.lyt.cyclenewwest.R;

import java.util.ArrayList;

/**
 * ArrayAdapter that turns a list of GreenWays into a ListView control
 * displayed to the user on the TrailsActivity.
 */
public class TrailAdapter extends ArrayAdapter<GreenWay> {
    Context _context;

    public TrailAdapter(Context context, ArrayList<GreenWay> greenWays) {
        super(context, 0, greenWays);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Get the data item for this position
        GreenWay greenWay = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_layout, parent, false);
        }

        TextView textName = (TextView) convertView.findViewById(R.id.itemName);
        TextView textLength = (TextView) convertView.findViewById(R.id.itemLength);

        textName.setText(greenWay.getFullName());
        textLength.setText(greenWay.getKM() + "km");

        // Return the completed view to render on screen
        return convertView;
    }


}