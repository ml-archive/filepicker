package dk.nodes.filepicker.uriHelper;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static dk.nodes.filepicker.FilePickerConstants.URI;

public class FilePickerUriHelper {

    public static String getUriString(@NonNull Intent intent) {
        if(intent.getData() != null) {
            return intent.getData().toString();
        }

        return intent.getExtras().getString(URI);
    }

    public static Uri getUri(@NonNull Intent intent) {
        return Uri.parse(getUriString(intent));
    }

    public static File getFile(@NonNull Context context, @NonNull Intent intent) {
        return getFile(context, getUriString(intent));
    }

    public static File getFile(@NonNull Context context, @NonNull Uri uri) {
        return getFile(context, uri.toString());
    }

    @TargetApi(19)
    public static File getFile(@NonNull Context context, @NonNull String uriString) {
        String filePath = getFilePath(context, uriString);
        if (filePath == null) {
            return null;
        }
        return new File(filePath);
    }

    @TargetApi(19)
    private static String getFilePath(@NonNull Context context, @NonNull String uriString) {
        File fileCheck = new File(uriString);
        if(fileCheck.exists()) {
            return uriString;
        }

        String filePath = null;
        Uri uri = Uri.parse(uriString);
        if (uri == null) {
            return null;
        }

        if(new File(uri.getPath()).exists()) {
            return uri.getPath();
        }

        Cursor cursor;
        // Used the new photos app which uses a different API
        if (uriString.contains("providers.media.documents/")) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            if (cursor == null) {
                return null;
            }
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.MediaColumns.DATA};
            cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    public static Uri makeImageUri() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String fileName = dateFormat.format(new Date()) + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), fileName);
        Uri outputUri = Uri.fromFile(photo);
        return outputUri;
    }

    public static String getFileType(Context context, Uri uri) {
        try {
            String mimeType = context.getContentResolver().getType(uri);
            String extension;
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(mimeType);
            } else {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
            return extension;
        } catch (Exception e) {
            return null;
        }
    }
}
