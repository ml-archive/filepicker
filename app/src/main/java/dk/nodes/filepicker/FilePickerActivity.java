package dk.nodes.filepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import dk.nodes.filepicker.bitmapHelper.BitmapHelper;
import dk.nodes.filepicker.intentHelper.FilePickerCameraIntent;
import dk.nodes.filepicker.intentHelper.FilePickerChooserIntent;
import dk.nodes.filepicker.intentHelper.FilePickerFileIntent;

import static dk.nodes.filepicker.FilePickerConstants.CAMERA;
import static dk.nodes.filepicker.FilePickerConstants.CHOOSER_TEXT;
import static dk.nodes.filepicker.FilePickerConstants.FILE;
import static dk.nodes.filepicker.FilePickerConstants.MULTIPLE_TYPES;
import static dk.nodes.filepicker.FilePickerConstants.PERMISSION_REQUEST_CODE;
import static dk.nodes.filepicker.FilePickerConstants.REQUEST_CODE;
import static dk.nodes.filepicker.FilePickerConstants.TYPE;
import static dk.nodes.filepicker.FilePickerConstants.URI;
import static dk.nodes.filepicker.permissionHelper.FilePickerPermissionHelper.askPermission;
import static dk.nodes.filepicker.permissionHelper.FilePickerPermissionHelper.requirePermission;

public class FilePickerActivity extends AppCompatActivity {

    Uri outputFileUri;

    String chooserText = "Choose an action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CHOOSER_TEXT)) {
            chooserText = getIntent().getStringExtra(CHOOSER_TEXT);
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
                String uri = null;
                if (data != null && data.getData() != null) {
                    uri = data.getData().toString();
                } else if (outputFileUri != null) {
                    uri = outputFileUri.toString();
                } else if (data.getExtras() != null && data.getExtras().get("data") != null) {
                    uri = data.getExtras().get("data").toString();
                    try {
                        File file = BitmapHelper.writeBitmap(this, (Bitmap) data.getExtras().get("data"));
                        uri = Uri.fromFile(file).toString();
                    } catch (Exception e) {
                        Log.e("FilePickerActivity", e.toString());
                    }
                }

                if (uri == null) {
                    setResult(RESULT_FIRST_USER);
                    finish();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(URI, uri);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            setResult(RESULT_FIRST_USER);
            finish();
        }
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
            intent = FilePickerChooserIntent.chooserIntent(chooserText, outputFileUri);
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
