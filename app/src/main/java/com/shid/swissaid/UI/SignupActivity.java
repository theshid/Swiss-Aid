package com.shid.swissaid.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;

import java.util.HashMap;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mDb;
    private DatabaseReference reference;


    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        hideSoftKeyboard();


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    signup();


            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();



        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.dialog_create_account_sign_up));
        progressDialog.show();



        // TODO: Implement your own signup logic here.

            createAccount(email, password);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void createAccount(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){



                            Intent intent = new Intent(SignupActivity.this, VerifyActivity.class);
                            startActivity(intent);

                            //Setting up user information for real time database
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            String username = email.substring(0, email.indexOf("@"));

                            reference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("user_id", userId);
                            hashMap.put("email", email);
                            hashMap.put("name",_nameText.getText().toString().trim() );
                            hashMap.put("username", username);
                            hashMap.put("imageUrl", "default");
                            hashMap.put("imagePath","default");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG,"created user successfully in real time");
                                    }
                                }
                            });



                            //implementing user model in registration

                            FirebaseUser fire_user = firebaseAuth.getCurrentUser();
                            //Weedit the display name in Firebase so that its matches the user's name
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(_nameText.getText().toString().trim()).build();

                            fire_user.updateProfile(userProfileChangeRequest).addOnCompleteListener(task1 -> Log.d(TAG,"Done"));
                            //Setting the User's Information
                            User user = new User();
                            user.setEmail(email);
                            user.setName(_nameText.getText().toString().trim());
                            user.setUser_id(FirebaseAuth.getInstance().getUid());
                            user.setImageUrl("default");
                            user.setUsername(email.substring(0, email.indexOf("@")));
                            user.setStatus("offline");
                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                    .build();
                            mDb.setFirestoreSettings(settings);
                            // We name the document as the user ID
                            DocumentReference newUserRef = mDb
                                    .collection(getString(R.string.collection_users))
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener((Task<Void> task12) -> {
                               // hideDialog();

                                if(task12.isSuccessful()){
                                    //redirectLoginScreen();
                                }else{
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, getResources().getString(R.string.snack_error_sign_up), Snackbar.LENGTH_SHORT).show();
                                }
                            });



                        } else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, getResources().getString(R.string.snack_email_used_sign_up), Snackbar.LENGTH_SHORT).show();
                            }
                            Log.d(TAG,"Sign up error:" + task.getException().getMessage());
                            Log.d(TAG,"Sign up error:" + task.getException());

                        }

                    }
                });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
      //  finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_error_sign_up), Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError(getResources().getString(R.string.error_name_sign_up));
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.error_email_valid_sign_up));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if(!(email.contains("@gmail.com"))){
            _emailText.setError(getResources().getString(R.string.error_email_unicef_sign_up1));
            valid = false;
        } else {
            _emailText.setError(null);
        }



        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            _passwordText.setError(getResources().getString(R.string.error_password_sign_up));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError(getResources().getString(R.string.error_password_match_sign_up));
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}