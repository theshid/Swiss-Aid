package com.shid.swissaid.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Upload;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.shid.swissaid.Constant.Constant.READ_STORAGE_PERMISSION_REQUEST_CODE;

public class UploadFragment extends Fragment implements View.OnClickListener {

    //these are the views
    private TextView textViewStatus;
    private EditText editTextFilename, editText_mission, editText_numeroTA;
    private ProgressBar progressBar;

    //the firebase objects for storage and database
    private StorageReference mStorageReference;
    private ImageView header_en, header_fr;

    private FirebaseFirestore mDb;
    private FirebaseUser firebaseUser;

    public UploadFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        FirebaseApp.initializeApp(getActivity());

        setUiElements(view);
        //Display correct header depending on the language
        displayHeader();

        setFirebaseInstances();
        //attaching listeners to views
        view.findViewById(R.id.buttonUploadFile).setOnClickListener(this);

        return view;
    }

    private void setFirebaseInstances() {
        //getting firebase objects
        mDb = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUiElements(View view) {
        header_en = view.findViewById(R.id.banner_home);
        header_fr = view.findViewById(R.id.banner_home_fr);
        //getting the views
        textViewStatus = view.findViewById(R.id.textViewStatus);
        editTextFilename = view.findViewById(R.id.editTextFileName);
        editText_mission = view.findViewById(R.id.editTextMission);
        editText_numeroTA = view.findViewById(R.id.editTextNumTA);
        progressBar = view.findViewById(R.id.progressbar);
    }


    //this method for check runtime Permission is enable or not
    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //will show a dialog for permission
    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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


    //this function will get the pdf from the storage
    private void getPDF() throws Exception {

        if (!checkPermissionForReadExternalStorage()) {
            requestPermissionForReadExternalStorage();
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/SwissAid/PDF");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri,"application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.PICK_PDF_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == Constant.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_file_upload_fragment), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //this method is uploading the file
    private void uploadFile(Uri data) {

        progressBar.setVisibility(View.VISIBLE);
        final StorageReference sRef = mStorageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("TAG", "onSuccess: uri= " + uri.toString());
                                progressBar.setVisibility(View.GONE);
                                textViewStatus.setText(getString(R.string.texview_file_upload_fragment));
                                //set up the id in Firebase
                                //using a call to document() w/o passing argument, a unique id is
                                //generated
                                DocumentReference ref = mDb.collection(Constant.DATABASE_PATH_UPLOADS).document();

                                String myId = ref.getId();
                                String url = uri.toString();

                                //set up the date
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ");
                                Date date = new Date();
                                String strDate = dateFormat.format(date).toString();

                                //Getting the user details

                                DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                                        .document(FirebaseAuth.getInstance().getUid());

                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: successfully set the user client.");
                                            User user = task.getResult().toObject(User.class);
                                            //Watch this
                                            // ((UserClient)(getContext())).setUser(user);

                                            Upload upload = new Upload();
                                            upload.setMission(editText_mission.getText().toString());
                                            upload.setUser(user);
                                            upload.setName(editTextFilename.getText().toString());
                                            upload.setNumero_ta(editText_numeroTA.getText().toString());
                                            upload.setTime(strDate);
                                            upload.setId(myId);
                                            upload.setUrl(url);

                                            upload.setName_employee(firebaseUser.getDisplayName());


                                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                                    .build();
                                            mDb.setFirestoreSettings(settings);


                                            DocumentReference newUploadRef = mDb
                                                    .collection(Constant.DATABASE_PATH_UPLOADS)
                                                    .document(myId);

                                            newUploadRef.set(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        View parentLayout = getActivity().findViewById(android.R.id.content);
                                                        Snackbar.make(parentLayout, getString(R.string.snack_file_upload_fragment), Snackbar.LENGTH_SHORT).show();
                                                        editText_mission.setText("");
                                                        editText_numeroTA.setText("");
                                                        editTextFilename.setText("");
                                                    } else {
                                                        View parentLayout = getActivity().findViewById(android.R.id.content);
                                                        Snackbar.make(parentLayout, getString(R.string.snack_error_upload_fragment), Snackbar.LENGTH_SHORT).show();
                                                    }


                                                }
                                            });

                                        }
                                    }
                                });


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + getString(R.string.textview_upload_fragment));
                    }
                });

    }

    public boolean validate() {
        boolean valid = true;


        String mission = editText_mission.getText().toString();
        String numero_ta = editText_numeroTA.getText().toString();
        String name_file = editTextFilename.getText().toString();


        if (mission.isEmpty()) {
            editText_mission.setError(getString(R.string.error_upload_fragment));
            valid = false;
        } else {
            editText_mission.setError(null);
        }

        if (name_file.isEmpty()) {
            editTextFilename.setError(getString(R.string.error_upload_fragment));
            valid = false;
        } else {
            editTextFilename.setError(null);
        }


        if (numero_ta.isEmpty() || numero_ta.length() < 10) {
            editText_numeroTA.setError(getString(R.string.error_ta_upload_fragment));
            valid = false;
        } else {
            editText_numeroTA.setError(null);
        }


        return valid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUploadFile:
                try {
                    if (validate()) {
                        getPDF();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }
}
