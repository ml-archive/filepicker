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
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

        Cursor cursor = null;
        // Used the new photos app which uses a different API
        if (uriString.contains("providers.media.documents/")) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = null;
            if(wholeID.contains("image")) {
                String[] iColumn = {MediaStore.Images.Media.DATA};
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, iColumn, sel, new String[]{id}, null);
                column = iColumn;
            }
            else if(wholeID.contains("video")) {
                String[] vColumn = {MediaStore.Video.Media.DATA};
                // where id is equal to
                String videoSel = MediaStore.Video.Media._ID + "=?";
                cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, vColumn, videoSel, new String[]{id}, null);
                column = vColumn;
            }

            if (cursor == null) { // nor video
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
            Log.e("DEBUG", "uri: " + uri.toString());
            if(uri.getScheme() == null)
            {
                Log.e("DEBUG", "Uri.scheme == null");
            }
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                Log.e("DEBUG", "mime.getExtensionFromMimeType: " + mimeType);
                // WORKAROUND: we got a device with a buggy cam app that sets the incorrect mimetype, correct it
                if("image/jpg".contentEquals(mimeType))
                {
                    mimeType = "image/jpeg";
                }
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(mimeType);
                Log.e("DEBUG", "extension: " + extension);
            } else {
                Log.e("DEBUG", "MimeTypeMap.getFileExtensionFromUrl");
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
            return extension;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean detectPDF(File file) {
        try {
            if (file == null)
                return false;
            InputStream is = new FileInputStream(file);
            byte[] buf = new byte[4];

            // 25 50 44 46
            if(is.read(buf, 0, 4) == 4)
            {
                if (buf[0] == 0x25 && buf[1] == 0x50 &&
                        buf[2] == 0x44 && buf[3] == 0x46) {
                    is.close();
                    return true;
                }
            }
            is.close();
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
