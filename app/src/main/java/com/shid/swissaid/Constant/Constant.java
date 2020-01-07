package com.shid.swissaid.Constant;

public class Constant {

    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";

    public static final String STORAGE_PATH_REPORTS = "reports/";
    public static final String DATABASE_PATH_REPORTS = "reports";

    public static final String STORAGE_PATH_REPORTS_FR = "rapports/";
    public static final String DATABASE_PATH_REPORTS_FR = "rapports";

    public static final int REQUEST_SIGNUP = 0;

    //this is the pic pdf code used in file chooser
    public final static int PICK_PDF_CODE = 2342;
    public static final int GALLERY_PICK = 1;
    public static final int IMAGE_CAMERA_REQUEST = 2;
    public static final int IMAGE_REQUEST = 1;

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //permission
    public static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 4536;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final  int PERMISSIONS_REQUEST_ENABLE_GPS = 6787;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    //Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final String[] PERMISSION_STRORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
