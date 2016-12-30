package dk.nodes.filepicker.intentHelper;

import android.annotation.TargetApi;
import android.content.Intent;

public class FilePickerFileIntent {

    public static Intent fileIntent(String type) {
        type = null != type ? type : "image/*";
        return new Intent().setAction(Intent.ACTION_GET_CONTENT).setType(type);
    }

    public static void setType(Intent intent, String type) {
        intent.setType(type);
    }

    @TargetApi(19)
    public static void setTypes(Intent intent, String[] types) {
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
    }
}
