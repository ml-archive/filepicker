package dk.nodes.filepicker.intentHelper;

import android.content.Intent;

public class FileIntent {

    public static Intent fileIntent(String type) {
        return new Intent().setAction(Intent.ACTION_GET_CONTENT);
    }

    public static void setType(Intent intent, String type) {
        intent.setType(type);
    }

    public static void setTypes(Intent intent, String[] types) {
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
    }
}
