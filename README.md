Filepicker

Translucent Activity that handles taking a picture or selecting any type of file (the MIME type must be supported by the device).

Usage

In Manifest:
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    Inside application:
    <activity
            android:name="filepicker.FilePickerActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.Translucent" />

In Activity or Fragment Class:

Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
startActivityForResult(intent, MY_REQUEST_CODE);

Implement onActivityResult.

Result codes ar the same as the Activity ones:
RESULT_OK -> Intent will have a String that should be parsed to an URI
RESULT_CANCELED -> User cancelled chooser or permission request
RESULT_FIRST_USER -> Other errors, phone doesn't have any type of app that supports that MIME type for ex.

@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.parse(data.getExtras().getString(FilePickerActivity.URI));
                //Get the File from the Uri
                new File(uri.getPath());
                //Load with Glide/Picasso
                Glide.with(this).load(uri).into(imageView);
                Toast.makeText(FilePickerExampleActivity.this, data.getExtras().getString(FilePickerActivity.URI), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FilePickerExampleActivity.this, "User Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_FIRST_USER) {
                Toast.makeText(FilePickerExampleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

This library is just an activity and in order to customize it you just need to .putExtra to the intent.

Just calling the activity will provide a chooser intent with Camera and File Explorer.

Camera:
intent.putExtra(FilePickerActivity.CAMERA, true);

File explorer with images:
intent.putExtra(FilePickerActivity.FILE, true);

File explorer with a specific MIME type:
intent.putExtra(FilePickerActivity.FILE, true);
intent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF);

File explorer with a set of MIME types:
intent.putExtra(FilePickerActivity.FILE, true);
intent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});

Download

Gradle:

dependencies {
    compile 'dk.nodes.filepicker:filepicker:1.0'
}