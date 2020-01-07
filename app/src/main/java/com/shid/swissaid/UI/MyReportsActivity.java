package com.shid.swissaid.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shid.swissaid.Adapters.RecycleViewMyReportsAdapter;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Upload;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;
import com.shid.swissaid.Util.SimpleDividerItemDecoration;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;

public class MyReportsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyReportsActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    //private ListView listView;
    private RecyclerView recyclerView;
    private RecycleViewMyReportsAdapter adapter;

    //database reference to get uploads data
    private FirebaseFirestore mDb;

    //list to store uploads data
    public static ArrayList<Upload> uploadList = new ArrayList<Upload>();
    private Toolbar toolbar;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SearchView searchView_custom;

    private RadioGroup radioGroup;
    private RadioButton radio_date;
    private RadioButton radio_mission;
    private RadioButton radio_number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        setUiElements();
        user = new User();
        setFirebaseInstances();
        uploadList = new ArrayList<>();
        revealMenuItems();
        uploadData();

    }

    private void setFirebaseInstances() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setUiElements() {
        //Setting up the Toolbar
        toolbar = findViewById(R.id.toolbar_my);
        toolbar.inflateMenu(R.menu.test_menu);
        toolbar.setTitle(getString(R.string.title_my_reports));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.getMenu().findItem(R.id.action_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                refresh();

                return false;
            }
        });

        radioGroup = findViewById(R.id.radio_group_my);
        radio_date = findViewById(R.id.radio_date_my);
        radio_mission = findViewById(R.id.radio_mission_my);
        radio_number = findViewById(R.id.radio_number_my);
        radio_mission.toggle();

        searchView_custom = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();

        recyclerView = findViewById(R.id.recycleview_my);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));

        swipeRefreshLayout = findViewById(R.id.swipe_my);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
    }

    @Override
    public void onRefresh() {
        refresh();

    }

    private void revealMenuItems() {
        toolbar.getMenu().findItem(R.id.action_refresh).setVisible(true);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(true);
    }

    private void refresh() {
        android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
                .setContext(MyReportsActivity.this)
                .setMessage(getString(R.string.refresh_my))
                .setCancelable(false)
                .build();
        waitingDialog.show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                startActivity(getIntent());
                finish();
                waitingDialog.dismiss();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2000); // <-- the "1000" is the delay time in miliseconds.
    }


    public void showUpdateDialog(String url, String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyReportsActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);

        final Button btn_delete = dialogView.findViewById(R.id.btnDelete);
        final Button btn_download = dialogView.findViewById(R.id.btnDownload);

        dialogBuilder.setTitle(getString(R.string.dialog_title_custom_fragment));

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                Log.d("TAG", "link: " + Uri.parse(url));
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(id);

            }
        });

    }

    private void deleteData(String id) {

        DocumentReference documentReference = mDb.collection(Constant.DATABASE_PATH_UPLOADS)
                .document(id);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, getString(R.string.snack_delete_custom_fragment), Snackbar.LENGTH_SHORT).show();
                    refresh();
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, getString(R.string.snack_error_custom_fragment), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {

        //getting the database reference
        mDb = FirebaseFirestore.getInstance();
        Log.d(TAG, "name: + " + firebaseUser.getDisplayName());
        //DocumentReference documentReference = mDb.collection(Constant.DATABASE_PATH_UPLOADS);
        CollectionReference reference = mDb.collection(Constant.DATABASE_PATH_UPLOADS);
        reference.whereEqualTo("name_employee", firebaseUser.getDisplayName().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (uploadList.size() > 0) {
                        uploadList.clear();
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Upload upload = document.toObject(Upload.class);
                        uploadList.add(upload);


                    }

                    adapter = new RecycleViewMyReportsAdapter(MyReportsActivity.this);
                    adapter.setOnItemClickListener(new RecycleViewMyReportsAdapter.onRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClickListener(View view, int position) {
                            Upload upload = uploadList.get(position);

                            showUpdateDialog(upload.getUrl(), upload.getId());
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(MyReportsActivity.this));
                    recyclerView.setAdapter(adapter);


                    searchView_custom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (radio_mission.isChecked()) {
                                adapter.filter(newText);
                            } else if (radio_date.isChecked()) {
                                adapter.filter_date(newText);
                            } else if (radio_number.isChecked()) {
                                adapter.filter_number(newText);
                            }

                            return false;
                        }
                    });


                } else {
                    Toast.makeText(MyReportsActivity.this, getString(R.string.toast_fail_files_custom_fragment), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
