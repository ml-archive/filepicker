package dk.nodes.filepicker.processors;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import dk.nodes.filepicker.uriHelper.FilePickerUriHelper;
import dk.nodes.filepicker.utils.Logger;

import static dk.nodes.filepicker.BuildConfig.DEBUG;
import static dk.nodes.filepicker.FilePickerConstants.URI;

/**
 * Created by bison on 31/10/17.
 */

public class GenericContentProviderProcessor implements IUriProcessor {
    public static final String TAG = GenericContentProviderProcessor.class.getSimpleName();
    UriProcessListener uriProcessListener;

    @SuppressLint("NewApi")
    @Override
    public void process(Context context, Uri uri, final UriProcessListener uriProcessListener) {
        this.uriProcessListener = uriProcessListener;
        if(!isValidUri(uri))
        {
            if(uriProcessListener != null) {
                Logger.loge(TAG, "URI not recognized, bailing out");
                uriProcessListener.onProcessingFailure();
            }
        }

        final String mimeType = context.getContentResolver().getType(uri);


        try {
            String filename = null;

            if(isDropboxFilecache(uri))
            {
                Logger.logd(TAG, "Uri is dropbox filecache");
                filename = getSAFDisplayName(context, uri);
            }

            if(isExternalStorageDocument(uri) && (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) && filename == null)
            {
                Logger.logd(TAG, "isExternalStorageDocument");
                final String docId;
                docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    filename = getLastSegmentString(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }

            }

            if(filename == null)
                filename = getFilenameFromMediaProvider(context, uri);
            // try alternative method
            if(filename == null)
            {
                String id = uri.getLastPathSegment();
                Logger.logd("TAG", "Trying mediastore id " + id);
                try {
                    filename = getFilenameFromMediaStore(context, id);
                    try {
                        long ts = Long.parseLong(stripExtension(filename));
                        filename = generateDCIMFilename("IMG", ts);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(filename == null && isDownloadsDocument(uri))
            {
                String id = uri.getLastPathSegment();
                Logger.logd(TAG, "Trying public download id " + id);
                try {
                    filename = getFilenameFromDownloadProvider(context, Long.parseLong(id));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(filename == null)
            {
                filename = getSAFDisplayName(context, uri);
            }
            if(filename == null)
            {
                String last = uri.getLastPathSegment();
                if(last != null)
                {
                    if(isValidFilename(last))
                    {
                        filename = last;
                    }
                }
            }
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileExtension = FilePickerUriHelper.getFileType(context, uri);
            Logger.logd(TAG, "fileExtension from contentprovider url is " + fileExtension);
            Logger.logd(TAG, "fileName: " + filename);

            File filesDir = context.getCacheDir();
            File file = null;
            if(filename == null) {
                if(fileExtension != null)
                    file = new File(filesDir, "f" + String.format("%04d", new Random().nextInt(10000)) + "." + fileExtension);
                else
                    file = new File(filesDir, "f" + String.format("%04d", new Random().nextInt(10000)));
            }
            else
            {
                if(!filename.contains(".") && fileExtension != null)
                    file = new File(filesDir, filename + "." + fileExtension);
                else
                    file = new File(filesDir, filename);

            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != - 1) {
                fileOutputStream.write(buffer, 0, len);
            }

            Intent intent = new Intent();
            if(mimeType != null)
                intent.putExtra("mimeType", mimeType);
            intent.putExtra(URI, file.getAbsolutePath());
            if(uriProcessListener != null)
                uriProcessListener.onProcessingSuccess(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.loge(TAG, e.toString());
            if(uriProcessListener != null)
                uriProcessListener.onProcessingFailure();
        }

    }

    private static boolean isValidUri(Uri uri) {
        return "content".equalsIgnoreCase(uri.getScheme());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private String generateDCIMFilename(String prefix, long timestamp)
    {
        try {
            Date d = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US);
            String filename = prefix + "_" + sdf.format(d);
            return filename;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    final String[] ReservedChars = {"|", "\\", "?", "*", "<", "\"", ":", ">"};
    private boolean isValidFilename(String name)
    {
        for(String c :ReservedChars){

            if(name.indexOf(c) > 0)
                return false;
        }
        return true;
    }

    private String getFilenameFromDownloadProvider(Context context, long id)
    {
        try {
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id);

            String selectedPath = getDataColumn(context, contentUri, null, null);
            if (selectedPath != null) {
                if (selectedPath.contains("/")) {
                    String[] parts = selectedPath.split("/");
                    if (parts.length > 0) {
                        selectedPath = parts[parts.length - 1];
                    }
                }
            }
            return selectedPath;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private String getFilenameFromMediaStore(Context context, String id)
    {
        try {
            if (id == null)
                return null;
            final String[] imageColumns = {MediaStore.Images.Media.DATA};
            final String imageOrderBy = null;
            Uri baseUri;
            String state = Environment.getExternalStorageState();
            if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                baseUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            } else {
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            String selectedPath = null;
            Cursor cursor = null;

            cursor = context.getContentResolver().query(baseUri, imageColumns, MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

            if (cursor.moveToFirst()) {
                selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
            if (selectedPath != null) {
                if (selectedPath.contains("/")) {
                    String[] parts = selectedPath.split("/");
                    if (parts.length > 0) {
                        selectedPath = parts[parts.length - 1];
                    }
                }
            }
            return selectedPath;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String getFilenameFromMediaProvider(Context context, Uri uri)
    {
        try {
            if (uri == null)
                return null;
            if (!uri.getLastPathSegment().contains(":"))
                return null;
            String id = uri.getLastPathSegment().split(":")[1];
            boolean isVideo = uri.getLastPathSegment().split(":")[0].contains("video");
            final String[] imageColumns = {MediaStore.Images.Media.DATA};
            final String[] videoColumns = {MediaStore.Video.Media.DATA};
            final String imageOrderBy = null;
            Uri baseUri;
            String state = Environment.getExternalStorageState();
            if (!isVideo) {
                if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    baseUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                } else {
                    baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
            } else {
                if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    baseUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                } else {
                    baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
            }

            String selectedPath = null;
            Cursor cursor = null;
            if (!isVideo) {
                cursor = context.getContentResolver().query(baseUri, imageColumns,
                        MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
            } else {
                cursor = context.getContentResolver().query(baseUri, videoColumns,
                        MediaStore.Video.Media._ID + "=" + id, null, imageOrderBy);
            }

            if (cursor.moveToFirst()) {
                if (!isVideo) {
                    selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                } else {
                    selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                }
            }
            cursor.close();
            if (selectedPath != null) {
                if (selectedPath.contains("/")) {
                    String[] parts = selectedPath.split("/");
                    if (parts.length > 0) {
                        selectedPath = parts[parts.length - 1];
                    }
                }
            }
            return selectedPath;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String stripExtension(String selectedPath)
    {
        if(selectedPath != null)
        {
            if(selectedPath.contains("."))
            {
                String[] parts = selectedPath.split("\\.");
                if(parts.length > 0)
                {
                    selectedPath = parts[0];
                }
            }
        }
        return selectedPath;
    }

    private String getLastSegmentString(String selectedPath)
    {
        if (selectedPath != null) {
            if (selectedPath.contains("/")) {
                String[] parts = selectedPath.split("/");
                if (parts.length > 0) {
                    selectedPath = parts[parts.length - 1];
                }
            }
        }
        return selectedPath;
    }

    private String getSAFDisplayName(Context context, Uri uri) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            return null;
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null, null);
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Logger.loge("DEBUG", "SAF Display Name: " + displayName);
                return displayName;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDropboxFilecache(Uri uri)
    {
        return "com.dropbox.android.FileCache".equals(uri.getAuthority());
    }

    private boolean isGoogleDrive(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority())
                || "com.google.android.apps.docs.files".equals(uri.getAuthority())
                || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority())
                ;

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
