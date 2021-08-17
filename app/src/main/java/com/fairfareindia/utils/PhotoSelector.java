package com.fairfareindia.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.fairfareindia.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class PhotoSelector {
    private Uri filePath, imageUri;
    private ImageView imgSelectPic;
    private Bitmap profilePic;
    private Context context;
    private boolean isFromGallery = false;

    static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final int PERMISSION_ALL = 1;
    public static int REQUEST_CAMERA = 90, SELECT_FILE = 91;


    public PhotoSelector(Context context) {
        this.context = context;
    }

    public void selectImage(ImageView img) {
        imgSelectPic = img;
        final CharSequence[] items = {context.getString(R.string.take_photo), context.getString(R.string.choose_from_gallery), context.getString(R.string.btn_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(context.getString(R.string.take_photo))) {
                    isFromGallery = false;
                    cameraIntent();
                } else if (items[item].equals(context.getString(R.string.choose_from_gallery))) {
                    isFromGallery = true;
                    galleryIntent();
                } else if (items[item].equals(context.getString(R.string.btn_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        ((Activity) context).startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    @SuppressWarnings("deprecation")
    public Uri onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                if (imgSelectPic != null){
                    imgSelectPic.setImageBitmap(bm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filePath = data.getData();
        }
        return null;
    }

    public Uri onCaptureImageResult() {
        Bitmap photo = null;
        try {
            photo = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            // imgSelectPic.setImageBitmap(getResizedBitmap(photo, 800));

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            SaveImage(getResizedBitmap(photo, 800));
            filePath = getImageUri(profilePic);


            /**
             * Mohsin Logic to fix auto rotate
             */
            Bitmap myBitmap = BitmapFactory.decodeFile(getPath (filePath, context));
            if (myBitmap != null){
                if (imgSelectPic!= null){
                    imgSelectPic.setImageBitmap(myBitmap);
                }
            }

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "", null);
        return Uri.parse(path);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        profilePic = Bitmap.createScaledBitmap(image, width, height, true);
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ShopSearch");

        if (!myDir.exists()) {
            myDir.mkdirs();
            myDir = new File(root + "/ShopSearch/Images");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
        } else {
            myDir = new File(root + "/ShopSearch/Images");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "imgShopSearch" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            filePath = getImageUri(finalBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri, final Context context) {
        Cursor cursor;
        String document_id;
        String path = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            cursor.moveToFirst();
            if(uri.toString ().contains ("provider")){
                document_id = DocumentsContract.getDocumentId(uri);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        proj, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
            }
            if (cursor.moveToFirst ()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }else{
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            if (cursor.moveToFirst ()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }

        cursor.close();
        return getRightAngleImage (path);
    }



    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface (photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    if (isFromGallery){
                        degree = 0;
                    }else {
                        degree = 90;
                    }

                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }

    /**
     * PERMISSION SECTION
     **/

    public boolean isPermissionGranted(Context context) {

        if (!hasPermissions(context, PERMISSIONS)) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

//                showPermissionDisclaimerDialog(context);
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

//                showPermissionDisclaimerDialog(context);
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return false;
            }
        } else {
            // Permission has already been granted
            return true;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}