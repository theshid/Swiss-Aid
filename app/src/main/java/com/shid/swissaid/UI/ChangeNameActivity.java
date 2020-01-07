package com.shid.swissaid.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Upload;
import com.shid.swissaid.R;

import androidx.appcompat.widget.Toolbar;

public class ChangeNameActivity extends BaseActivity {

    private static final String TAG = "ChangeNameActivity";

    private Toolbar toolbar;

    private Button btn_update;

    private EditText editText_name;

    private TextView textView_name;

    private FirebaseUser user;

    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        setUiElements();

        setFirebaseInstances();

        retrieveName();
        hideSoftKeyboard();

        btn_update.setOnClickListener(v -> update());


    }

    private void setFirebaseInstances() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setUiElements() {
        editText_name = findViewById(R.id.new_name);
        textView_name = findViewById(R.id.current_name);
        btn_update = findViewById(R.id.btnUpdateName);

        toolbar =findViewById(R.id.toolbar_name);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void updateFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.toast_update_fail_change_name), Toast.LENGTH_LONG).show();

        btn_update.setEnabled(true);
    }

//Method to retrieve the name of user and set it in a textview
    public void retrieveName(){
        DocumentReference documentReference = firebaseFirestore.collection(getResources().getString(R.string.collection_users))
                .document(user.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, document.getString("name"));
                    String name = document.getString("name");//Print the name
                    textView_name.setText(name);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
  // When the condition are met update
    public void update(){
        if(!validate()){
            updateFailed();
            return;
        }

        btn_update.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ChangeNameActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_update_change_name));
        progressDialog.show();

        updateName();
        progressDialog.hide();
    }

    public boolean validate() {
        boolean valid = true;

        String name = editText_name.getText().toString();


        if (name.isEmpty() || name.length() < 6) {
            editText_name.setError(getString(R.string.error_name_change_name));
            valid = false;
        } else {
            editText_name.setError(null);
        }


        return valid;
    }

    public  void updateName(){
        //we trim the name so that any space characters won't be considered which can cause errors
        String name = editText_name.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference =  db.collection(Constant.DATABASE_PATH_UPLOADS);

        //We change the name of the documents uploaded by the user
        reference.whereEqualTo("name_employee",user.getDisplayName().trim()).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Upload upload = document.toObject(Upload.class);

                            DocumentReference uploadReference = db.collection(Constant.DATABASE_PATH_UPLOADS)
                                    .document(upload.getId());

                            uploadReference.update("name_employee",name).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    Log.d(TAG,"employee name updated");
                                }
                            });


                        }
                    } else{
                        Log.d(TAG,"user have no documents uploaded");
                    }
                });


        //We now change the displayName so that the change will be reflected when the user will upload new
        //documents
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(task -> Log.d(TAG,"Done"));

        //We change the name in the User model in the database
        DocumentReference documentReference = db.collection(getResources().getString(R.string.collection_users))
                .document(user.getUid());


        documentReference.update("name",name).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "name updated");
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, getString(R.string.snack_update_change_name), Snackbar.LENGTH_SHORT).show();
                btn_update.setEnabled(true);
            } else {
                Log.d(TAG, "error updating name");
            }
        });
    }
}
