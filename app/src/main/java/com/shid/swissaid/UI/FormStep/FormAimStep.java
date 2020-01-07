package com.shid.swissaid.UI.FormStep;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.shid.swissaid.R;

import androidx.annotation.NonNull;
import ernestoyaquello.com.verticalstepperform.Step;

public class FormAimStep extends Step<String> {

    private static final int MIN_CHARACTERS_NAME = 3;

    private TextInputEditText aimEditText;
    private String unformattedErrorString;

    public FormAimStep(String title) {
        this(title, "");
    }

    public FormAimStep(String title, String subtitle) {
        super(title, subtitle);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view programmatically
        aimEditText = new TextInputEditText(getContext());
        aimEditText.setMinLines(2);
        aimEditText.setHint(R.string.objectif);
        aimEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        aimEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getFormView().goToNextStep(true);
                return false;
            }
        });

        unformattedErrorString = getContext().getResources().getString(R.string.form_min_name_char);

        return aimEditText;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    public String getStepData() {
        Editable text = aimEditText.getText();
        if (text != null) {
            return text.toString();
        }

        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String aim = getStepData();
        return aim == null || aim.isEmpty()
                ? getContext().getString(R.string.form_empty_field)
                : aim;
    }

    @Override
    public void restoreStepData(String data) {
        if (aimEditText != null) {
            aimEditText.setText(data);
        }
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        if (stepData.length() < MIN_CHARACTERS_NAME) {
            String titleError = String.format(unformattedErrorString, MIN_CHARACTERS_NAME);
            return new IsDataValid(false, titleError);
        } else {
            return new IsDataValid(true);
        }
    }
}




