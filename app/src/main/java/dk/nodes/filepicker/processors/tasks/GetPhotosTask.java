package dk.nodes.filepicker.processors.tasks;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import dk.nodes.filepicker.BuildConfig;
import dk.nodes.filepicker.uriHelper.FilePickerUriHelper;
import dk.nodes.filepicker.utils.Logger;

/**
 * Created by joso on 26/03/15.
 */
public class GetPhotosTask extends AsyncTask<Void, String, String> {
    public static final String TAG = GetPhotosTask.class.getSimpleName();

    private Context context;
    private Uri uri;
    private PhotosListener listener;

    public GetPhotosTask( Context context, Uri uri, PhotosListener listener ) {
        this.context = context;
        this.uri = uri;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri,"r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            InputStream inputStream = new FileInputStream(fileDescriptor);
            BufferedInputStream reader = new BufferedInputStream(inputStream);

            String outputPath = FilePickerUriHelper.makeImageUri().toString();

            // Create an output stream to a file that you want to save to
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputPath));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            return outputPath;

        } catch( Exception e ) {
            Logger.loge(TAG, e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if( listener != null && context != null ) {
            if( s != null ) {
                listener.didDownloadBitmap(s);
            } else {
                listener.didFail();
            }
        }
    }

    public interface PhotosListener {
        public void didDownloadBitmap(String path);
        public void didFail();
    }
}

