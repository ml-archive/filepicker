package dk.nodes.filepicker.utils;

import android.net.Uri;
/**
 * Created by johnny on 02/03/2017.
 */

public class Paths {
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isGoogleDrive(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority())
                || "com.google.android.apps.docs.files".equals(uri.getAuthority())
                || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority())
                ;

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGoogleMediaUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority())|| "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDocumentsUri(Uri uri) {
        return "com.google.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isContentProviderUri(Uri uri) {
        return "content".equalsIgnoreCase(uri.getScheme());
    }

    public static boolean isDownloadProviderUri(Uri uri) {
        if (uri.getAuthority() != null)
        {
            if(uri.getAuthority().contains("com.android.providers.downloads"))
            {
                return true;
            }
        }
        return false;
    }
}
