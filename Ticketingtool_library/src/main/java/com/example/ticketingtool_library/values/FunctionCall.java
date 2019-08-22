package com.example.ticketingtool_library.values;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.ticketingtool_library.R;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Pattern;

public class FunctionCall {
    public void logStatus(String message) {
        Log.d("debug", message);
    }

    public void showToast(Context context, String Message) {
        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        toast.show();
    }

    //-----------------------------------------method for show progress dialog----------------------------------------------------//
    public void showprogressdialog(String Message, String Title, ProgressDialog dialog) {
        dialog.setTitle(Title);
        dialog.setMessage(Message);
        dialog.setCancelable(false);
        dialog.show();
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    public void setSnackBar(Context context, View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        view.setBackgroundColor(context.getResources().getColor(R.color.red));
        TextView txtv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setTextColor(context.getResources().getColor(R.color.white));
        txtv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtv.setGravity(Gravity.CENTER);
    }

    public File filestorepath(String value, String file) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, File.separator + file);
    }

    public String Appfoldername() {
        return "Transvision";
    }

    public Bitmap getImage(String path, Context con) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int[] newWH = new int[2];
        newWH[0] = srcWidth / 2;
        newWH[1] = (newWH[0] * srcHeight) / srcWidth;

        int inSampleSize = 1;
        while (srcWidth / 2 >= newWH[0]) {
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path, options);
        ExifInterface exif = new ExifInterface(path);
        String s = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        System.out.println("Orientation>>>>>>>>>>>>>>>>>>>>" + s);
        Matrix matrix = new Matrix();
        float rotation = rotationForImage(con, Uri.fromFile(new File(path)));
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }
        return Bitmap.createBitmap(
                sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);
    }


    private float rotationForImage(Context context, Uri uri) {
        if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            @SuppressLint("Recycle") Cursor c = context.getContentResolver().query(
                    uri, projection, null, null, null);
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                return (int) exifOrientationToDegrees(
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public String filepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String encoded(String filepath) {
        File originalFile = new File(filepath);
        String encodedBase64;
        try {
            byte[] bytes = FileUtils.readFileToByteArray(originalFile);
            encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            byte[] bytes = new byte[0];
            try {
                bytes = FileUtils.readFileToByteArray(originalFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return encodedBase64;
    }

    //--------------------------------------CHECKING INTERNET IS ON OR NOT-------------------------------------------------------//
    public final boolean isInternetOn(Activity activity) {
        ConnectivityManager connect = (ConnectivityManager) activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    //***************************************************************************************************************************
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) throws URISyntaxException {
        @SuppressLint("ObsoleteSdkInt") final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());

    }

    //****************************************update App*****************************************************************
    public void updateApp(Context context, File Apkfile) {
        Uri path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()
                    + ".provider", Apkfile);
        } else path = Uri.fromFile(Apkfile);
        Intent objIntent = new Intent(Intent.ACTION_VIEW);
        objIntent.setDataAndType(path, "application/vnd.android.package-archive");
        objIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(objIntent);
    }


    //**********************************************************************************************************
    public String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    private String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    //***********************************compare version************************************************************************
    public boolean compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmpStr.equals("<");
    }
}
