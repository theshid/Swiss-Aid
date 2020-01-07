package com.shid.swissaid.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shid.swissaid.R;

import androidx.annotation.NonNull;

public class VerifyActivity extends BaseActivity {
    private static final String TAG = "VerifyActivity";
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        sendEmailVerification();
        delayIntent();
    }

    private void delayIntent() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, 5000);
    }


    private void sendEmailVerification() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (!firebaseUser.isEmailVerified()) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.toast_verification_sent) + " " + firebaseUser.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, getString(R.string.toast_verification_sent) + " " + firebaseUser.getEmail());
                            } else {
                                Log.e(TAG, getString(R.string.toast_fail_verification), task.getException());
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.toast_fail_verification),
                                        Toast.LENGTH_SHORT).show();
                                // email not sent, so display message and restart the activity or do whatever you wish to do

                                //restart this activity
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                            }
                        }
                    });


        }
    }
}