package com.shid.swissaid.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shid.swissaid.Adapters.HomeAdapter;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.R;
import com.shid.swissaid.UI.PdfUi.FormActivity;
import com.shid.swissaid.UI.PdfUi.TemplatePDF;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

import static android.app.Activity.RESULT_CANCELED;

public class CustomFragment extends Fragment {

    private static final String TAG = "CustomFragment";

    private ListView listView;

    private HomeAdapter homeAdapter;

    private String[] menu;

    //private ImageView header_en, header_fr;

    private FloatingActionButton fab;

    public static final int NEW_FORM_REQUEST_CODE = 1;
    public static final int SELECT_PICTURES = 34;

    private static final String DATA_RECEIVED = "data_received";

    private boolean dataReceived = false;
    private String nameProject;
    private String duration;
    private String projectNumber;
    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String q5;
    private String q6;
    private String q7;
    private String q8;
    private String q9;
    private String q10;
    private String q11;
    private String q12;
    private String q13;


    private Button btn_pdf;

    private int count;

    private String[] header = {"Question", "Answer"};
    private String pointText = "It cant be she speaks Spanish with such an accent";
    private String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    private TemplatePDF templatePDF;

    private Handler mainHandler = new Handler();

    private FlipperLayout flipperLayout;



    private FirebaseUser firebaseUser;

    public CustomFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        Log.d(TAG, "language + " + Locale.getDefault().getLanguage());
        if (isAdded()) {
            menu = getResources().getStringArray(R.array.rowCustom);
            homeAdapter = new HomeAdapter(getContext(), menu);
        }

        setUiElements(view);
        setFirebaseInstances();
        //Display correct header depending on the language
        displayHeader();
        setLayout();


        //If we don't have permission we prompt the user
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSION_STRORAGE, Constant.REQUEST_EXTERNAL_STORAGE);
        }

        return view;
    }

    private void setUiElements(View view) {

        //header_en = view.findViewById(R.id.banner_home);
       // header_fr = view.findViewById(R.id.banner_home_fr);
        flipperLayout = view.findViewById(R.id.flipper_layout);


        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FormActivity.class);
                startActivityForResult(intent, NEW_FORM_REQUEST_CODE);
            }
        });

        btn_pdf = view.findViewById(R.id.btn_pdf);
        btn_pdf.setVisibility(View.INVISIBLE);
        btn_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPdfDialog();
            }
        });



        listView = view.findViewById(R.id.listView_custom);
        listView.setAdapter(homeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.d("TAG", "position " + position);
                        Intent intent = new Intent(getContext(), MyReportsActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        Log.d("TAG", "position " + position);
                        Intent intent1 = new Intent(getContext(), AllReportActivity.class);
                        startActivity(intent1);
                        break;


                }
            }
        });
    }

    private void setLayout() {

        for (int i = 0; i < 4; i++) {
            FlipperView view = new FlipperView(getContext());
            if (i == 0){
                view.setImageDrawable(R.raw.slide1)
                        .setDescriptionBackgroundColor(Color.TRANSPARENT)
                        .resetDescriptionTextView();
                flipperLayout.addFlipperView(view);
                flipperLayout.setCircleIndicatorHeight(60);
                flipperLayout.setCircleIndicatorWidth(200);
                flipperLayout.removeCircleIndicator();
                flipperLayout.showCircleIndicator();
                view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                    @Override
                    public void onFlipperClick(FlipperView flipperView) {
                        Toast.makeText(getContext()
                                , "Here " + (flipperLayout.getCurrentPagePosition() + 1)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (i == 1){
                view.setImageDrawable(R.raw.slide2)
                        .setDescriptionBackgroundColor(Color.TRANSPARENT)
                        .resetDescriptionTextView();
                flipperLayout.addFlipperView(view);
                flipperLayout.setCircleIndicatorHeight(60);
                flipperLayout.setCircleIndicatorWidth(200);
                flipperLayout.removeCircleIndicator();
                flipperLayout.showCircleIndicator();
                view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                    @Override
                    public void onFlipperClick(FlipperView flipperView) {
                        Toast.makeText(getContext()
                                , "Here " + (flipperLayout.getCurrentPagePosition() + 1)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (i == 2){
                view.setImageDrawable(R.raw.slide3)
                        .setDescriptionBackgroundColor(Color.TRANSPARENT)
                        .resetDescriptionTextView();
                flipperLayout.addFlipperView(view);
                flipperLayout.setCircleIndicatorHeight(60);
                flipperLayout.setCircleIndicatorWidth(200);
                flipperLayout.removeCircleIndicator();
                flipperLayout.showCircleIndicator();
                view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                    @Override
                    public void onFlipperClick(FlipperView flipperView) {
                        Toast.makeText(getContext()
                                , "Here " + (flipperLayout.getCurrentPagePosition() + 1)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (i == 3){
                view.setImageDrawable(R.raw.slide4)
                        .setDescriptionBackgroundColor(Color.TRANSPARENT)
                        .resetDescriptionTextView();
                flipperLayout.addFlipperView(view);
                flipperLayout.setCircleIndicatorHeight(60);
                flipperLayout.setCircleIndicatorWidth(200);
                flipperLayout.removeCircleIndicator();
                flipperLayout.showCircleIndicator();
                view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                    @Override
                    public void onFlipperClick(FlipperView flipperView) {
                        Toast.makeText(getContext()
                                , "Here " + (flipperLayout.getCurrentPagePosition() + 1)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    private void setFirebaseInstances() {
        //getting firebase objects
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }





    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
//**These following line is the important one!
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
    }

    private void createPdf(String nameFile) {
        templatePDF = new TemplatePDF(getContext());
        templatePDF.openDocument(nameFile);
        templatePDF.addImage();
        templatePDF.addMetaData("Client", "Ventes", "Marines");
        templatePDF.addTitles("Rapport de Projet", "Swiss Aid", currentDate);
        //templatePDF.createPdf(header, getClients());

        templatePDF.addParagraphQ(getString(R.string.nom_projet));
        templatePDF.addParagraph(nameProject);
        templatePDF.addParagraphQ(getString(R.string.duree_projet));
        templatePDF.addParagraph(duration);
        templatePDF.addParagraphQ(getString(R.string.ta));
        templatePDF.addParagraph(projectNumber);

        templatePDF.addParagraphQ(getString(R.string.q1));
        templatePDF.addParagraph(q1);
        templatePDF.addParagraphQ(getString(R.string.q2));
        templatePDF.addParagraph(q2);
        templatePDF.addParagraphQ(getString(R.string.q3));
        templatePDF.addParagraph(q3);
        templatePDF.addParagraphQ(getString(R.string.q4));
        templatePDF.addParagraph(q4);
        templatePDF.addParagraphQ(getString(R.string.q5));
        templatePDF.addParagraph(q5);
        templatePDF.addParagraphQ(getString(R.string.q6));
        templatePDF.addParagraph(q6);
        templatePDF.addParagraphQ(getString(R.string.q7));
        templatePDF.addParagraph(q7);
        templatePDF.addParagraphQ(getString(R.string.q8));
        templatePDF.addParagraph(q8);
        templatePDF.addParagraphQ(getString(R.string.q9));
        templatePDF.addParagraph(q9);
        templatePDF.addParagraphQ(getString(R.string.q10));
        templatePDF.addParagraph(q10);
        templatePDF.addParagraphQ(getString(R.string.q11));
        templatePDF.addParagraph(q11);
        templatePDF.addParagraphQ(getString(R.string.q12));
        templatePDF.addParagraph(q12);
        templatePDF.addParagraphQ(getString(R.string.q13));
        templatePDF.addParagraph(q13);
        templatePDF.addParagraphQ(getString(R.string.annexe));
        templatePDF.closeDocument();
    }


    public void viewPdfDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.submit_dialog, null);

        dialogBuilder.setView(dialogView);

        final Button btnViewPdf = dialogView.findViewById(R.id.btnViewPdf);
        //final Button btnSubmitPdf = dialogView.findViewById(R.id.btnSubmitPdf);
        final Button btnSubmitPdfImg = dialogView.findViewById(R.id.btnSubmitPdfImg);

        dialogBuilder.setTitle(getString(R.string.create_pdf));


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        /*
        btnSubmitPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadTask uploadTask = new UploadTask();
                uploadTask.run();
                //createPdf(nameProject);
                //templatePDF.uploadFile(nameProject,projectNumber);
            }
        });
        */

        btnViewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf(nameProject);
                templatePDF.appPDF(getActivity());
            }
        });



        btnSubmitPdfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                getImage();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK
                && requestCode == NEW_FORM_REQUEST_CODE
                && data != null
                && data.hasExtra(FormActivity.STATE_NEW_FORM_ADDED)) {

            dataReceived = true;

            nameProject = data.getExtras().getString(FormActivity.STATE_NAME_PROJECT);
            duration = data.getExtras().getString(FormActivity.STATE_DURATION_STEP);
            projectNumber = data.getExtras().getString(FormActivity.STATE_TA);// project number
            q1 = data.getExtras().getString(FormActivity.STATE_Q1);
            q2 = data.getExtras().getString(FormActivity.STATE_Q2);
            q3 = data.getExtras().getString(FormActivity.STATE_Q3);
            q4 = data.getExtras().getString(FormActivity.STATE_Q4);
            q5 = data.getExtras().getString(FormActivity.STATE_Q5);
            q6 = data.getExtras().getString(FormActivity.STATE_Q6);
            q7 = data.getExtras().getString(FormActivity.STATE_Q7);
            q8 = data.getExtras().getString(FormActivity.STATE_Q8);
            q9 = data.getExtras().getString(FormActivity.STATE_Q9);
            q10 = data.getExtras().getString(FormActivity.STATE_Q10);
            q11 = data.getExtras().getString(FormActivity.STATE_Q11);
            q12 = data.getExtras().getString(FormActivity.STATE_Q12);
            q13 = data.getExtras().getString(FormActivity.STATE_Q13);

            //pointText = q8;

            //createPdf(nameProject);
            btn_pdf.setVisibility(View.VISIBLE);


        } else if (requestCode == SELECT_PICTURES) {
            templatePDF = new TemplatePDF(getContext());
            templatePDF.openDocument(nameProject);
            templatePDF.addImage();
            templatePDF.addMetaData("Client", "Ventes", "Marines");
            templatePDF.addTitles(getString(R.string.pdf_title), "Swiss Aid", currentDate);
            //templatePDF.createPdf(header, getClients());

            templatePDF.addParagraphQ(getString(R.string.nom_projet));
            templatePDF.addParagraph(nameProject);
            templatePDF.addParagraphQ(getString(R.string.duree_projet));
            templatePDF.addParagraph(duration);
            templatePDF.addParagraphQ(getString(R.string.ta));
            templatePDF.addParagraph(projectNumber);

            templatePDF.addParagraphQ(getString(R.string.q1));
            templatePDF.addParagraph(q1);
            templatePDF.addParagraphQ(getString(R.string.q2));
            templatePDF.addParagraph(q2);
            templatePDF.addParagraphQ(getString(R.string.q3));
            templatePDF.addParagraph(q3);
            templatePDF.addParagraphQ(getString(R.string.q4));
            templatePDF.addParagraph(q4);
            templatePDF.addParagraphQ(getString(R.string.q5));
            templatePDF.addParagraph(q5);
            templatePDF.addParagraphQ(getString(R.string.q6));
            templatePDF.addParagraph(q6);
            templatePDF.addParagraphQ(getString(R.string.q7));
            templatePDF.addParagraph(q7);
            templatePDF.addParagraphQ(getString(R.string.q8));
            templatePDF.addParagraph(q8);
            templatePDF.addParagraphQ(getString(R.string.q9));
            templatePDF.addParagraph(q9);
            templatePDF.addParagraphQ(getString(R.string.q10));
            templatePDF.addParagraph(q10);
            templatePDF.addParagraphQ(getString(R.string.q11));
            templatePDF.addParagraph(q11);
            templatePDF.addParagraphQ(getString(R.string.q12));
            templatePDF.addParagraph(q12);
            templatePDF.addParagraphQ(getString(R.string.q13));
            templatePDF.addParagraph(q13);

            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                            templatePDF.addPic(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                templatePDF.closeDocument();
                templatePDF.appPDF(getActivity());
                //templatePDF.uploadFile(nameProject, projectNumber);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(),getString(R.string.image_null),Toast.LENGTH_LONG).show();
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        } else {
            dataReceived = false;


        }

    }


    private ArrayList<String[]> getClients() {
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(new String[]{getString(R.string.file), q7});
        rows.add(new String[]{getString(R.string.name), nameProject});
        rows.add(new String[]{getString(R.string.ta), projectNumber});
        rows.add(new String[]{getString(R.string.date), q6});
        rows.add(new String[]{getString(R.string.budget), duration});
        rows.add(new String[]{getString(R.string.iteneraire), q1});
        rows.add(new String[]{getString(R.string.membre), q2});
        rows.add(new String[]{getString(R.string.rencontre), q4});
        rows.add(new String[]{getString(R.string.objectif), q3});
        rows.add(new String[]{getString(R.string.resultat), q5});

        return rows;
    }

    private void displayHeader() {
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
          //  header_en.setVisibility(View.VISIBLE);
         //   header_fr.setVisibility(View.GONE);
        } else if (Locale.getDefault().getLanguage().contentEquals("fr")) {
          //  header_fr.setVisibility(View.VISIBLE);
        //    header_en.setVisibility(View.GONE);
        }
    }

    //Method that checks the permission depending on the version of the phone
    private boolean storageAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return permission == PackageManager.PERMISSION_GRANTED;
        }

        return true;

    }




    class UploadTask implements Runnable{

        @Override
        public void run() {
            createPdf(nameProject);
            templatePDF.uploadFile( nameProject, projectNumber);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"file uploaded",Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    /*
    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        //ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
          //  pdLoading.setMessage("\tLoading...");
           // pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            createPdf(nameProject);
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            templatePDF.uploadFile( nameProject, projectNumber);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            //pdLoading.dismiss();
            Toast.makeText(getContext(),"file uploaded",Toast.LENGTH_LONG).show();
        }

    }
    */


}
