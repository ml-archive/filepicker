package dk.nodes.filepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class FilePickerActivity extends AppCompatActivity {

    Uri outputFileUri;

    public static String URI = "URI";

    public static String CAMERA = "CAMERA";

    public static String FILE = "FILE";
    public static String TYPE = "TYPE";
    public static String MULTIPLE_TYPES = "MULTIPLE_TYPES";

    public static String MIME_IMAGE = "image/*";
    public static String MIME_IMAGE_BMP = "image/bmp";
    public static String MIME_IMAGE_GIF = "image/gif";
    public static String MIME_IMAGE_JPG = "image/jpg";
    public static String MIME_IMAGE_PNG = "image/png";

    public static String MIME_VIDEO = "video/*";
    public static String MIME_VIDEO_WAV = "video/wav";
    public static String MIME_VIDEO_MP4 = "video/mp4";
    public static String MIME_TEXT_PLAIN = "text/plain";
    public static String MIME_PDF = "application/pdf";

    public static String CHOOSER_TEXT = "CHOOSER_TEXT";

    public static int REQUEST_CODE = 2;
    public static int PERMISSION_REQUEST_CODE = 3;

    String chooserText = "Choose an action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(CHOOSER_TEXT)) {
            chooserText = getIntent().getStringExtra(CHOOSER_TEXT);
        }
        if (!hasPermissions()) {
            requestPermission();
        } else {
            start();
        }
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
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
                if (data.getData() != null) {
                    uri = data.getData().toString();
                } else if (outputFileUri != null) {
                    uri = outputFileUri.toString();
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
            //Only camera
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, outputFileUri);
        } else if (getIntent().getBooleanExtra(FILE, false)) {
            //Only file
            intent = new Intent().setAction(Intent.ACTION_GET_CONTENT);
            if (null != getIntent().getStringArrayExtra(MULTIPLE_TYPES)) {
                //User can specify multiple types for the intent.
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, getIntent().getStringArrayExtra(MULTIPLE_TYPES));
            } else {
                //If no types defaults to image files, if just 1 type applies type
                String type = null == getIntent().getStringExtra(TYPE) ? "image/*" : getIntent().getStringExtra(TYPE);
                intent.setType(type);
            }
        } else {
            //We assume its an image since developer didn't specify anything and we will show chooser with Camera, File explorers (including gdrive, dropbox...)
            intent = chooserIntent();
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });

        } else {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }

    Intent chooserIntent() {
        return Intent.createChooser(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), chooserText)
                .putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{new Intent(MediaStore.ACTION_IMAGE_CAPTURE)});
    }
}
