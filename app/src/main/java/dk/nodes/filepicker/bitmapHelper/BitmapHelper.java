package dk.nodes.filepicker.bitmapHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
/**
 * Created by joso on 15/11/2016.
 */

public class BitmapHelper {

    /**
     * Saves to external storage
     *
     * @param context
     * @param bitmap
     * @param compressFormat
     * @return
     * @throws Exception
     */
    public static File writeBitmapToExternalStorage(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat) throws Exception {
        File filesDir = context.getExternalCacheDir();
        File file = new File(filesDir, "image.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, 90, fileOutputStream);

        return file;
    }

    /**
     * Calls @see dk.nodes.filepicker.bitmapHelper.BitmapHelper#writeBitmap(Context, Bitmap, Bitmap.CompressFormat) with CompressFormat set to PNG
     * Saves to external storage
     *
     * @param context
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static File writeBitmapToExternalStorage(@NonNull Context context, @NonNull Bitmap bitmap) throws Exception {
        return writeBitmapToExternalStorage(context, bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * Saves to internal storage
     * @param context
     * @param bitmap
     * @param compressFormat
     * @return
     * @throws Exception
     */
    public static File writeBitmap(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat) throws Exception {
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "image.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, 90, fileOutputStream);

        return file;
    }

    /**
     * Calls @see dk.nodes.filepicker.bitmapHelper.BitmapHelper#writeBitmap(Context, Bitmap, Bitmap.CompressFormat) with CompressFormat set to PNG
     * Saves to internal storage
     * @param context
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static File writeBitmap(@NonNull Context context, @NonNull Bitmap bitmap) throws Exception {
        return writeBitmap(context, bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * Saves to external storage
     *
     * @param context
     * @param bitmap
     * @param compressFormat
     * @return
     * @throws Exception
     */
    public static Uri writeBitmapToExternalStorageAsUri(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat) throws Exception {
        File filesDir = context.getExternalCacheDir();
        File file = new File(filesDir, "image.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, 90, fileOutputStream);

        return Uri.fromFile(file);
    }

    /**
     * Calls @see dk.nodes.filepicker.bitmapHelper.BitmapHelper#writeBitmapToExternalStorageAsUri(Context, Bitmap, Bitmap.CompressFormat) with CompressFormat set to PNG
     * Saves to external storage
     *
     * @param context
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static Uri writeBitmapToExternalStorageAsUri(@NonNull Context context, @NonNull Bitmap bitmap) throws Exception {
        return writeBitmapToExternalStorageAsUri(context, bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * Saves to internal storage
     *
     * @param context
     * @param bitmap
     * @param compressFormat
     * @return
     * @throws Exception
     */
    public static Uri writeBitmapAsUri(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat) throws Exception {
        File filesDir = context.getFilesDir();
        File file = new File(filesDir, "image.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, 90, fileOutputStream);

        return Uri.fromFile(file);
    }

    /**
     * Calls @see dk.nodes.filepicker.bitmapHelper.BitmapHelper#writeBitmapAsUri(Context, Bitmap, Bitmap.CompressFormat) with CompressFormat set to PNG
     * Saves to internal storage
     *
     * @param context
     * @param bitmap
     * @return
     * @throws Exception
     */
    public static Uri writeBitmapAsUri(@NonNull Context context, @NonNull Bitmap bitmap) throws Exception {
        return writeBitmapAsUri(context, bitmap, Bitmap.CompressFormat.PNG);
    }

}
