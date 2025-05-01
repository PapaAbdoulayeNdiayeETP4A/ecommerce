// utils/ImageUtils.java
package com.example.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitaire pour gérer les opérations sur les images
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    private ImageUtils() {
        // Constructeur privé pour empêcher l'instanciation
    }

    /**
     * Crée un fichier temporaire pour stocker une image
     */
    public static File createImageFile(Context context) throws IOException {
        // Créer un nom de fichier unique basé sur la date actuelle
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* préfixe */
                ".jpg",         /* suffixe */
                storageDir      /* répertoire */
        );
    }

    /**
     * Compresse une image à partir de son URI
     */
    public static byte[] compressImage(Context context, Uri imageUri, int maxWidth, int maxHeight, int quality) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

            // Décoder les dimensions de l'image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculer le ratio de redimensionnement
            int width = options.outWidth;
            int height = options.outHeight;
            float widthRatio = (float) width / maxWidth;
            float heightRatio = (float) height / maxHeight;
            float ratio = Math.max(widthRatio, heightRatio);

            int inSampleSize = 1;
            if (ratio > 1) {
                inSampleSize = (int) Math.ceil(ratio);
            }

            // Décoder l'image avec le ratio calculé
            options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;

            inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Corriger l'orientation de l'image
            bitmap = fixImageOrientation(context, imageUri, bitmap);

            // Compresser l'image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la compression de l'image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Corrige l'orientation de l'image en fonction des métadonnées EXIF
     */
    private static Bitmap fixImageOrientation(Context context, Uri imageUri, Bitmap bitmap) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la correction de l'orientation de l'image: " + e.getMessage());
            return bitmap;
        }
    }

    /**
     * Sauvegarde une image Bitmap dans un fichier
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, File file, int quality) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la sauvegarde de l'image: " + e.getMessage());
            return false;
        }
    }
}