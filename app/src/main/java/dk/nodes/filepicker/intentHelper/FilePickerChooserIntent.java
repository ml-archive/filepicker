package dk.nodes.filepicker.intentHelper;

import android.content.Intent;
import android.provider.MediaStore;

public class FilePickerChooserIntent {

    public static Intent chooserIntent(String chooserText) {
        return Intent.createChooser(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), chooserText)
                .putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{new Intent(MediaStore.ACTION_IMAGE_CAPTURE)});
    }
}
