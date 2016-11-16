package dk.nodes.filepicker.intentHelper;

import android.content.Intent;
import android.net.Uri;

public class FilePickerChooserIntent {

    public static Intent chooserIntent(String chooserText, Uri uri) {
        return Intent.createChooser(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION), chooserText)
                .putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{FilePickerCameraIntent.cameraIntent(uri)});
    }
}
