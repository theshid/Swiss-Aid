package com.shid.swissaid.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shid.swissaid.R;


public class ChangePasswordActivity extends BaseActivity {

    private androidx.appcompat.widget.Toolbar toolbar;

    private EditText current_password;
    private EditText new_password;
    private EditText confirm_password;

    private Button btn_update;

    private FirebaseUser user;

    private FirebaseAuth auth;

    private TextView forgot_password;

    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        setUiElements();

        setFirebaseElements();

        setClickEvents();

        hideSoftKeyboard();

    }

    private void setFirebaseElements() {
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setClickEvents() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        btn_update.setOnClickListener(v -> update());
        forgot_password.setOnClickListener(v -> resetEmail());
    }

    private void setUiElements() {
        toolbar = findViewById(R.id.toolbar_pass);

        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);

        forgot_password = findViewById(R.id.text_view_forgotPassword);

        progressBar = findViewById(R.id.progressBar_change_activity);

        btn_update = findViewById(R.id.btnUpdate);

    }

    private void resetEmail() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        final String email = user.getEmail();

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email)

                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_instruction_change_password), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_instruction_fail_change_password), Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                });

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void updateFailed() {
        Toast.makeText(getBaseContext(), "Update failed, check errors", Toast.LENGTH_LONG).show();

        btn_update.setEnabled(true);
    }

    public void update() {

        if (!validate()) {
            updateFailed();
            return;
        }


        String password_new = new_password.getText().toString();
        String current = current_password.getText().toString();

        btn_update.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_update_change_password));
        progressDialog.show();

        user = FirebaseAuth.getInstance().getCurrentUser();

        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, current);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(password_new).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, getString(R.string.snack_error_change_password), Snackbar.LENGTH_SHORT).show();

                        btn_update.setEnabled(true);
                        progressDialog.hide();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_update_change_password), Toast.LENGTH_LONG).show();
                        btn_update.setEnabled(true);
                        progressDialog.hide();
                        onBackPressed();
                    }
                });
            } else {

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, getString(R.string.snack_incorrect_change_password), Snackbar.LENGTH_SHORT).show();
                btn_update.setEnabled(true);
                progressDialog.hide();
            }
        });


    }

    public boolean validate() {
        boolean valid = true;

        String confirm = confirm_password.getText().toString();
        String password_new = new_password.getText().toString();
        String current = current_password.getText().toString();


        if (password_new.isEmpty() || password_new.length() < 6 || password_new.length() > 20) {
            new_password.setError(getString(R.string.editText_error_change_password));
            valid = false;
        } else {
            new_password.setError(null);
        }

        if (current.isEmpty() || current.length() < 6 || current.length() > 20) {
            current_password.setError(getString(R.string.editText_error_change_password));
            valid = false;
        } else {
            current_password.setError(null);
        }

        if (confirm.isEmpty() || confirm.length() < 6 || confirm.length() > 20) {
            confirm_password.setError(getString(R.string.editText_error_change_password));
            valid = false;
        } else {
            confirm_password.setError(null);
        }

        if (!(password_new.equals(confirm))) {
            new_password.setError(getString(R.string.error_match_change_passeword));
            confirm_password.setError(getString(R.string.error_match_change_passeword));
            valid = false;
        } else {
            new_password.setError(null);
            confirm_password.setError(null);
        }

        return valid;
    }

}
