package dk.nodes.filepicker.processors.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.nodes.filepicker.BuildConfig;
import dk.nodes.filepicker.utils.Logger;

/**
 * Created by Nicolaj on 15-10-2015.
 */
public class GetFileTask extends AsyncTask<Void, String, String> {
    private static final String TAG = GetFileTask.class.getSimpleName();

    private Context context;
    private Uri uri;
    private TaskListener listener;

    public GetFileTask(Context context, Uri uri, TaskListener listener) {
        this.context = context;
        this.uri = uri;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "eboks");

        if (! cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String fileName = "IMG_" + dateFormat.format(new Date()) + ".jpg";
        File f = new File(cacheDir, fileName);

        try {
            InputStream is;
            if (uri.toString().startsWith("content://com.google.android")) {
                is = getSourceStream(uri, context);
            } else {
                is = new URL(uri.toString()).openStream();
            }
            OutputStream os = new FileOutputStream(f);


            copyStream(is, os);
            os.close();
            return Uri.fromFile(f).toString();
        } catch (Exception ex) {
            Logger.loge(TAG, ex.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if( listener != null && context != null ) {
            if( s != null ) {
                listener.didSucceed(s);
            } else {
                listener.didFail();
            }
        }
    }

    private void copyStream(InputStream is, OutputStream os) {
        final int bufferSize = 1024;
        try {
            byte[] bytes = new byte[bufferSize];
            while (true) {
                int count = is.read(bytes, 0, bufferSize);
                if (count == - 1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            Logger.loge(TAG, ex.toString());
        }
    }

    private InputStream getSourceStream(Uri u, Context context) throws FileNotFoundException {
        InputStream out;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(u, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            out = new FileInputStream(fileDescriptor);
        } else {
            out = context.getContentResolver().openInputStream(u);
        }
        return out;
    }

    public interface TaskListener{
        public void didSucceed(String path);
        public void didFail();
    }
}
