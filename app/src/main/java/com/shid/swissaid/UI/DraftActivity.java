package com.shid.swissaid.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shid.swissaid.R;
import com.shid.swissaid.UI.ui.draft.DraftFragment;

public class DraftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DraftFragment.newInstance())
                    .commitNow();
        }
    }
}
