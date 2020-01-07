package com.shid.swissaid.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.shid.swissaid.Adapters.HomeAdapter;
import com.shid.swissaid.R;

import java.util.Locale;

import androidx.fragment.app.Fragment;


public class ReportFragment extends Fragment {


    private ListView listView;
    private ImageView header_en, header_fr;
    //Adapter to load data into recycle view
    private HomeAdapter homeAdapter;
    private String[] menu;


    public ReportFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report, container, false);
        //getActivity().setTitle(getString(R.string.unicef_mobile));

        if (isAdded()) {
            menu = getResources().getStringArray(R.array.template);
            homeAdapter = new HomeAdapter(getContext(), menu);
        }

        listView = view.findViewById(R.id.listView_report);
        listView.setAdapter(homeAdapter);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.d("TAG", "position " + position);
                        Intent intent = new Intent(getContext(), ModelActivity.class);
                        startActivity(intent);
                        break;

                    case slide1:
                        Log.d("TAG", "position " + position);
                        Intent intent1 = new Intent(getContext(), ModelActivity.class);
                        startActivity(intent1);
                        break;


                }
            }
        });

        */

        header_en = view.findViewById(R.id.banner_home);
        header_fr = view.findViewById(R.id.banner_home_fr);

        //Display correct header depending on the language
        displayHeader();
        return view;
    }


    private void displayHeader() {
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            header_en.setVisibility(View.VISIBLE);
            header_fr.setVisibility(View.GONE);
        } else if (Locale.getDefault().getLanguage().contentEquals("fr")) {
            header_fr.setVisibility(View.VISIBLE);
            header_en.setVisibility(View.GONE);
        }
    }

}
