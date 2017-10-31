package dk.nodes.filepicker.processors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import dk.nodes.filepicker.utils.Logger;

import static dk.nodes.filepicker.FilePickerConstants.URI;

/**
 * Created by bison on 31/10/17.
 */

public class GoogleDocumentsProcessor implements IUriProcessor {
    public static final String TAG = GoogleDocumentsProcessor.class.getSimpleName();
    UriProcessListener uriProcessListener;

    @Override
    public void process(Context context, Uri uri, UriProcessListener uriProcessListener) {
        this.uriProcessListener = uriProcessListener;
        if(!isValidUri(uri))
        {
            if(uriProcessListener != null) {
                Logger.loge(TAG, "URI not recognized, bailing out");
                uriProcessListener.onProcessingFailure();
            }
        }
        final String mimeType = context.getContentResolver().getType(uri);

        String id = uri.getLastPathSegment().split(":")[1];
        boolean isVideo = uri.getLastPathSegment().split(":")[0].contains("video");
        final String[] imageColumns = {MediaStore.Images.Media.DATA};
        final String[] videoColumns = {MediaStore.Video.Media.DATA};
        final String imageOrderBy = null;
        Uri baseUri;
        String state = Environment.getExternalStorageState();
        if (! isVideo) {
            if (! state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                baseUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            } else {
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
        } else {
            if (! state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                baseUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
            } else {
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
        }

        String selectedPath = "path";
        Cursor cursor = null;
        if (!isVideo) {
            cursor = context.getContentResolver().query(baseUri, imageColumns,
                    MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
        } else {
            cursor = context.getContentResolver().query(baseUri, videoColumns,
                    MediaStore.Video.Media._ID + "=" + id, null, imageOrderBy);
        }

        if(cursor == null)
        {
            Logger.loge(TAG, "cursor is null");
            if(uriProcessListener != null)
                uriProcessListener.onProcessingFailure();
            return;
        }

        if (cursor.moveToFirst()) {
            if (! isVideo) {
                selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } else {
                selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
        }
        cursor.close();

        Intent intent = new Intent();
        if(mimeType != null)
            intent.putExtra("mimeType", mimeType);
        intent.putExtra(URI, Uri.parse(selectedPath));
        if(uriProcessListener != null)
            uriProcessListener.onProcessingSuccess(intent);
    }

    private static boolean isValidUri(Uri uri) {
        return "com.google.android.providers.media.documents".equals(uri.getAuthority());
    }
}
