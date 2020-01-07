package com.shid.swissaid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.shid.swissaid.R;

import androidx.annotation.NonNull;

public class HomeAdapter extends ArrayAdapter {

    private String[] nameArray;
    private Context context;

    public HomeAdapter(Context context,String[] name){
        super(context, R.layout.home_list,name);
        this.nameArray = name;
        this.context = context;
    }

    public HomeAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public View getView(int position, View view, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = view;

        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.home_list,null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.textview_home);


        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);

        return rowView;

    };
}
