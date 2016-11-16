package dk.nodes.filepickerexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dk.nodes.filepicker.FilePickerActivity;
import dk.nodes.filepicker.FilePickerConstants;
import dk.nodes.filepicker.uriHelper.FilePickerUriHelper;

import static dk.nodes.filepicker.FilePickerConstants.RESULT_CODE_FAILURE;

public class FilePickerExampleActivity extends AppCompatActivity {

    public static int MY_REQUEST_CODE = 10;
    final String DEFAULT = "DEFAULT (startActivityForResult with no extras)";
    final String DEFAULT_CAMERA = "CAMERA (startActivityForResult with intent.putExtra(FilePickerActivity.CAMERA, true);";
    final String DEFAULT_FILE_IMAGES = "FILE IMAGES (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true);";
    final String DEFAULT_FILE_TYPE = "FILE TYPE (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true); AND intent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF); for example";
    final String DEFAULT_FILE_MULTIPLE_TYPES = "FILE MULTIPLE TYPES (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true); AND intent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});";
    Button typeBtn;
    ImageView imageView;
    Button goBtn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker_example);
        typeBtn = (Button) findViewById(R.id.type_btn);
        imageView = (ImageView) findViewById(R.id.image_view);
        goBtn = (Button) findViewById(R.id.go_btn);

        typeBtn.setText(DEFAULT);
        newIntent();

        typeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //New intent to clear previous bundle since get extras remove or clear doesn't seem to work
                newIntent();
                String buttonText = typeBtn.getText().toString();
                String newButtonText = null;
                switch (buttonText) {
                    case DEFAULT:
                        newButtonText = DEFAULT_CAMERA;
                        intent.putExtra(FilePickerConstants.CAMERA, true);
                        break;
                    case DEFAULT_CAMERA:
                        newButtonText = DEFAULT_FILE_IMAGES;
                        intent.putExtra(FilePickerConstants.FILE, true);
                        break;
                    case DEFAULT_FILE_IMAGES:
                        newButtonText = DEFAULT_FILE_TYPE;
                        intent.putExtra(FilePickerConstants.FILE, true);
                        intent.putExtra(FilePickerConstants.TYPE, FilePickerConstants.MIME_PDF);
                        break;
                    case DEFAULT_FILE_TYPE:
                        newButtonText = DEFAULT_FILE_MULTIPLE_TYPES;
                        intent.putExtra(FilePickerConstants.FILE, true);
                        intent.putExtra(FilePickerConstants.MULTIPLE_TYPES, new String[]{FilePickerConstants.MIME_IMAGE, FilePickerConstants.MIME_PDF});
                        break;
                    case DEFAULT_FILE_MULTIPLE_TYPES:
                        newButtonText = DEFAULT;
                        break;
                }
                typeBtn.setText(newButtonText);
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
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
                ImgurManager imgurManager = new ImgurManager();
                imgurManager.uploadImage(FilePickerUriHelper.getFileFromContentIntent(FilePickerExampleActivity.this, data), new ImgurManager.UploadCallback() {
                    @Override
                    public void onUploaded(ImgurManager.ImageResponse response) {
                        Picasso.with(FilePickerExampleActivity.this).load(response.data.link).into(imageView);
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
}
