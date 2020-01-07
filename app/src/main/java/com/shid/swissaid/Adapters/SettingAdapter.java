package com.shid.swissaid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shid.swissaid.R;

public class SettingAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Context context;

    //to store icons
    private final Integer[] imageIDarray;

    //to store name of settings
    private final String[] nameArray;

    public SettingAdapter(Context context, String[] nameArrayParam, Integer[]imageIDarray) {
        super(context, R.layout.listview_row_setting, nameArrayParam);
        this.context=context;
        this.imageIDarray = imageIDarray;
        this.nameArray = nameArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = view;

        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.listview_row_setting,null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.text_setting);
        ImageView imageView = rowView.findViewById(R.id.image_setting);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);
        imageView.setImageResource(imageIDarray[position]);

        return rowView;

    };
}
