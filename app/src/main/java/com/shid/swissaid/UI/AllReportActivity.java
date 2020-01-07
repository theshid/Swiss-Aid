package com.shid.swissaid.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shid.swissaid.Adapters.RecycleViewAllReportsAdapter;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Upload;
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


public class AllReportActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "AllReportActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    /*private ListView listView; */

    //database reference to get uploads data
    private FirebaseFirestore mDb;

    //list to store uploads data
    public static ArrayList<Upload> uploadList = new ArrayList<Upload>();


    private Toolbar toolbar2;

    private RecyclerView recyclerView;
    private RecycleViewAllReportsAdapter adapter;


    private SearchView searchView_custom;

    private RadioGroup radioGroup;
    private RadioButton radio_name;
    private RadioButton radio_date;
    private RadioButton radio_mission;
    private RadioButton radio_number;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_report);

        setToolbar();

        setUiElements();

        uploadList = new ArrayList<>();

        setSwipeRefreshLayout();

       //reveals button on the tools bar that are used in the activity
        revealMenuItems();

        uploadData();

    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_all);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
    }

    private void setUiElements() {
        radioGroup = findViewById(R.id.radio_group_all);
        radio_date = findViewById(R.id.radio_date_all);
        radio_mission = findViewById(R.id.radio_mission_all);
        radio_number = findViewById(R.id.radio_number_all);
        radio_name = findViewById(R.id.radio_name_all);
        //Mission is the radio button that is selected by default
        radio_mission.toggle();

        //setting recycler view
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
    }

    private void setToolbar() {
        //Setting up the Toolbar
        toolbar2 = findViewById(R.id.toolbar_allReport);
        toolbar2.inflateMenu(R.menu.test_menu);
        toolbar2.setTitle(getString(R.string.title_all_reports));

        //When pressing the refresh button the activity is refreshed
        toolbar2.getMenu().findItem(R.id.action_refresh).setOnMenuItemClickListener(item -> {
            refresh();
            return false;
        });

        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView_custom = (SearchView) toolbar2.getMenu().findItem(R.id.action_search).getActionView();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(){
        android.app.AlertDialog waitingDialog = new SpotsDialog.Builder()
                .setContext(AllReportActivity.this)
                .setMessage(getString(R.string.refresh_my))
                .setCancelable(false)
                .build();
        waitingDialog.show();
        Runnable r = new Runnable() {
            @Override
            public void run(){
                startActivity(getIntent());
                finish();
                waitingDialog.dismiss();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2000); // <-- the "1000" is the delay time in milliseconds.
    }



    private void revealMenuItems(){
        toolbar2.getMenu().findItem(R.id.action_refresh).setVisible(true);
        toolbar2.getMenu().findItem(R.id.action_search).setVisible(true);
    }
    //Dialog to open reports in browser
    private void showUpdateDialog(String url, String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AllReportActivity.this);

        LayoutInflater inflater = getLayoutInflater();


        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.update_dialog2,null);

        dialogBuilder.setView(dialogView);


        final Button btn_download = dialogView.findViewById(R.id.btnDownload) ;


        dialogBuilder.setTitle(getString(R.string.dialog_title_custom_fragment));

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                Log.d(TAG, "link: " + Uri.parse(url));
                startActivity(intent);
            }
        });


    }



    private void uploadData(){

        //getting the database reference
        mDb = FirebaseFirestore.getInstance();

        //DocumentReference documentReference = mDb.collection(Constant.DATABASE_PATH_UPLOADS);
        CollectionReference reference =  mDb.collection(Constant.DATABASE_PATH_UPLOADS);
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    // To avoid repetition in the data , we clear the arrayList before uploading
                    if(uploadList.size() > 0){
                        uploadList.clear();
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Upload upload = document.toObject(Upload.class);
                        uploadList.add(upload);

                    }

                    adapter = new RecycleViewAllReportsAdapter(AllReportActivity.this);
                    adapter.setOnItemClickListener((view, position) -> {
                        Upload upload = uploadList.get(position);

                        showUpdateDialog(upload.getUrl(),upload.getId());
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(AllReportActivity.this));
                    recyclerView.setAdapter(adapter);

                  //Method for the search box to filter data
                    searchView_custom.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (radio_mission.isChecked()){
                                adapter.filter(newText);

                            } else if (radio_date.isChecked()){
                                adapter.filter_date(newText);

                            }else if (radio_name.isChecked()){
                                adapter.filter_name(newText);

                            } else if (radio_number.isChecked()){
                                adapter.filter_number(newText);

                            }

                            return false;
                        }
                    });


                } else{
                    Toast.makeText(AllReportActivity.this,getString(R.string.toast_fail_files_custom_fragment), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
