package dk.nodes.filepicker.uriHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import static dk.nodes.filepicker.FilePickerConstants.URI;

public class FilePickerUriHelper {

    public static String getUriString(@NonNull Intent intent) {
        return intent.getExtras().getString(URI);
    }

    /**
     * Gets the Uri, useful to load with Glide or Picasso for example.
     */
    public static Uri getUri(@NonNull Intent intent) {
        return Uri.parse(getUriString(intent));
    }

    /**
     * Gets the File in case you need to upload it to a server for example.
     */
    public static File getFile(@NonNull Intent intent) {
        return new File(getUri(intent).getPath());
    }

    public static Bitmap getBitmap(@NonNull Activity activity, @NonNull Intent intent) {
        try {
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), getUri(intent));
        } catch (IOException e) {
            //It might be a large bitmap so we try just in case
            return getLargeBitmap(intent);
        }
    }

    public static Bitmap getLargeBitmap(@NonNull Intent intent) {
        BitmapRegionDecoder decoder;
        try {
            decoder = BitmapRegionDecoder.newInstance(getUriString(intent), false);
        } catch (IOException e) {
            return null;
        }
        return decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
    }

    @TargetApi(19)
    public static File getFileFromContentString(@NonNull Context context, @NonNull String uriString) {
        String filePath = uriString;
        Uri uri = Uri.parse(uriString);

        // Used the new photos app which uses a different API apparently
        if (uriString.toString().contains("providers.media.documents/")) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();

        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.MediaColumns.DATA};

            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return new File(filePath);
    }

    public static File getFileFromContentUri(@NonNull Context context, @NonNull Uri uri) {
        return getFileFromContentString(context, uri.toString());
    }

    public static File getFileFromContentIntent(@NonNull Context context, @NonNull Intent intent) {
        return getFileFromContentString(context, getUriString(intent));
    }

}
