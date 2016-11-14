package dk.nodes.filepicker.intentHelper;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class FilePickerCameraIntent {

    public static Intent cameraIntent(Uri outputFileUri) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE, outputFileUri);
    }
}
