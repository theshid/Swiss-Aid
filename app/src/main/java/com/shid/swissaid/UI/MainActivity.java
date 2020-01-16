package com.shid.swissaid.UI;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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
import com.shid.swissaid.UI.ChatFragment.ChatFragment;
import com.shid.swissaid.UI.ChatFragment.ProfileFragment;
import com.shid.swissaid.UI.ChatFragment.UsersFragment;
import com.shid.swissaid.Util.Offline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shid.swissaid.Constant.Constant.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private CircleImageView profile_image;
    private CircleImageView image_nav;
    private TextView username_nav;
    private TextView name_nav;
    private TextView username;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private NavigationView navigationView;
    private SwitchCompat animatedSwitch;
    private FirebaseFirestore mDb;
    private UserLocation mUserLocation;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ListenerRegistration mUserListEventListener;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUiElements();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getTotalUsers();
        eventListener();
        unreadMsgEventListener();
        setNavigationHeader();
        setSwitchStatus();
    }

    private void setUiElements() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        drawerLayout = findViewById(R.id.drawer_main);
        navigationView = findViewById(R.id.nav_view);
        //Setting the toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
                    stopService(new Intent(MainActivity.this, LocationService.class));
                }

            }
        });

    }

    private void setSwitchStatus() {
        if (isLocationServiceRunning()) {
            animatedSwitch.setChecked(true);
        } else {
            animatedSwitch.setChecked(false);
        }
    }

    private void setNavigationHeader() {
        View header = navigationView.getHeaderView(0);
        image_nav = header.findViewById(R.id.image_nav);
        name_nav = header.findViewById(R.id.name_nav);
        username_nav = header.findViewById(R.id.username_nav);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name_nav.setText(user.getName());
                username_nav.setText(user.getUsername());
                if (user.getImageUrl().equals("default")) {
                    image_nav.setImageResource(R.mipmap.icon);
                } else if (!user.getImagePath().equals("default")) {
                    File file = new File(user.getImagePath());
                    if (file.exists()) {
                        Glide.with(getApplicationContext()).load(file).into(image_nav);
                    }
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(image_nav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setTitle(getString(R.string.dialog_log_out_title));

        alertDialogBuilder
                .setMessage(getString(R.string.dialog_log_out_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_log_out_btn_yes), (dialog, id) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent4 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent4);
                    MainActivity.this.finish();
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

    private void eventListener() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
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

    private void unreadMsgEventListener() {
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                        unread++;
                    }

                }
                if (unread == 0) {
                    viewPagerAdapter.addFragment(new ChatFragment(), "Chats");
                } else {
                    viewPagerAdapter.addFragment(new ChatFragment(), "(" + unread + ") Chats");
                }

                viewPagerAdapter.addFragment(new UsersFragment(), getString(R.string.users));
                viewPagerAdapter.addFragment(new ProfileFragment(), getString(R.string.profile));

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        Intent intent = new Intent(MainActivity.this, GeolocationActivity.class);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        View parentLayout = findViewById(android.R.id.content);
        switch (menuItem.getItemId()) {
            case R.id.home:
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.geolocation:
                // Snackbar.make(parentLayout, getString(R.string.feature), Snackbar.LENGTH_SHORT).show();
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return false;
                } else {
                    passDataToGeolocationActivity();

                }
                break;
            case R.id.menu_setting:
                Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                return true;
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

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                MainActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.shid.swissaid.Services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        setSwitchStatus();
        getUserDetails();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserListEventListener != null) {
            mUserListEventListener.remove();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}

