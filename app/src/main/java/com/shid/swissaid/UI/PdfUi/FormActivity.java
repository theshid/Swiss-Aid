package com.shid.swissaid.UI.PdfUi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.shid.swissaid.R;
import com.shid.swissaid.UI.BaseActivity;
import com.shid.swissaid.UI.FormStep.FormAimStep;
import com.shid.swissaid.UI.FormStep.FormBudgetStep;
import com.shid.swissaid.UI.FormStep.FormDateStep;
import com.shid.swissaid.UI.FormStep.FormFileStep;
import com.shid.swissaid.UI.FormStep.FormMeetingStep;
import com.shid.swissaid.UI.FormStep.FormMembersStep;
import com.shid.swissaid.UI.FormStep.FormNameStep;
import com.shid.swissaid.UI.FormStep.FormPointStep;
import com.shid.swissaid.UI.FormStep.FormResultStep;
import com.shid.swissaid.UI.FormStep.FormRouteStep;
import com.shid.swissaid.UI.FormStep.FormTaStep;
import com.shid.swissaid.UI.Step.DurationStep;
import com.shid.swissaid.UI.Step.NameProjectStep;
import com.shid.swissaid.UI.Step.Q10Step;
import com.shid.swissaid.UI.Step.Q11Step;
import com.shid.swissaid.UI.Step.Q12Step;
import com.shid.swissaid.UI.Step.Q13Step;
import com.shid.swissaid.UI.Step.Q1Step;
import com.shid.swissaid.UI.Step.Q2Step;
import com.shid.swissaid.UI.Step.Q3Step;
import com.shid.swissaid.UI.Step.Q4Step;
import com.shid.swissaid.UI.Step.Q5Step;
import com.shid.swissaid.UI.Step.Q6Step;
import com.shid.swissaid.UI.Step.Q7Step;
import com.shid.swissaid.UI.Step.Q8Step;
import com.shid.swissaid.UI.Step.Q9Step;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class FormActivity extends BaseActivity implements StepperFormListener, DialogInterface.OnClickListener {

    private ProgressDialog progressDialog;
    private VerticalStepperFormView verticalStepperForm;

    private FormNameStep nameStep;
    private FormDateStep dateStep;
    private FormBudgetStep budgetStep;
    private FormRouteStep routeStep;
    private FormMembersStep membersStep;
    private FormMeetingStep meetingStep;
    private FormAimStep aimStep;
    private FormResultStep resultStep;
    private FormTaStep taStep;
    private FormFileStep fileStep;
    private FormPointStep pointStep;

    private NameProjectStep nameProjectStep;
    private DurationStep durationStep;
    private Q1Step q1Step;
    private Q2Step q2Step;
    private Q3Step q3Step;
    private Q4Step q4Step;
    private Q5Step q5Step;
    private Q6Step q6Step;
    private Q7Step q7Step;
    private Q8Step q8Step;
    private Q9Step q9Step;
    private Q10Step q10Step;
    private Q11Step q11Step;
    private Q12Step q12Step;
    private Q13Step q13Step;

    public static final String STATE_NAME_PROJECT = "name_project";
    public static final String STATE_DURATION_STEP = "duration";
    public static final String STATE_Q1 = "q1";
    public static final String STATE_Q2 = "q2";
    public static final String STATE_Q3 = "q3";
    public static final String STATE_Q4 = "q4";
    public static final String STATE_Q5 = "q5";
    public static final String STATE_Q6 = "q6";
    public static final String STATE_Q7 = "q7";
    public static final String STATE_Q8 = "q8";
    public static final String STATE_Q9 = "q9";
    public static final String STATE_Q10 = "q10";
    public static final String STATE_Q11 = "q11";
    public static final String STATE_Q12 = "q12";
    public static final String STATE_Q13 = "q13";



    public static final String STATE_NEW_FORM_ADDED = "new_form_added";
    public static final String STATE_TA = "ta";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        String[] stepTitles = getResources().getStringArray(R.array.steps_titles1);

        nameProjectStep = new NameProjectStep(stepTitles[0]);
        durationStep = new DurationStep(stepTitles[1]);
        taStep = new FormTaStep(stepTitles[2]);
        q1Step = new Q1Step(stepTitles[3]);
        q2Step = new Q2Step(stepTitles[4]);
        q3Step = new Q3Step(stepTitles[5]);
        q4Step = new Q4Step(stepTitles[6]);
        q5Step = new Q5Step(stepTitles[7]);
        q6Step = new Q6Step(stepTitles[8]);
        q7Step = new Q7Step(stepTitles[9]);
        q8Step = new Q8Step(stepTitles[10]);
        q9Step = new Q9Step(stepTitles[11]);
        q10Step = new Q10Step(stepTitles[12]);
        q11Step = new Q11Step(stepTitles[13]);
        q12Step = new Q12Step(stepTitles[14]);
        q13Step = new Q13Step(stepTitles[15]);


        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm.setup(this,nameProjectStep,durationStep,taStep,q1Step,q2Step,q3Step,q4Step,
                q5Step,q6Step,q7Step,q8Step,q9Step,q10Step,q11Step,q12Step,q13Step).init();
       // verticalStepperForm.setup(this,fileStep,nameStep,taStep,dateStep,budgetStep,routeStep,membersStep,
         //       meetingStep,aimStep,resultStep,pointStep).init();

    }

    @Override
    public void onCompletedForm() {
        final Thread dataSavingThread = saveData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.form_sending_data_message));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                try {
                    dataSavingThread.interrupt();
                } catch (RuntimeException e) {
                    // No need to do anything here
                } finally {
                    verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                }
            }
        });
    }

    @Override
    public void onCancelledForm() {
        showCloseConfirmationDialog();
    }

    private Thread saveData() {

        // Fake data saving effect
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(STATE_NEW_FORM_ADDED, true);
                    intent.putExtra(STATE_NAME_PROJECT, nameProjectStep.getStepData());
                    intent.putExtra(STATE_DURATION_STEP, durationStep.getStepData());
                    intent.putExtra(STATE_TA, taStep.getStepData());
                    intent.putExtra(STATE_Q1, q1Step.getStepData());
                    intent.putExtra(STATE_Q2, q2Step.getStepData());
                    intent.putExtra(STATE_Q3, q3Step.getStepData());
                    intent.putExtra(STATE_Q4, q4Step.getStepData());
                    intent.putExtra(STATE_Q5, q5Step.getStepData());
                    intent.putExtra(STATE_Q6, q6Step.getStepData());
                    intent.putExtra(STATE_Q7, q7Step.getStepData());
                    intent.putExtra(STATE_Q8, q8Step.getStepData());
                    intent.putExtra(STATE_Q9, q9Step.getStepData());
                    intent.putExtra(STATE_Q10, q10Step.getStepData());
                    intent.putExtra(STATE_Q11, q11Step.getStepData());
                    intent.putExtra(STATE_Q12, q12Step.getStepData());
                    intent.putExtra(STATE_Q13, q13Step.getStepData());


                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return thread;
    }

    private void finishIfPossible() {
        if(verticalStepperForm.isAnyStepCompleted()) {
            showCloseConfirmationDialog();
        } else {
            finish();
        }
    }

    private void showCloseConfirmationDialog() {
        new DiscardAlarmConfirmationFragment().show(getSupportFragmentManager(), null);
    }

    private void dismissDialogIfNecessary() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finishIfPossible();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {

            // "Discard" button of the Discard Alarm dialog
            case -1:
                finish();
                break;

            // "Cancel" button of the Discard Alarm dialog
            case -2:
                verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finishIfPossible();
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissDialogIfNecessary();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dismissDialogIfNecessary();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(STATE_NAME_PROJECT, nameProjectStep.getStepData());
        savedInstanceState.putString(STATE_DURATION_STEP, durationStep.getStepData());
        savedInstanceState.putString(STATE_TA, taStep.getStepData());
        savedInstanceState.putString(STATE_Q1, q1Step.getStepData());
        savedInstanceState.putString(STATE_Q2, q2Step.getStepData());
        savedInstanceState.putString(STATE_Q3, q3Step.getStepData());
        savedInstanceState.putString(STATE_Q4, q4Step.getStepData());
        savedInstanceState.putString(STATE_Q5, q5Step.getStepData());
        savedInstanceState.putString(STATE_Q6, q6Step.getStepData());
        savedInstanceState.putString(STATE_Q7, q7Step.getStepData());
        savedInstanceState.putString(STATE_Q8, q8Step.getStepData());
        savedInstanceState.putString(STATE_Q9, q9Step.getStepData());
        savedInstanceState.putString(STATE_Q10, q10Step.getStepData());
        savedInstanceState.putString(STATE_Q11, q11Step.getStepData());
        savedInstanceState.putString(STATE_Q12, q12Step.getStepData());
        savedInstanceState.putString(STATE_Q13, q13Step.getStepData());


        // IMPORTANT: The call to super method must be here at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if(savedInstanceState.containsKey(STATE_NAME_PROJECT)) {
            String nameProject = savedInstanceState.getString(STATE_NAME_PROJECT);
            nameProjectStep.restoreStepData(nameProject);
        }

        if(savedInstanceState.containsKey(STATE_DURATION_STEP)) {
            String duration = savedInstanceState.getString(STATE_DURATION_STEP);
            durationStep.restoreStepData(duration);
        }

        if(savedInstanceState.containsKey(STATE_TA)) {
            String projectNumber = savedInstanceState.getString(STATE_TA);
            taStep.restoreStepData(projectNumber);
        }

        if(savedInstanceState.containsKey(STATE_Q1)) {
            String q1 = savedInstanceState.getString(STATE_Q1);
            q1Step.restoreStepData(q1);
        }

        if(savedInstanceState.containsKey(STATE_Q2)) {
            String q2 = savedInstanceState.getString(STATE_Q2);
            q2Step.restoreStepData(q2);
        }

        if(savedInstanceState.containsKey(STATE_Q3)) {
            String q3 = savedInstanceState.getString(STATE_Q3);
            q3Step.restoreStepData(q3);
        }

        if(savedInstanceState.containsKey(STATE_Q4)) {
            String q4 = savedInstanceState.getString(STATE_Q4);
            q4Step.restoreStepData(q4);
        }

        if(savedInstanceState.containsKey(STATE_Q5)) {
            String q5 = savedInstanceState.getString(STATE_Q5);
            q5Step.restoreStepData(q5);
        }

        if(savedInstanceState.containsKey(STATE_Q6)) {
            String q6 = savedInstanceState.getString(STATE_Q6);
            q6Step.restoreStepData(q6);
        }

        if(savedInstanceState.containsKey(STATE_Q7)) {
            String q7 = savedInstanceState.getString(STATE_Q7);
            q7Step.restoreStepData(q7);
        }

        if(savedInstanceState.containsKey(STATE_Q8)) {
            String q8 = savedInstanceState.getString(STATE_Q8);
            q8Step.restoreStepData(q8);
        }

        if(savedInstanceState.containsKey(STATE_Q9)) {
            String q9 = savedInstanceState.getString(STATE_Q9);
            q9Step.restoreStepData(q9);
        }

        if(savedInstanceState.containsKey(STATE_Q10)) {
            String q10 = savedInstanceState.getString(STATE_Q10);
            q10Step.restoreStepData(q10);
        }

        if(savedInstanceState.containsKey(STATE_Q11)) {
            String q11 = savedInstanceState.getString(STATE_Q11);
            q11Step.restoreStepData(q11);
        }

        if(savedInstanceState.containsKey(STATE_Q12)) {
            String q12 = savedInstanceState.getString(STATE_Q12);
            q12Step.restoreStepData(q12);
        }

        if(savedInstanceState.containsKey(STATE_Q13)) {
            String q13 = savedInstanceState.getString(STATE_Q13);
            q13Step.restoreStepData(q13);
        }


        // IMPORTANT: The call to super method must be here at the end
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static class DiscardAlarmConfirmationFragment extends DialogFragment {

        private DialogInterface.OnClickListener listener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            listener = (DialogInterface.OnClickListener) context;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.form_discard_question)
                    .setMessage(R.string.form_info_will_be_lost)
                    .setPositiveButton(R.string.form_discard, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton(R.string.form_discard_cancel, listener)
                    .setCancelable(false);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            return dialog;
        }
    }
}

