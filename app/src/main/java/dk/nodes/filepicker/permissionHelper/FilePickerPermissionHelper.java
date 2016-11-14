package dk.nodes.filepicker.permissionHelper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

public class FilePickerPermissionHelper {

    public static boolean requirePermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void askPermission(Activity activity, int requestCode, @NonNull String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
