package dk.nodes.filepicker.bitmapHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FilePickerBitmapHelper {

    public static File writeBitmap(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull Boolean externalStorage) throws Exception {
        return writeBitmap(context, bitmap, Bitmap.CompressFormat.PNG, externalStorage);
    }

    public static File writeBitmap(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat, @NonNull Boolean externalStorage) throws Exception {
        File filesDir = externalStorage ? context.getExternalCacheDir() : context.getCacheDir();
        File file = new File(filesDir, "image.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, 90, fileOutputStream);
        return file;
    }

    public static BitmapFactory.Options getBitmapOptions(@NonNull Uri uri, @NonNull Context context) {
        BitmapFactory.Options o;
        o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o);

            // If the uri is a "file path", this will throw java.io.FileNotFoundException: No content provider
            // Try the old way
        } catch (FileNotFoundException fnfe) {
            File file = new File(uri.getPath());
            if (file.exists()) {
                BitmapFactory.decodeFile(uri.getPath(), o);
            }
        }

        return o;
    }

}
