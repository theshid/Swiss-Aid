package com.shid.swissaid.UI;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shid.swissaid.Model.Chat;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.Model.UserLocation;
import com.shid.swissaid.R;
import com.shid.swissaid.Services.LocationService;
import com.shid.swissaid.Util.Offline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.shid.swissaid.Constant.Constant.ERROR_DIALOG_REQUEST;
import static com.shid.swissaid.Constant.Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.shid.swissaid.Constant.Constant.PERMISSIONS_REQUEST_ENABLE_GPS;


public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    //final Fragment fragment2 = new ReportFragment();
    final Fragment fragment3 = new UploadFragment();
    //final Fragment fragment4 = new SettingFragment();
    final Fragment fragment1 = new CustomFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    Fragment saved_fragment = null;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private CircleImageView profile_image;
    private CircleImageView image_toolbar;
    private TextView name;
    private TextView username;
    private TextView title_toolbar;
    private DatabaseReference reference;

    private FirebaseUser firebaseUser;

    private ImageView img_chat;

    private int unread;
    private TextView count_txt;

    //geo
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseFirestore mDb;
    private UserLocation mUserLocation;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ListenerRegistration mUserListEventListener;
    private SwitchCompat animatedSwitch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // img_chat = findViewById(R.id.chat_icon)
        //Setting the toolbar and navigation drawer
        setUiElements();

        setNavigationHeader();
        getTotalUsers();


        if (savedInstanceState == null) {
            //fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();
            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
            //fm.beginTransaction().add(R.id.main_container, fragment2, "slide2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();


        } else {
          //  fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();
            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
            //   fm.beginTransaction().add(R.id.main_container, fragment2, "slide2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container, fragment1, "1").hide(fragment1).commit();
        }

    }

    private void setUiElements() {
        //Setting the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.plan));
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setTitle(getString(R.string.plan));
        //image_toolbar = findViewById(R.id.image_toolbar);

        //Setting the navigation drawer
        drawerLayout = findViewById(R.id.drawer_home);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        animatedSwitch = menuItem.getActionView().findViewById(R.id.pin);
        animatedSwitch.setChecked(false);
        animatedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (animatedSwitch.isChecked()) ? getString(R.string.switch_activate) :
                        getString(R.string.switch_deactivate), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                if (animatedSwitch.isChecked()) {
                    startLocationService();
                } else {
                    stopService(new Intent(HomeActivity.this, LocationService.class));


                }

            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Setting the bottom navigation view
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getUserDetails();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Determine if the device can use Google services
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getUserDetails();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void saveUserLocation() {

        if (mUserLocation != null) {
            DocumentReference locationRef = mDb
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }

    private void getUserLocation(User user) {
        DocumentReference locationsRef = mDb
                .collection(getString(R.string.collection_user_locations))
                .document(user.getUser_id());

        locationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().toObject(UserLocation.class) != null) {

                        mUserLocations.add(task.getResult().toObject(UserLocation.class));

                    }
                }
            }
        });

    }

    private void passDataToGeolocationActivity() {
        Intent intent = new Intent(HomeActivity.this, GeolocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("intent_user_list", mUserList);
        bundle.putParcelableArrayList("intent_user_locations", mUserLocations);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getTotalUsers() {

        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_users));

        mUserListEventListener = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {

                            // Clear the list and add all the users again
                            mUserList.clear();
                            mUserList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                User user = doc.toObject(User.class);
                                mUserList.add(user);
                                getUserLocation(user);
                            }

                            Log.d(TAG, "onEvent: user list size: " + mUserList.size());
                        }
                    }
                });
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                HomeActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.shid.swissaid.Services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        ((Offline) (getApplicationContext())).setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        } else {
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                   // startLocationService();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.notification_chat);
        View actionView = menuItem.getActionView();
        count_txt = actionView.findViewById(R.id.notification_badge);
        unreadMsgEventListener();
        setBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    private void unreadMsgEventListener() {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver() != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                            unread++;
                            setBadge();
                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBadge() {

        if (count_txt != null) {
            if (unread == 0) {
                if (count_txt.getVisibility() != View.GONE) {
                    count_txt.setVisibility(View.GONE);
                }
            } else {
                count_txt.setText(String.valueOf(Math.min(unread, 99)));
                if (count_txt.getVisibility() != View.VISIBLE) {
                    count_txt.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification_chat:
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavigationHeader() {
        View header = navigationView.getHeaderView(0);
        profile_image = header.findViewById(R.id.image_nav);
        name = header.findViewById(R.id.name_nav);
        username = header.findViewById(R.id.username_nav);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                username.setText(user.getUsername());
                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.icon);
                } else if (!user.getImagePath().equals("default")) {
                    File file = new File(user.getImagePath());
                    if (file.exists()) {
                        Glide.with(getApplicationContext()).load(file).into(profile_image);
                    }
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getUserDetails();

            } else {
                getLocationPermission();
            }
        }

        if (isLocationServiceRunning()){
            animatedSwitch.setChecked(true);
        } else{
            animatedSwitch.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUserListEventListener != null){
            mUserListEventListener.remove();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    private void logOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.dialog_log_out_title));

        alertDialogBuilder
                .setMessage(getString(R.string.dialog_log_out_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_log_out_btn_yes), (dialog, id) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent4 = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent4);
                    HomeActivity.this.finish();
                })
                .setNegativeButton(getString(R.string.dialog_log_out_btn_no), (dialog, id) -> {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                saved_fragment = fragment1;
                return true;
/*
            case R.id.navigation_report:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                saved_fragment = fragment2;
                return true;
*/
            case R.id.navigation_backup:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                saved_fragment = fragment3;
                return true;
/*
            case R.id.navigation_setting:
                fm.beginTransaction().hide(active).show(fragment4).commit();
                active = fragment4;
                saved_fragment = fragment4;
                return true;

 */
        }
        return false;
    };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        View parentLayout = findViewById(android.R.id.content);
        switch (menuItem.getItemId()) {
            case R.id.chat:
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.geolocation:
                //Snackbar.make(parentLayout, getString(R.string.feature), Snackbar.LENGTH_SHORT).show();
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return false;
                } else if (!animatedSwitch.isChecked()){
                    Snackbar.make(parentLayout, getString(R.string.switch_status), Snackbar.LENGTH_SHORT).show();
                }else{
                    passDataToGeolocationActivity();
                }


                break;
            case R.id.menu_setting:
                Intent intent1 = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.exit:
                logOut();
                break;
            case R.id.about:
                Snackbar.make(parentLayout, getString(R.string.feature), Snackbar.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
