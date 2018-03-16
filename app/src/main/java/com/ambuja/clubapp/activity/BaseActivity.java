package com.ambuja.clubapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambuja.clubapp.BuildConfig;
import com.ambuja.clubapp.MyApplication;
import com.ambuja.clubapp.R;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.InputFilterMinMax;
import com.ambuja.clubapp.utils.MySpannable;
import com.ambuja.clubapp.utils.PrefStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.wandrip.http.RequestParams;
import com.wandrip.http.SyncEventListner;
import com.wandrip.http.SyncManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.Gravity.CENTER;


public class BaseActivity extends AppCompatActivity implements SyncEventListner, LocationListener {


    public PrefStore store;
    public static SyncManager syncManager;
    public AsyncTask<Void, Void, Void> mRegisterTask;
    public String regId;
    public boolean isGPSEnabled = false;
    public boolean isNetworkEnabled = false;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public MyApplication myApplication;
    private LocationManager locationManager;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "ClubApp Images";
    private static final int PERMISSION_REQUEST_CODE = 1;
    public float LOCATION_UPDATE_DISTANCE = 10;
    public long LOCATION_UPDATE_TIME_INTERVAL = 1000;
    public static Dialog dialogPD = null;
    private boolean isClicked = false;
    private ProgressDialog pDialog;
    public static final int REQUEST_SELECT_PICTURE = 2;
    public static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    private static AnimationDrawable progressAnimation;
    private MediaPlayer mPlay;
    public static Toast toast;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private AlertDialog mAlertDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        taskBarColorChange();
        transitionSlideInHorizontal();
        myApplication = (MyApplication) getApplication();
        checkNetwork();
        store = new PrefStore(this);
        syncManager = SyncManager.getInstance(this);
        syncManager.setBaseUrl(Constants.SERVER_URL_REMOTE);
        initGCM();
        hideSoftKeyboard();
        /*FontsOverride.setDefaultFont(this);
        FontsOverride.setAppFont(this);*/


    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }


    protected static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        store.setLoginStatus("on");
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver
                (mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    protected boolean checkReadWritePermission() {

        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (read == PackageManager.PERMISSION_GRANTED &&
                write == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            requestReadWritePermission();
            return false;

        }
    }

    private void requestReadWritePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            Toast.makeText(this, " permission allows. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    protected boolean checkPermission() {
        int fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int call = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (fine == PackageManager.PERMISSION_GRANTED &&
                coarse == PackageManager.PERMISSION_GRANTED &&
                camera == PackageManager.PERMISSION_GRANTED &&
                read == PackageManager.PERMISSION_GRANTED &&
                write == PackageManager.PERMISSION_GRANTED &&
                call == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            requestPermission();
            return false;

        }
    }


    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
            Toast.makeText(this, " permission allows. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showToast("Permission Granted");


                } else {

                    showToast("Permission Denied");

                }
                break;
        }
    }


    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (Exception e) {
            Log.e("TAG Date", "ParseException - dateFormat");
        }

        return outputDate;

    }

    protected void checkNetwork() {

        if (isNetworkAvailable())
            return;


        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        myDialog.setTitle("Network Connection");
        myDialog.setMessage("You are not connected to any network.Press ok to change settings");
        myDialog.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent settings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settings);
                dialog.dismiss();

            }
        });
        myDialog.setNegativeButton("Exit", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                exitFromApp();
                dialog.dismiss();
            }
        });
        myDialog.setCancelable(false);
        AlertDialog alertd = myDialog.create();
        alertd.show();
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    protected void exitFromApp() {

        store.setString("clubId", "");
        this.overridePendingTransition(0, R.anim.slide_out_left);
        finishAffinity();

    }


    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                log("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void initGCM() {


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);

            }
        };
        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(View view, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }


    /*public void showCustomSnackbar(View view, String message, String action, final Class<?> cls) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BaseActivity.this, cls);
                        startActivity(intent);
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#d7222a"));
        snackbar.show();
    }*/

    public void myColoredToast(String msg) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void log(String string) {
        if (BuildConfig.DEBUG)
            Log.i("ClubApp", string);
    }

    protected void rateApp() {
        final String appPackageName = getPackageName();

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }


    @Override
    public void onSyncStart() {
        onProgressStart();
    }

    @Override
    public void onSyncFinish() {
        onProgressFinish();
    }


    public void onProgressStart() {

        try {
            onProgressFinish();
            if (dialogPD != null && dialogPD.isShowing())
                dialogPD.dismiss();

            dialogPD = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

            dialogPD.setContentView(R.layout.progress);

            dialogPD.show();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void onProgressFinish() {

        try {
            if (isFinishing())
                return;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (dialogPD != null && dialogPD.isShowing())
                            dialogPD.dismiss();
                        dialogPD = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncFailure(int code) {

        // myColoredToast("Internet or Server Error!");
        log("Error code: " + code);

       /* if (code == 500) {

            syncManager.setLoginStatus(null);
            syncManager.bLoggedin = false;
            store.setLoginStatus("off");
            store.setUserName(getResources().getString(R.string.app_name));
            myColoredToast("Successfully Logged out");

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            store.setString("appLogin", "false");
            startActivity(intent);
        }*/


    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {

        if (action.equals("logout"))
            logoutDone(status);
        if (action.equalsIgnoreCase("contact_us") && controller.equalsIgnoreCase("user")) {
            try {
                if (status) {

                    myColoredToast(object.getString("message"));

                } else {

                    myColoredToast(object.getString("error"));
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }

    @Override
    public void onSyncProgress(int arg0, int arg1) {

    }

    @Override
    public void onSyncForbidden(int code, String string) {

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    public void logoutDone(boolean status) {

        log("logout()");

        if (status) {

            syncManager.setLoginStatus(null);
            syncManager.bLoggedin = false;
            store.setLoginStatus("off");
            store.setUserName(getResources().getString(R.string.app_name));
            myColoredToast("Successfully Logged out");
            store.setString("clubId", "");

            //Intent intent = new Intent(getApplicationContext(), AskLoginActvity.class);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            store.setString("appLogin", "false");
            startActivity(intent);

        } else {

            myColoredToast("Login Failed");
        }
    }


    /*public void updateLocation(Location location, GoogleMap googleMap) {
        if (location != null) {

            try {
                double dLatitude = location.getLatitude();
                double dLongitude = location.getLongitude();
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), Constants.MAP_ZOOM));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }*/

    protected void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, R.anim.slide_out_left);
    }

    public void playSoundAlert() {

        mPlay = null;
        if (mPlay == null) {
            mPlay = MediaPlayer.create(this, R.raw.motorcycle);

        }
        mPlay.start();
    }

    public void taskBarColorChange() {

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            //window.setNavigationBarColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void transitionSlideInHorizontal() {
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Image Capture Code
     **/

    protected void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        //final CharSequence[] options = {"Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Select or take a new Picture!");
        builder.setItems(options, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    Constants.mUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Constants.mUri);

                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                } else if (options[item].equals("Choose from Gallery")) {

                   /* Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);*/

                    pickFromGallery();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    protected Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
           /* Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 450, 350,
                    true);*/
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static int getOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.v("", "Exif orientation: " + orientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public Bitmap rotateImage(Bitmap bitmap, int angle, int width, int height) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);


        if (scaledBitmap != null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
                    new Paint(Paint.FILTER_BITMAP_FLAG));
        }


//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            if (scaledBitmap != null) {
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            if (scaledBitmap != null) {
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "ClubApp/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    protected String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser
                    (intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
        }
    }

    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }


    protected void shareApp() {

        String body = "Hi,\n \n  Download this App Now! It will give you all club activities information."
                + " \n \n its available on,"

                + "\n\n  For Android users: https://play.google.com/store/apps/details?id=com.ambuja.clubapp"

                + "\n\n For i-phone users: https://itunes.apple.com/in/app/playstore/";

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(Intent.EXTRA_TEXT, body);

        startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), 999);

    }


    public void shareTextApp(String title, String body) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


    public void shareWallData(String audio_video_file, String event_activities, String event_name) {

        URL url = null;
        try {
            url = new URL(audio_video_file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareImageData(event_name, event_activities, url);
    }

    public void shareImageData(String event_name, String body, URL url) {
        try {

            Uri uri = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap immutableBpm = BitmapFactory.decodeStream(input);
                Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888, true);
                View view = new View(BaseActivity.this);
                view.draw(new Canvas(mutableBitmap));
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mutableBitmap, "Nur", null);
                uri = Uri.parse(path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, event_name);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            emailIntent.setType("image/png");
            startActivity(Intent.createChooser(emailIntent, "Share Via..."));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void shareAudio(String audio_video_file, String event_activities) {

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, event_activities);
        share.putExtra(Intent.EXTRA_TEXT, audio_video_file);
        startActivity(Intent.createChooser(share, "Share File!"));
    }

    public void gotoPageDetail(String id) {
        store.setString("aboutPageId", id);
        Intent intent = new Intent(this, AboutPageActivity.class);
        startActivity(intent);
    }

    public static void makeTextViewResizable(final TextView textView, final int maxLine, final String expandText, final boolean viewMore) {

        if (textView.getTag() == null) {
            textView.setTag(textView.getText());
        }
        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = textView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = textView.getLayout().getLineEnd(0);
                    String text = textView.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    textView.setText(text);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(textView.getText().toString()), textView, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && textView.getLineCount() >= maxLine) {
                    int lineEndIndex = textView.getLayout().getLineEnd(maxLine - 1);
                    String text = textView.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    textView.setText(text);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(textView.getText().toString()), textView, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = textView.getLayout().getLineEnd(textView.getLayout().getLineCount() - 1);
                    String text = textView.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    textView.setText(text);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(textView.getText().toString()), textView, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {

            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        String colorText = "<font color=\"#ff9518\"><bold>" + "View Less" + "</bold></font>";
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        String colorText = "<font color=\"#ff9518\"><bold>" + "Continue Reading" + "</bold></font>";
                        makeTextViewResizable(tv, 3, "Continue Reading", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);


        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public void openContactUsDialog() {

        final Dialog dialog;
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(CENTER);

        Button SubmitBT;
        ImageView cancelIV;
        final EditText fullNameET, emailET, mobileET, commentsET;

        dialog.setContentView(R.layout.dialog_contact_us);

        fullNameET = (EditText) dialog.findViewById(R.id.fullNameET);
        emailET = (EditText) dialog.findViewById(R.id.emailET);
        mobileET = (EditText) dialog.findViewById(R.id.mobileET);
        mobileET.setFilters(new InputFilter[]{new InputFilterMinMax("1", "9999999999")});
        commentsET = (EditText) dialog.findViewById(R.id.commentsET);
        cancelIV = (ImageView) dialog.findViewById(R.id.cancelIV);
        SubmitBT = (Button) dialog.findViewById(R.id.SubmitBT);

        SubmitBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String feedbackName = fullNameET.getText().toString();
                String feedbackemail = emailET.getText().toString().trim();
                String feedbackmobile = mobileET.getText().toString().trim();
                String feedbackcomments = commentsET.getText().toString();

                if (feedbackName.isEmpty()) {

                    fullNameET.setError(getString(R.string.err_msg_first_name));

                } else if (feedbackemail.isEmpty()) {

                    emailET.setError(getString(R.string.err_msg_email));

                } else if (feedbackcomments.isEmpty()) {

                    commentsET.setError("Please add your comments!");

                } else {

                    sendContactUs(feedbackName, feedbackemail, feedbackmobile, feedbackcomments, dialog);

                }


            }
        });

        cancelIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });
        dialog.show();


    }

    private void sendContactUs(String feedbackName, String feedbackemail, String feedbackmobile,
                               String feedbackcomments, Dialog dialog) {

        String action = "contact_us";

        RequestParams params = new RequestParams();

        params.put("name", feedbackName);
        params.put("email", feedbackemail);
        params.put("phonenumber", feedbackmobile);
        params.put("message", feedbackcomments);

        syncManager.sendToServer(action, params, this);
        dialog.dismiss();

    }

    protected void selectClub() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        myDialog.setTitle("Clubs");
        myDialog.setMessage("Please select the club first!");
        myDialog.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


                dialog.dismiss();

            }
        });

        myDialog.setCancelable(false);
        AlertDialog alertd = myDialog.create();
        alertd.show();
    }

}
