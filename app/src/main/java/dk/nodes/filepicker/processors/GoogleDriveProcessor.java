package dk.nodes.filepicker.processors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import dk.nodes.filepicker.processors.tasks.GetGoogleDriveFileTask;
import dk.nodes.filepicker.utils.Logger;

import static dk.nodes.filepicker.FilePickerConstants.URI;

/**
 * Created by bison on 31/10/17.
 */

public class GoogleDriveProcessor implements IUriProcessor {
    public static final String TAG = GoogleDriveProcessor.class.getSimpleName();
    UriProcessListener uriProcessListener;

    @Override
    public void process(Context context, Uri uri, final UriProcessListener uriProcessListener) {
        this.uriProcessListener = uriProcessListener;
        if(!isValidUri(uri))
        {
            if(uriProcessListener != null) {
                Logger.loge(TAG, "URI not recognized, bailing out");
                uriProcessListener.onProcessingFailure();
                return;
            }
        }

        final String mimeType = context.getContentResolver().getType(uri);

        new GetGoogleDriveFileTask(context, uri, new GetGoogleDriveFileTask.TaskListener() {
            @Override
            public void didSucceed(String newPath) {
                Intent intent = new Intent();
                if(mimeType != null)
                    intent.putExtra("mimeType", mimeType);
                intent.putExtra(URI, newPath);
                if(uriProcessListener != null)
                    uriProcessListener.onProcessingSuccess(intent);
            }

            @Override
            public void didFail() {
                if(uriProcessListener != null)
                    uriProcessListener.onProcessingFailure();
            }
        }).execute();
    }

    private static boolean isValidUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority())
                || "com.google.android.apps.docs.files".equals(uri.getAuthority())
                || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }
}
