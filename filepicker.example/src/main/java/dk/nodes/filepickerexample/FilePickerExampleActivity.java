package dk.nodes.filepickerexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import dk.nodes.filepicker.FilePickerActivity;
import dk.nodes.filepicker.FilePickerConstants;
import dk.nodes.filepicker.uriHelper.FilePickerUriHelper;

import static dk.nodes.filepicker.FilePickerConstants.RESULT_CODE_FAILURE;

public class FilePickerExampleActivity extends AppCompatActivity {

    public static int MY_REQUEST_CODE = 10;
    final String DEFAULT = "DEFAULT";
    final String CAMERA = "CAMERA";
    final String FILE_IMAGE = "FILE IMAGE";
    final String FILE_TYPE = "FILE TYPE";
    final String FILE_MULTIPLE_TYPES = "FILE MULTIPLE TYPES";

    final String DEFAULT_INTENT = "";
    final String CAMERA_INTENT = "intent.putExtra(FilePickerActivity.CAMERA, true);";
    final String FILE_IMAGE_INTENT = "intent.putExtra(FilePickerActivity.FILE, true);";
    final String FILE_TYPE_INTENT = "intent.putExtra(FilePickerActivity.FILE, true);\nintent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF);";
    final String FILE_MULTIPLE_TYPES_INTENT = "intent.putExtra(FilePickerActivity.FILE, true);\nintent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});";
    Button typeBtn;
    Toolbar toolbar;
    ImageView uriIv, glideIv, fileIv;
    Button startActivityForResultBtn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker_example);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        typeBtn = (Button) findViewById(R.id.type_btn);
        uriIv = (ImageView) findViewById(R.id.uri_iv);
        glideIv = (ImageView) findViewById(R.id.glide_iv);
        fileIv = (ImageView) findViewById(R.id.file_iv);
        startActivityForResultBtn = (Button) findViewById(R.id.start_activity_for_result_btn);

        toolbar.setTitle(DEFAULT);
        typeBtn.setText(DEFAULT_INTENT);
        newIntent();

        typeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButton();
            }
        });

        startActivityForResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(FilePickerExampleActivity.this, FilePickerUriHelper.getUriString(data), Toast.LENGTH_SHORT).show();
                //If its not an image we don't load it
                if (toolbar.getTitle().toString().equals(FILE_TYPE) || toolbar.getTitle().toString().equals(FILE_MULTIPLE_TYPES)) {
                    return;
                }
                uriIv.setImageURI(FilePickerUriHelper.getUri(data));
                Glide.with(this).load(FilePickerUriHelper.getUri(data)).into(glideIv);
                File file = FilePickerUriHelper.getFile(this, data);
                if (file == null) {
                    return;
                }
                fileIv.setImageURI(Uri.fromFile(file));
                ImgurManager imgurManager = new ImgurManager();
                imgurManager.uploadImage(FilePickerUriHelper.getFile(FilePickerExampleActivity.this, data), new ImgurManager.UploadCallback() {
                    @Override
                    public void onUploaded(ImgurManager.ImageResponse response) {
                        Toast.makeText(FilePickerExampleActivity.this, "Image Uploaded to Imgur", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FilePickerExampleActivity.this, "User Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CODE_FAILURE) {
                Toast.makeText(FilePickerExampleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void newIntent() {
        intent = new Intent(FilePickerExampleActivity.this, FilePickerActivity.class);
    }

    void setButton() {
        newIntent();
        String buttonText = typeBtn.getText().toString();
        switch (buttonText) {
            case DEFAULT_INTENT:
                toolbar.setTitle(CAMERA);
                buttonText = CAMERA_INTENT;
                intent.putExtra(FilePickerConstants.CAMERA, true);
                break;
            case CAMERA_INTENT:
                toolbar.setTitle(FILE_IMAGE);
                buttonText = FILE_IMAGE_INTENT;
                intent.putExtra(FilePickerConstants.FILE, true);
                break;
            case FILE_IMAGE_INTENT:
                toolbar.setTitle(FILE_TYPE);
                buttonText = FILE_TYPE_INTENT;
                intent.putExtra(FilePickerConstants.FILE, true);
                intent.putExtra(FilePickerConstants.TYPE, FilePickerConstants.MIME_PDF);
                break;
            case FILE_TYPE_INTENT:
                toolbar.setTitle(FILE_MULTIPLE_TYPES);
                buttonText = FILE_MULTIPLE_TYPES_INTENT;
                intent.putExtra(FilePickerConstants.FILE, true);
                intent.putExtra(FilePickerConstants.MULTIPLE_TYPES, new String[]{FilePickerConstants.MIME_PDF, FilePickerConstants.MIME_VIDEO});
                break;
            case FILE_MULTIPLE_TYPES_INTENT:
                toolbar.setTitle(DEFAULT);
                buttonText = DEFAULT_INTENT;
                break;
        }
        typeBtn.setText(buttonText);
    }
}
