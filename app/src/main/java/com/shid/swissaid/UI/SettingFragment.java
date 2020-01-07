package com.shid.swissaid.UI;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.shid.swissaid.Adapters.SettingAdapter;
import com.shid.swissaid.R;
import com.shid.swissaid.Util.Offline;

import java.util.Locale;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;


import static com.shid.swissaid.Util.LocaleManager.LANGUAGE_ENGLISH;
import static com.shid.swissaid.Util.LocaleManager.LANGUAGE_FRENCH;


public class SettingFragment extends Fragment {


    private ListView listView;

    private SettingAdapter settingAdapter;

    private String[] setting ;

    private ImageView header_en, header_fr;


    private Integer[] imageArray = {
            R.drawable.baseline_perm_identity_24,
            R.drawable.baseline_lock_24,
            R.drawable.baseline_language_24,
            R.drawable.baseline_power_settings_new_24};





    public SettingFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //java.lang.IllegalStateException: Fragment not attached to Context
        if(isAdded()){
            setting = getResources().getStringArray(R.array.rowSetting);
            settingAdapter = new SettingAdapter(getContext(),setting,imageArray);
        }

        header_en = view.findViewById(R.id.banner_home);
        header_fr = view.findViewById(R.id.banner_home_fr);

        //Display correct header depending on the language
        displayHeader();

        listView = (ListView)view.findViewById(R.id.listView_setting);
        listView.setAdapter(settingAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              switch (position){
                  case 0:
                      Log.d("TAG","position "+position);
                      Intent intent = new Intent(getContext(),ChangeNameActivity.class);
                      startActivity(intent);
                      break;

                  case 1:
                      Log.d("TAG","position "+position);
                      Intent intent1 = new Intent(getContext(),ChangePasswordActivity.class);
                      startActivity(intent1);
                      break;

                  case 2:
                      Log.d("TAG","position "+position);
                      showLanguageDialog();
                      break;

                  case 3:
                      Log.d("TAG","position "+position);
                      //Logout
                      logOut();
                      break;

              }
            }
        });


        return view;
    }

    private void displayHeader(){
        if (Locale.getDefault().getLanguage().contentEquals("en")){
            header_en.setVisibility(View.VISIBLE);
            header_fr.setVisibility(View.GONE);
        } else if(Locale.getDefault().getLanguage().contentEquals("fr")){
            header_fr.setVisibility(View.VISIBLE);
            header_en.setVisibility(View.GONE);
        }
    }

    private void logOut(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        alertDialogBuilder.setTitle(getString(R.string.dialog_log_out_title));

        alertDialogBuilder
                .setMessage(getString(R.string.dialog_log_out_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_log_out_btn_yes), (dialog, id) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent4 = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent4);
                    getActivity().finish();
                })
                .setNegativeButton(getString(R.string.dialog_log_out_btn_no), (dialog, id) -> {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showLanguageDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        AlertDialog optionDialog = dialog.create();
        dialog.setTitle(getString(R.string.dialog_title_lg));
        dialog.setMessage(getString(R.string.dialog_message_lg));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View register_layout = inflater.inflate(R.layout.layout_lg,null);

        final AppCompatImageView france = (AppCompatImageView) register_layout.findViewById(R.id.btn_fr_lg);
        final AppCompatImageView en = (AppCompatImageView) register_layout.findViewById(R.id.btn_en_lg);



        france.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewLocale(LANGUAGE_FRENCH, true);
              optionDialog.dismiss();

            }
        });

        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewLocale(LANGUAGE_ENGLISH, false);

             optionDialog.dismiss();
            }
        });

        dialog.setNegativeButton(getString(R.string.cancel_lg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setView(register_layout);
        dialog.show();

    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        Offline.localeManager.setNewLocale(getContext(), language);

        Intent i = new Intent(getContext(), HomeActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
           // Toast.makeText(getContext(), "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}
