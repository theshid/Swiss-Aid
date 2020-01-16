package com.shid.swissaid.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.shid.swissaid.R;
import com.shid.swissaid.Util.Offline;

import static com.shid.swissaid.Util.LocaleManager.LANGUAGE_ENGLISH;
import static com.shid.swissaid.Util.LocaleManager.LANGUAGE_FRENCH;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference btnChangeLang = getPreferenceManager().findPreference("lang");
            if (btnChangeLang != null){
                btnChangeLang.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showLanguageDialog();
                        return true;
                    }
                });
            }
        }

        private void showLanguageDialog(){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            AlertDialog optionDialog = dialog.create();
            dialog.setTitle(getString(R.string.dialog_title_lg));
            dialog.setMessage(getString(R.string.dialog_message_lg));

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View register_layout = inflater.inflate(R.layout.layout_lg,null);

            final AppCompatImageView france =  register_layout.findViewById(R.id.btn_fr_lg);
            final AppCompatImageView en =  register_layout.findViewById(R.id.btn_en_lg);



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

            Intent i = new Intent(getContext(), SettingsActivity.class);
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            if (restartProcess) {
                System.exit(0);
            } else {
                // Toast.makeText(getContext(), "Activity restarted", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}