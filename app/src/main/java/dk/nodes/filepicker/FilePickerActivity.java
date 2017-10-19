package dk.nodes.filepicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import dk.nodes.filepicker.bitmapHelper.FilePickerBitmapHelper;
import dk.nodes.filepicker.intentHelper.FilePickerCameraIntent;
import dk.nodes.filepicker.intentHelper.FilePickerChooserIntent;
import dk.nodes.filepicker.intentHelper.FilePickerFileIntent;
import dk.nodes.filepicker.tasks.GetFileTask;
import dk.nodes.filepicker.tasks.GetGoogleDriveFileTask;
import dk.nodes.filepicker.tasks.GetPhotosTask;
import dk.nodes.filepicker.uriHelper.FilePickerUriHelper;
import dk.nodes.filepicker.utils.Paths;

import static android.R.attr.path;
import static dk.nodes.filepicker.FilePickerConstants.CAMERA;
import static dk.nodes.filepicker.FilePickerConstants.CHOOSER_TEXT;
import static dk.nodes.filepicker.FilePickerConstants.DOWNLOAD_IF_NON_LOCAL;
import static dk.nodes.filepicker.FilePickerConstants.FILE;
import static dk.nodes.filepicker.FilePickerConstants.MULTIPLE_TYPES;
import static dk.nodes.filepicker.FilePickerConstants.PERMISSION_REQUEST_CODE;
import static dk.nodes.filepicker.FilePickerConstants.REQUEST_CODE;
import static dk.nodes.filepicker.FilePickerConstants.RESULT_CODE_FAILURE;
import static dk.nodes.filepicker.FilePickerConstants.TYPE;
import static dk.nodes.filepicker.FilePickerConstants.URI;
import static dk.nodes.filepicker.permissionHelper.FilePickerPermissionHelper.askPermission;
import static dk.nodes.filepicker.permissionHelper.FilePickerPermissionHelper.requirePermission;

public class FilePickerActivity extends AppCompatActivity {

    Uri outputFileUri;

    String chooserText = "Choose an action";
    private boolean download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CHOOSER_TEXT)) {
            chooserText = getIntent().getStringExtra(CHOOSER_TEXT);
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(DOWNLOAD_IF_NON_LOCAL)) {
            download = getIntent().getBooleanExtra(DOWNLOAD_IF_NON_LOCAL, true);
        }
        if (requirePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            askPermission(this, PERMISSION_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            setResult(RESULT_FIRST_USER);
            finish();
            return;
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && (permissions[1].equals(Manifest.permission.CAMERA) && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                start();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                String uriString = null;
                if (data != null && data.getData() != null) {
                    uriString = data.getData().toString();
                } else if (outputFileUri != null) {
                    uriString = outputFileUri.toString();
                } else if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                    uriString = data.getExtras().get("data").toString();
                    try {
                        File file = FilePickerBitmapHelper.writeBitmap(this, (Bitmap) data.getExtras().get("data"), false);
                        uriString = Uri.fromFile(file).toString();
                    } catch (Exception e) {
                        Log.e("FilePickerActivity", e.toString());
                    }
                }

                if (uriString == null) {
                    setResult(RESULT_FIRST_USER);
                    finish();
                    return;
                }

                Uri uri = Uri.parse(uriString);

                // Android 4.4 throws:
                // java.lang.SecurityException: Permission Denial: opening provider com.android.providers.media.MediaDocumentsProvider
                // So we do this
                try {
                    grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    }
                } catch (Exception e) {
                    if(BuildConfig.DEBUG) {
                        Log.e("", e.toString());
                    }
                }

                if (Paths.isGooglePhotosUri(uri)) {
                    new GetPhotosTask(this, uri, new GetPhotosTask.PhotosListener() {
                        @Override
                        public void didDownloadBitmap(String path) {
                            Intent intent = new Intent();
                            intent.putExtra(URI, path);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void didFail() {
                            setResult(RESULT_FIRST_USER);
                            finish();
                            return;
                        }
                    }).execute();
                } else if (Paths.isGoogleDocumentsUri(uri)) {
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
                    if (! isVideo) {
                        cursor = getContentResolver().query(baseUri, imageColumns,
                                MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
                    } else {
                        cursor = getContentResolver().query(baseUri, videoColumns,
                                MediaStore.Video.Media._ID + "=" + id, null, imageOrderBy);
                    }

                    if (cursor.moveToFirst()) {
                        if (! isVideo) {
                            selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        } else {
                            selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        }
                    }

                    Intent intent = new Intent();
                    intent.putExtra(URI, Uri.parse(selectedPath));
                    setResult(RESULT_OK, intent);
                    finish();

                } else if(Paths.isContentProviderUri(uri)) {
                    try {

                        String filename = getFilenameFromMediaProvider(uri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        String fileExtension = FilePickerUriHelper.getFileType(this, uri);
                        Log.e("DEBUG", "fileExtension from contentprovider url is " + fileExtension);
                        Log.e("DEBUG", "fileName: " + filename);

                        File filesDir = getCacheDir();
                        File file = null;
                        if(filename == null) {
                            file = new File(filesDir, "f" + String.format("%04d", new Random().nextInt(10000)) + "." + fileExtension);
                        }
                        else
                        {
                            file = new File(filesDir, filename + "." + fileExtension);
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
                        intent.putExtra(URI, file.getAbsolutePath());
                        setResult(RESULT_OK, intent);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("", e.toString());
                    }
                } else if (Paths.isGoogleMediaUri(uri)) {

                    new GetFileTask(this, uri, new GetFileTask.TaskListener() {
                        @Override
                        public void didSucceed(String newPath) {
                            Intent intent = new Intent();
                            intent.putExtra(URI, newPath);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void didFail() {
                            setResult(RESULT_FIRST_USER);
                            finish();
                            return;
                        }
                    }).execute();
                } else if (Paths.isGoogleDrive(uri)) {
                    new GetGoogleDriveFileTask(this, uri, new GetGoogleDriveFileTask.TaskListener() {
                        @Override
                        public void didSucceed(String newPath) {
                            Intent intent = new Intent();
                            intent.putExtra(URI, newPath);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void didFail() {
                            setResult(RESULT_FIRST_USER);
                            finish();
                            return;
                        }
                    }).execute();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(URI, uriString);
                    setResult(RESULT_OK, intent);
                    finish();
                }


            }
        } else if (resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            setResult(RESULT_CODE_FAILURE);
            finish();
        }
    }

    private String getFilenameFromMediaProvider(Uri uri)
    {
        if(uri == null)
            return null;
        if(!uri.getLastPathSegment().contains(":"))
            return null;
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

        String selectedPath = null;
        Cursor cursor = null;
        if (! isVideo) {
            cursor = getContentResolver().query(baseUri, imageColumns,
                    MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
        } else {
            cursor = getContentResolver().query(baseUri, videoColumns,
                    MediaStore.Video.Media._ID + "=" + id, null, imageOrderBy);
        }

        if (cursor.moveToFirst()) {
            if (! isVideo) {
                selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } else {
                selectedPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
        }
        cursor.close();
        if(selectedPath != null)
        {
            if(selectedPath.contains("/"))
            {
                String[] parts = selectedPath.split("/");
                if(parts.length > 0)
                {
                    selectedPath = parts[parts.length-1];
                }
            }
        }
        return selectedPath;
    }

    void start() {
        final Intent intent;
        if (getIntent().getBooleanExtra(CAMERA, false)) {
            outputFileUri = FilePickerCameraIntent.setUri(this);
            intent = FilePickerCameraIntent.cameraIntent(outputFileUri);
        } else if (getIntent().getBooleanExtra(FILE, false)) {
            //Only file
            intent = FilePickerFileIntent.fileIntent("image/*");
            if (null != getIntent().getStringArrayExtra(MULTIPLE_TYPES)) {
                //User can specify multiple types for the intent.
                FilePickerFileIntent.setTypes(intent, getIntent().getStringArrayExtra(MULTIPLE_TYPES));
            } else if (null != getIntent().getStringExtra(TYPE)) {
                //If no types defaults to image files, if just 1 type applies type
                FilePickerFileIntent.setType(intent, getIntent().getStringExtra(TYPE));
            }
        } else {
            //We assume its an image since developer didn't specify anything and we will show chooser with Camera, File explorers (including gdrive, dropbox...)
            outputFileUri = FilePickerCameraIntent.setUri(this);

            if (null != getIntent().getStringArrayExtra(MULTIPLE_TYPES)) {
                //User can specify multiple types for the intent.
                intent = FilePickerChooserIntent.chooserMultiIntent(chooserText, outputFileUri, getIntent().getStringArrayExtra(MULTIPLE_TYPES));
            } else if (null != getIntent().getStringExtra(TYPE)) {
                //If no types defaults to image files, if just 1 type applies type
                intent = FilePickerChooserIntent.chooserSingleIntent(chooserText, outputFileUri, getIntent().getStringExtra(TYPE));
            }
            else {
                intent = FilePickerChooserIntent.chooserIntent(chooserText, outputFileUri);
            }
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }, 500);

        } else {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }
}
