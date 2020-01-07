package com.shid.swissaid.UI;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.rxdownloader.RxDownloader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shid.swissaid.Adapters.MessageAdapter;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Chat;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.Notification.Client;
import com.shid.swissaid.Notification.Data;
import com.shid.swissaid.Notification.MyResponse;
import com.shid.swissaid.Notification.Sender;
import com.shid.swissaid.Notification.Token;
import com.shid.swissaid.R;
import com.shid.swissaid.UI.ChatFragment.APIService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.codetail.animation.ViewAnimationUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shid.swissaid.Constant.Constant.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.shid.swissaid.Constant.Constant.GALLERY_PICK;
import static com.shid.swissaid.Constant.Constant.IMAGE_CAMERA_REQUEST;
import static com.shid.swissaid.Constant.Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class MessageActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MessageActivity";

    CircleImageView profile_image;
    TextView username;

    Intent intent;

    ImageButton btn_send;
    ImageButton btn_file;
    ImageButton btn_gallery;
    ImageButton btn_camera;
    ImageButton btn_doc;
    ImageButton btn_location;
    EditText txt_send;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    List<Chat> mChat;

    String userid;
    String nameFile;
    String pathImage;
    Uri sentImageUri;


    APIService apiService;


    //boolean to determine if notification should be sent
    boolean shouldNotify = false;
    private boolean isAnimationMenuHidden = true;


    private Uri imageUri;

    // private Uri photoUri;
    // private String photo_name;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ValueEventListener seenListener;
    StorageReference storageReference;
    private StorageTask uploadTask;

    private RxDownloader rxDownloader;
    private String name_file;
    private LinearLayout mRevealView;


    private android.app.AlertDialog waitingDialog;
    private android.app.AlertDialog dlDialog;

    //Define a request code to send to Google Play services

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        intent = getIntent();

        //Setting the UI
        setToolbar();
        setRecyclerView();
        setUi();

        setmGoogleApiClientAndLocationRequest();
        setFirebaseInstances();


        final String userid = intent.getStringExtra("userid");

        btnClickListeners();
        seenMessage(userid);
    }

    private void setFirebaseInstances() {
        final String userid = intent.getStringExtra("userid");
        //Reference of where the medias are stored
        storageReference = FirebaseStorage.getInstance().getReference("messageImg");

        //variable to set the Firebase Cloud Messaging i.e the notifications
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.icon);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
                readMessages(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUi() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        btn_send = findViewById(R.id.btn_send);
        btn_file = findViewById(R.id.btn_file);
        // btn_camera = findViewById(R.id.btn_camera);
        btn_doc = findViewById(R.id.btn_doc);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_location = findViewById(R.id.btn_location);
        txt_send = findViewById(R.id.text_send);

        mRevealView = findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void setmGoogleApiClientAndLocationRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // slide1 second, in milliseconds
    }

    private void readMessages(final String myId, final String userId, final String imageUrl) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.setOnItemClickListener(new MessageAdapter.onRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Chat chat1 = mChat.get(position);
                            if (chat1.getType().equals("\uD83D\uDCF7 Image")) {
                                Log.d("Image", "Image has been clicked");
                                showImageOptionDialog(chat1.getMessage(), getRandomString());

                            } else if (chat1.getType().equals("\uD83D\uDCC1 Document")) {
                                if (isFilePresent(chat1.getDocName())) {
                                    openDoc(chat1.getDocName());
                                } else {
                                    dlDialog = new SpotsDialog.Builder()
                                            .setContext(MessageActivity.this)
                                            .setMessage(getString(R.string.loading))
                                            .setCancelable(false)
                                            .build();
                                    dlDialog.show();
                                    downloadDoc(chat1.getMessage(), chat1.getDocName());

                                }
                            } else if (chat1.getType().equals("\uD83C\uDF0D Location")) {
                                showMapOptionsDialog(chat1.getLatitude(), chat1.getLongitude());
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMapOptionsDialog(double lat, double lon) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MessageActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);

        final Button btn_viewMap = dialogView.findViewById(R.id.btnDelete);
        final Button btn_directionMap = dialogView.findViewById(R.id.btnDownload);

        dialogBuilder.setTitle(getString(R.string.option_location));

        btn_viewMap.setText(getString(R.string.view_location));
        btn_directionMap.setText(R.string.view_direction);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_directionMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapIntent(lat, lon);
                alertDialog.dismiss();
                //Opening the upload file in browser using the upload url

            }
        });

        btn_viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + "Position" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(intent);
                alertDialog.dismiss();

            }
        });

    }

    private void showImageOptionDialog(String url, String file_name) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MessageActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);

        final Button btn_view = dialogView.findViewById(R.id.btnDelete);
        final Button btn_download = dialogView.findViewById(R.id.btnDownload);

        dialogBuilder.setTitle(getString(R.string.option_image));

        btn_view.setText(getString(R.string.view_image));


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RxDownloader rxDownloader = new RxDownloader(MessageActivity.this);
                rxDownloader.download(url, file_name, "image/*", true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String s) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(MessageActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.dismiss();
                //Opening the upload file in browser using the upload url

            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, FullScreenImageActivity.class);
                intent.putExtra("urlPhotoClick", url);
                startActivity(intent);
                alertDialog.dismiss();

            }
        });

    }


    private void btnClickListeners() {
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
                //fetchImage();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Message is about to be sent so notification should be sent
                shouldNotify = true;
                String msg = txt_send.getText().toString();
                if (!msg.equals("")) {
                    userid = intent.getStringExtra("userid");
                    sendMessage(firebaseUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, getString(R.string.empty_msg), Toast.LENGTH_LONG).show();
                }
                txt_send.setText("");
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchImage();
                animation();
            }
        });
        /*
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                  //  photoCameraIntent();
                    animation();
                }

            }
        });
        */

        btn_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getDocument();
                    animation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userid = intent.getStringExtra("userid");
                sendMap(firebaseUser.getUid(), userid, fetchMap(), currentLatitude, currentLongitude);
                animation();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            imageUri = data.getData();
            //Compressing image to reduce size
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                storeImageInStorage(getImageUri(this, bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {

            // photoUri = data.getData();

            //stockPhoto(photoUri);

        } else if (requestCode == Constant.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                String docName = fetchDocName(data.getData());
                uploadFile(data.getData(), docName);

            } else {
                Toast.makeText(MessageActivity.this, getString(R.string.toast_file_upload_fragment), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void fetchImage() {
        Intent intent_gallery = new Intent();
        intent_gallery.setType("image/*");
        intent_gallery.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent_gallery, "SELECT IMAGE"), GALLERY_PICK);
    }

    private String fetchMap() {
        String urlMap = "https://maps.googleapis.com/maps/api/staticmap?center=";
        String urlMap1 = "&zoom=16&scale=slide2&size=600x300&maptype=roadmap&key=";
        String urlMap2 = "&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:slide1%7C";
        String lat = String.valueOf(currentLatitude);
        String lont = String.valueOf(currentLongitude);
        String mapUrl = urlMap + lat + "," + lont + urlMap1 + getString(R.string.map_apiKey) + urlMap2 + lat + "," + lont;

        return mapUrl;
    }

    //Fetching the Documents name without the extension
    private String fetchDocName(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                    int pos = displayName.lastIndexOf(".");
                    if (pos > 0) {
                        displayName = displayName.substring(0, pos);
                    }

                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
            int pos = displayName.lastIndexOf(".");
            if (pos > 0) {
                displayName = displayName.substring(0, pos);
            }

        }
        return displayName;
    }

    private void uploadFile(Uri data, String file_name) {
        waitingDialog = new SpotsDialog.Builder()
                .setContext(MessageActivity.this)
                .setMessage(getString(R.string.loading))
                .setCancelable(false)
                .build();
        waitingDialog.show();
        //final StorageReference sRef = mStorageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        final StorageReference fileReference = storageReference.child(file_name
                + "." + getFileExtension(data));
        fileReference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                          @SuppressWarnings("VisibleForTests")
                                          @Override
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                      Log.d("TAG", "onSuccess: uri= " + uri.toString());

                                                      //set up the id in Firebase
                                                      //using a call to document() w/o passing argument, a unique id is
                                                      //generated
                                                      String doc_name = fileReference.getName();

                                                      //String myId = ref.getId();
                                                      String url = uri.toString();

                                                      final String userid = intent.getStringExtra("userid");

                                                      sendDoc(firebaseUser.getUid(), userid, url, doc_name);


                                                  }
                                              });
                                          }
                                      }
                );


    }


    private void getDocument() throws Exception {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "Select Document"), Constant.PICK_PDF_CODE);
    }
/*
    public void createFile(String nameFile) {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid");
        File subFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/Image/Sent");

        if (!folder.exists()) {
            folder.mkdir();
            if(!subFolder.exists()){
                subFolder.mkdirs();
            }
        }
       File imgFile = new File(subFolder, nameFile + ".pdf");
    }

    */

    private void storeImageInPhone(Bitmap image) {
        File pictureFile = getOutputMediaFile(nameFile);
        Log.d(TAG,"path: " + pictureFile.getAbsolutePath());
        pathImage = pictureFile.getAbsolutePath();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            //Retrieving the path
            /*
            pathImage = getPathFromURI(getImageUri(this, image));
            if (pathImage != null) {
                File f = new File(pathImage);
                sentImageUri = Uri.fromFile(f);
            }
            */
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(String nameFile) {

        File folder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid");
        File subFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/Image/Sent");
        File sFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/Image");
        if (!folder.exists()) {
            folder.mkdir();
            if (!sFolder.exists()){
                sFolder.mkdirs();
                if (!subFolder.exists()) {
                    subFolder.mkdirs();
                }
            }

        } else if (folder.exists()){
            if (!sFolder.exists()) {
                sFolder.mkdirs();
                if (!subFolder.exists()) {
                    subFolder.mkdirs();
                }
            }
        }
        File imgFile = new File(subFolder, nameFile + ".jpeg");

        // Create a media file name
        File mediaFile;
        mediaFile = new File(subFolder, nameFile + ".jpeg");
        return mediaFile;
    }

    private Bitmap generateBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return bitmap;
    }

    private void storeImageInStorage(Uri uriImage) {
        if (uriImage != null) {
            nameFile = String.valueOf(System.currentTimeMillis());
            getOutputMediaFile(nameFile);
            storeImageInPhone(generateBitmapFromUri(uriImage));

            final StorageReference fileReference = storageReference.child(nameFile
                    + "." + getFileExtension(uriImage));

            uploadTask = fileReference.putFile(uriImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    name_file = fileReference.getName();
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        final String userid = intent.getStringExtra("userid");

                        sendImage(firebaseUser.getUid(), userid, mUri);


                    } else {
                        Toast.makeText(MessageActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        } else {
            Toast.makeText(MessageActivity.this, getString(R.string.no_message), Toast.LENGTH_LONG).show();
        }

    }

    private void sendMap(String sender, final String receiver, String message, double latitude, double longitude) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        userid = intent.getStringExtra("userid");
        String time_message = DateFormat.getDateTimeInstance().format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("docName", "null");
        hashMap.put("isseen", false);
        hashMap.put("type", "\uD83C\uDF0D Location");
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);
        hashMap.put("time", time_message);
        hashMap.put("sentImagePath","default");


        reference.child("Chats").push().setValue(hashMap);


        //add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //receiver getting his chatlist created when user send a message
        final DatabaseReference chatRef_receiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());

        chatRef_receiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef_receiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (shouldNotify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }

                shouldNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendDoc(String sender, final String receiver, String message, String name_doc) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        userid = intent.getStringExtra("userid");
        String time_message = DateFormat.getDateTimeInstance().format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("docName", "\uD83D\uDCC1 " + name_doc);
        hashMap.put("isseen", false);
        hashMap.put("type", "\uD83D\uDCC1 Document");
        hashMap.put("latitude", 0);
        hashMap.put("longitude", 0);
        hashMap.put("time", time_message);
        hashMap.put("sentImagePath","default");


        reference.child("Chats").push().setValue(hashMap);
        waitingDialog.dismiss();

        //add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //receiver getting his chatlist created when user send a message
        final DatabaseReference chatRef_receiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());

        chatRef_receiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef_receiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (shouldNotify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }

                shouldNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        userid = intent.getStringExtra("userid");
        String time_message = DateFormat.getDateTimeInstance().format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("docName", "null");
        hashMap.put("isseen", false);
        hashMap.put("type", "text");
        hashMap.put("latitude", 0);
        hashMap.put("longitude", 0);
        hashMap.put("time", time_message);
        hashMap.put("sentImagePath","default");

        reference.child("Chats").push().setValue(hashMap);

        //add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //receiver getting his chatlist created when user send a message
        final DatabaseReference chatRef_receiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());

        chatRef_receiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef_receiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (shouldNotify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }

                shouldNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendImage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        userid = intent.getStringExtra("userid");
        String time_message = DateFormat.getDateTimeInstance().format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("docName", "null");
        hashMap.put("isseen", false);
        hashMap.put("type", "\uD83D\uDCF7 Image");
        hashMap.put("latitude", 0);
        hashMap.put("longitude", 0);
        hashMap.put("time", time_message);
        hashMap.put("sentImagePath",pathImage);


        reference.child("Chats").push().setValue(hashMap);

        //add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //receiver getting his chatlist created when user send a message
        final DatabaseReference chatRef_receiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());

        chatRef_receiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef_receiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (shouldNotify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }

                shouldNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Determine the file extension of a document
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Determine if Message has been seen by the receiver
    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.icon, username + ": " + message,
                            getString(R.string.new_message), userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void downloadDoc(String url, String file_name) {
        RxDownloader rxDownloader = new RxDownloader(MessageActivity.this);
        rxDownloader.download(url, file_name, "application/*", true)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        dlDialog.dismiss();
                        openDoc(file_name);
                    }
                });
    }

    private void openDoc(String filename) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + filename);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    //Determine if a file is saved on the device
    public boolean isFilePresent(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        return file.exists();
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    //Set the user status whether online or offline
    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        stopLocationUpdates();

    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    //Method to generate a random String
    private String getRandomString() {
        String randomStringCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        while (stringBuilder.length() < 18) { // length of the random string.
            int index = (int) (random.nextFloat() * randomStringCharacters.length());
            stringBuilder.append(randomStringCharacters.charAt(index));
        }
        String randomString = stringBuilder.toString();
        return randomString;

    }


    private void googleMapIntent(double latitude, double longitude) {
        String lat = String.valueOf(latitude);
        String longi = String.valueOf(longitude);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + longi);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        } catch (NullPointerException e) {
            Log.e("TAG", "onClick: NullPointerException: Couldn't open map." + e.getMessage());
            Toast.makeText(MessageActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
        }
    }


    private void animation() {
        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


        Animator animator =
                ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);

        Animator animator_reverse = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);

        if (isAnimationMenuHidden) {
            mRevealView.setVisibility(View.VISIBLE);
            animator.start();
            isAnimationMenuHidden = false;

        } else {

            animator_reverse.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    isAnimationMenuHidden = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }


            });

            animator_reverse.start();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }


      /*
    private void photoCameraIntent() {

        String nomeFoto = android.text.format.DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(MessageActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, IMAGE_CAMERA_REQUEST);

    }
      */

    /*
    private void stockPhoto(Uri photoUri) {
        if (photoUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(photoUri));

            uploadTask = fileReference.putFile(photoUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    name_file = fileReference.getName();
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        final String userid = intent.getStringExtra("userid");

                        sendImage(firebaseUser.getUid(), userid, mUri);


                    } else {
                        Toast.makeText(MessageActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });


        } else {
            Toast.makeText(MessageActivity.this, getString(R.string.no_message), Toast.LENGTH_LONG).show();
        }

    }
    */
}
