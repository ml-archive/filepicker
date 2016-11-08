package dk.nodes.filepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class FilePickerExampleActivity extends AppCompatActivity {

    public static int MY_REQUEST_CODE = 10;

    ImageButton doneIb;
    Button typeBtn;

    Intent intent;

    final String DEFAULT = "DEFAULT (startActivityForResult with no extras)";
    final String DEFAULT_CAMERA = "CAMERA (startActivityForResult with intent.putExtra(FilePickerActivity.CAMERA, true);";
    final String DEFAULT_FILE_IMAGES = "FILE IMAGES (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true);";
    final String DEFAULT_FILE_TYPE = "FILE TYPE (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true); AND intent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF); for example";
    final String DEFAULT_FILE_MULTIPLE_TYPES = "FILE MULTIPLE TYPES (startActivityForResult with intent.putExtra(FilePickerActivity.FILE, true); AND intent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker_example);

        typeBtn = (Button) findViewById(R.id.type_btn);
        doneIb = (ImageButton) findViewById(R.id.done_ib);

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
                        intent.putExtra(FilePickerActivity.CAMERA, true);
                        break;
                    case DEFAULT_CAMERA:
                        newButtonText = DEFAULT_FILE_IMAGES;
                        intent.putExtra(FilePickerActivity.FILE, true);
                        break;
                    case DEFAULT_FILE_IMAGES:
                        newButtonText = DEFAULT_FILE_TYPE;
                        intent.putExtra(FilePickerActivity.FILE, true);
                        intent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF);
                        break;
                    case DEFAULT_FILE_TYPE:
                        newButtonText = DEFAULT_FILE_MULTIPLE_TYPES;
                        intent.putExtra(FilePickerActivity.FILE, true);
                        intent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});
                        break;
                    case DEFAULT_FILE_MULTIPLE_TYPES:
                        newButtonText = DEFAULT;
                        break;
                }
                typeBtn.setText(newButtonText);
            }
        });

        doneIb.setOnClickListener(new View.OnClickListener() {
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
                //Uri uri = Uri.parse(data.getExtras().getString(FilePickerActivity.URI));
                //Create a file to send it to the server
                //new File(uri.getPath());
                //Load with Glide/Picasso
                //Glide/Picasso.with(this).load(uri).into(imageView);
                Toast.makeText(FilePickerExampleActivity.this, data.getExtras().getString(FilePickerActivity.URI), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FilePickerExampleActivity.this, "User Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_FIRST_USER) {
                Toast.makeText(FilePickerExampleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void newIntent() {
        intent = new Intent(FilePickerExampleActivity.this, FilePickerActivity.class);
    }
}
