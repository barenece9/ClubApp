package com.ambuja.clubapp.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ambuja.clubapp.BuildConfig;
import com.ambuja.clubapp.MyApplication;
import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.activity.ClubActivitiesSettingsActivity;
import com.ambuja.clubapp.activity.ClubListSettingsActivity;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.PrefStore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.wandrip.http.SyncEventListner;
import com.wandrip.http.SyncManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class BaseFragment extends CropImageBaseFragment implements SyncEventListner, LocationListener {

    public PrefStore store;
    public static SyncManager syncManager;
    public int screenWidth;
    public int screenHeight;
    public static Dialog dialogPD = null;
    public boolean isGPSEnabled = false;
    public boolean isNetworkEnabled = false;
    private MediaPlayer mPlay;
    public MyApplication myApplication;
    private LocationManager locationManager;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "ClubApp Images";
    public static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
    public static final int REQUEST_SELECT_PICTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public float LOCATION_UPDATE_DISTANCE = 10;
    public long LOCATION_UPDATE_TIME_INTERVAL = 1000;

    public GoogleMap googleMap;
    private static AnimationDrawable progressAnimation;
    public BaseActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myApplication = new MyApplication();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        transitionSlideInHorizontal();
        // checkNetwork();
        store = new PrefStore(getActivity());

        syncManager = SyncManager.getInstance(getActivity());


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mActivity = (BaseActivity) getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();

        store.setLoginStatus("on");
    }


    public void transitionSlideInHorizontal() {

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static String formateDateFromstring(String inputFormat,
                                               String outputFormat, String inputDate) {

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

        Builder myDialog = new Builder(getActivity());
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

                dialog.dismiss();
            }
        });
        myDialog.setCancelable(false);
        AlertDialog alertd = myDialog.create();
        alertd.show();
    }


    protected void EnableLocation() {

        if (!isGPSEnabled && !isNetworkEnabled) {
            Builder dialog = new Builder(getActivity());
            dialog.setMessage("Location sevice not enabled");
            dialog.setPositiveButton("Enable Location", new OnClickListener() {

                @Override
                public void onClick(DialogInterface Dialog, int paramInt) {

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    Dialog.dismiss();

                }
            });
            dialog.setNegativeButton("Cancel", new OnClickListener() {

                @Override
                public void onClick(DialogInterface Dialog, int paramInt) {

                    Dialog.cancel();

                }
            });
            dialog.show();

        }
    }

    // ==============================================================================================
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception e) {

            e.printStackTrace();
        }

        return latLng;
    }


    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(View view, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#d7222a"));
        snackbar.show();

    }

    public void myColoredToast(String msg) {
        try {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(msg);

            Toast toast = new Toast(getActivity());
            toast.setGravity(Gravity.BOTTOM, 0, 30);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // ============================================================================================================

    public void log(String string) {
        if (BuildConfig.DEBUG)
            Log.i("babujewellers", string);

    }

    public Date currentDate() {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 0);
            date = dateFormat.parse(dateFormat.format(calendar.getTime()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public Date incrementExpirationDate(Date expiryDate) {
        Date incrementExpiryDate = expiryDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(incrementExpiryDate);
            calendar.add(Calendar.HOUR, 2);
            incrementExpiryDate = dateFormat.parse(dateFormat.format(calendar.getTime()));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incrementExpiryDate;
    }

    public Date decrementExpirationDate(Date expiryDate) {
        Date decrementExpiryDate = expiryDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(decrementExpiryDate);
            calendar.add(Calendar.HOUR, -2);
            decrementExpiryDate = dateFormat.parse(dateFormat.format(calendar.getTime()));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return decrementExpiryDate;
    }

    // ===============================================================================================

    @Override
    public void onSyncStart() {
        onProgressStart();

    }

    @Override
    public void onSyncFinish() {

        onProgressFinish();


    }

    @Override
    public void onSyncFailure(int code) {

        myColoredToast("Internet or Server Error!");
        log("Error code: " + code);

    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status,
                              JSONObject object) {


    }

    public void onProgressStart() {
        try {
            onProgressFinish();

            if (dialogPD != null && dialogPD.isShowing())
                dialogPD.dismiss();

            dialogPD = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

            dialogPD.setContentView(R.layout.progress);

            dialogPD.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onProgressFinish() {

        try {

            if (getActivity().isFinishing())
                return;

            getActivity().runOnUiThread(new Runnable() {

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


    public void logoutDone(boolean status) {

        log("logout()");

        if (status) {

			/*
             * syncManager.setLoginStatus(null); syncManager.bLoggedin = false;
			 * store.setLoginStatus("off");
			 * store.setUserName(getResources().getString(R.string.app_name));
			 * myColoredToast("Successfully Logged out");
			 * 
			 * Intent intent = new Intent(getApplicationContext(),
			 * SignUpActivity.class);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * startActivity(intent); finishAffinity();
			 */

        } else {

            myColoredToast("Login Failed");
        }
    }


    public void cancelReasonDone(boolean status, JSONObject object) {
        if (status == true) {
            myColoredToast("Cancelled");
        }
    }


    public void updateLocation(Location location, GoogleMap googleMap) {

        //  location = gpsTracker.getLocation();
        if (location != null) {

            try {
                double dLatitude = location.getLatitude();
                double dLongitude = location.getLongitude();
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), Constants.MAP_ZOOM));
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }


    public void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    /**
     * * @param getLocationByGeocoder * @return
     */

    public List<Address> getLocationNameByGeocoder(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> list = null;
        try {

            list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 3);

        } catch (IOException e) {

            myColoredToast("No Address Found " + e.getMessage());

            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void onDestroy() {
        // cancelLocationUpdate();
        super.onDestroy();
    }

    public void playSoundAlert() {

        if (mPlay == null) {
            mPlay = MediaPlayer.create(getActivity(), R.raw.motorcycle);

        }
        mPlay.start();

    }

    // ================= ANIMATE MARKER =======================

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker, GoogleMap map) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public void getSreenDimanstions() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    public LatLngBounds createLatLngBounds(LatLng firstLocation, LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        //location = gpsTracker.getLocation();

        Log.i("log_data", "location listener called");

        updateLocation(location, googleMap);

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

    public String getAddress(LatLng latLng) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity());
            if (latLng.latitude != 0 || latLng.longitude != 0) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city + ", country = " + country);
                return address + " ," + city + " ," + country;
            } else {
                Toast.makeText(getActivity(), "latitude and longitude are null", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onSyncProgress(int arg0, int arg1) {

    }

    @Override
    public void onSyncForbidden(int code, String string) {

    }


    /**
     * Image Capture Code
     **/

    protected void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app
                .AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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

                  /*  Intent intent = new Intent(Intent.ACTION_PICK,
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
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
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
            File file = new File(getActivity().getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getActivity().openFileOutput(file.getName(),
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

    public static int getOrientation(Context context, String imagePath) {
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
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
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

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
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

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
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
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    public void opensClubs() {

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ClubActivitiesSettingsActivity.class);
            startActivity(intent);

        }
    }



}
