package dk.nodes.filepicker.intentHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

public class FilePickerCameraIntent {

    public static Intent cameraIntent(@NonNull Uri uri) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

    public static Uri setUri(@NonNull Activity activity) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }
}
