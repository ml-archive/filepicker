package dk.nodes.filepicker.bitmapHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;
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

}
