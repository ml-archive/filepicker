package dk.nodes.filepicker.uriHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

import static dk.nodes.filepicker.FilePickerConstants.URI;

public class FilePickerUriHelper {

    public static String getUriString(Intent intent) {
        return intent.getExtras().getString(URI);
    }

    /**
     * Gets the Uri, useful to load with Glide or Picasso for example.
     */
    public static Uri getUri(Intent intent) {
        return Uri.parse(getUriString(intent));
    }

    /**
     * Gets the File in case you need to upload it to a server for example.
     */
    public static File getFile(Intent intent) {
        return new File(getUri(intent).getPath());
    }

    public static Bitmap getBitmap(Activity activity, Intent intent) {
        try {
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), getUri(intent));
        } catch (IOException e) {
            //It might be a large bitmap so we try just in case
            return getLargeBitmap(intent);
        }
    }

    public static Bitmap getLargeBitmap(Intent intent) {
        BitmapRegionDecoder decoder = null;
        try {
            decoder = BitmapRegionDecoder.newInstance(getUriString(intent), false);
        } catch (IOException e) {
            return null;
        }
        return decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
    }
}
