package dk.nodes.filepicker.processors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import dk.nodes.filepicker.processors.tasks.GetPhotosTask;
import dk.nodes.filepicker.utils.Logger;

import static dk.nodes.filepicker.FilePickerConstants.URI;

/**
 * Created by bison on 31/10/17.
 */

public class GooglePhotosProcessor implements IUriProcessor {
    public static final String TAG = GooglePhotosProcessor.class.getSimpleName();
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

        new GetPhotosTask(context, uri, new GetPhotosTask.PhotosListener() {
            @Override
            public void didDownloadBitmap(String path) {
                Intent intent = new Intent();
                if(mimeType != null)
                    intent.putExtra("mimeType", mimeType);
                intent.putExtra(URI, path);
                if(uriProcessListener != null)
                    uriProcessListener.onProcessingSuccess(intent);
            }

            @Override
            public void didFail() {
                if(uriProcessListener != null)
                    uriProcessListener.onProcessingFailure();
                return;
            }
        }).execute();
    }

    private static boolean isValidUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
