package com.shid.swissaid.UI.PdfUi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.shid.swissaid.Constant.Constant;
import com.shid.swissaid.Model.Upload;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import static com.itextpdf.text.factories.GreekAlphabetFactory.getString;

public class TemplatePDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font rText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private Font fTextHigh = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore mDb = FirebaseFirestore.getInstance();


    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void openDocument(String nom) {
        createFile(nom);
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            pdfWriter.setFullCompression();
            document.open();
        } catch (Exception e) {
            Log.e("open document", e.toString());
        }
    }


    public void createFile(String nameFile) {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid");
        File subFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/PDF");

        if (!folder.exists()) {
            folder.mkdir();
            if (!subFolder.exists()) {
                subFolder.mkdirs();
            }
        }
        pdfFile = new File(subFolder, nameFile + ".pdf");
    }

    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addPic(Bitmap bitmap) {
        try {
            int indentation = 0;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            Image image = null;

            try {
                image = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - indentation) / image.getWidth()) * 50;
                image.scalePercent(scaler);
                image.setAlignment(Element.ALIGN_CENTER);
            } catch (BadElementException e) {
                e.printStackTrace();
            }
            try {

                document.add(image);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            return;
        }
    }

    public void addImage() {
// load image
        try {
            int indentation = 0;
            // get input stream
            InputStream ims = context.getAssets().open("logo.png");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
            Image image = null;

            try {
                image = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - indentation) / image.getWidth()) * 50;
                image.scalePercent(scaler);
                image.setAlignment(Element.ALIGN_CENTER);
            } catch (BadElementException e) {
                e.printStackTrace();
            }
            try {

                document.add(image);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            return;
        }
    }

    public void addTitles(String title, String subTitle, String date) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subTitle, fSubTitle));
            addChildP(new Paragraph(date, fTextHigh));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("open document", e.toString());
        }
    }

    private void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, rText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(3);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("open document", e.toString());
        }
    }

    public void addParagraphQ(String text) {
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(3);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("open document", e.toString());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> clients) {
        try {

            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(20);
            PdfPCell pdfPCell;
            int indexC = 0;
            while (indexC < header.length) {
                Log.d("TAG", "header length" + header.length);
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(new BaseColor(0, 153, 255));
                pdfPTable.addCell(pdfPCell);

                if (header.length == 2) {
                    // Defiles the relative width of the columns
                    float[] columnWidths = new float[]{10f, 20f};
                    pdfPTable.setWidths(columnWidths);
                }

            }

            for (int indexR = 0; indexR < clients.size(); indexR++) {
                String[] row = clients.get(indexR);
                for (indexC = 0; indexC < header.length; indexC++) {
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("create table", e.toString());
        }
    }

/*
    public void viewPDF(){
        Intent intent = new Intent(context,ViewPDFActivity.class);
        intent.putExtra("path",pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    */

    public void appPDF(Activity activity) {
        if (pdfFile.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT < 24) {
                uri = Uri.fromFile(pdfFile);
            } else {
                uri = FileProvider.getUriForFile(context, context.getApplicationContext()
                        .getPackageName() + ".provider", pdfFile);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), context.getString(R.string.error_pdf)
                        , Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "Archive not found"
                    , Toast.LENGTH_LONG).show();
        }
    }





    public void uploadFile( String nameProject, String projectNumber) {

        if (pdfFile.exists()) {
            Uri data;
            if (Build.VERSION.SDK_INT < 24) {
                data = Uri.fromFile(pdfFile);
            } else {
                data = FileProvider.getUriForFile(context, context.getApplicationContext()
                        .getPackageName() + ".provider", pdfFile);
            }

            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            FirebaseFirestore mDb = FirebaseFirestore.getInstance();
            final StorageReference sRef = mStorageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
            sRef.putFile(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("TAG", "onSuccess: data= " + uri.toString());
                                    //set up the id in Firebase
                                    //using a call to document() w/o passing argument, a unique id is
                                    //generated
                                    DocumentReference ref = mDb.collection(Constant.DATABASE_PATH_UPLOADS).document();

                                    String myId = ref.getId();
                                    Log.d("Template",myId);
                                    String url = uri.toString();
                                    Log.d("Template",url);
                                    //set up the date
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ");
                                    Date date = new Date();
                                    String strDate = dateFormat.format(date).toString();
                                    Log.d("Template",strDate);

                                    //Getting the user details

                                    DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                                            .document(FirebaseAuth.getInstance().getUid());

                                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Template", "onComplete: successfully set the user client.");
                                                User user = task.getResult().toObject(User.class);
                                                //Watch this
                                                // ((UserClient)(getContext())).setUser(user);

                                                Upload upload = new Upload();
                                                upload.setMission(nameProject);
                                                upload.setUser(user);
                                                upload.setName(nameProject);
                                                upload.setNumero_ta(projectNumber);
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
                                                           Toast.makeText(context.getApplicationContext(), context.getString(R.string.snack_file_upload_fragment), Toast.LENGTH_LONG).show();

                                                        } else {

                                                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.snack_error_upload_fragment), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("Failure","fail to get user data");
                        }
                    });

        }


    }
}
