# FilePicker

Hello there! I’m **FilePicker**, the open source Uri helper for Android.

Let me introduce myself.



## What do I do?

**FilePicker** is an Android library that basically boils down to an Activity that will handle all of the work related to taking a picture or selecting a file of any type. Example Activity can be found [here](https://github.com/nodes-android/filepicker/blob/master/app/src/main/java/dk/nodes/filepicker/FilePickerExampleActivity.java).

**FilePicker** Is a translucent Activity so you won't have to worry about breaking the design. It uses the Android Native chooser so it will adapt to the different Android versions and OEMs.

![Chooser Screenshot](http://cketti.de/img/share-url-to-clipboard/screenshot_share.png)

## Usage
Lets jump right in.

In the Activity or Fragment class:

```
Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
```
```
startActivityForResult(intent, MY_REQUEST_CODE);
```
This will prompt the above native chooser.

Since its an activity you can configure everything via intent.putExtra();

To change the chooser text:

```
intent.putExtra(FilePickerActivity.CHOOSER_TEXT, "Please select an action");
```

To get the camera directly:

```
intent.putExtra(FilePickerConstants.CAMERA, true);
```

To get the file picker directly with only images:

```
intent.putExtra(FilePickerConstants.FILE, true);
```


To get the file picker directly with only 1 specific MIME type:
```
intent.putExtra(FilePickerConstants.FILE, true);
intent.putExtra(FilePickerConstants.TYPE, FilePickerConstants.MIME_PDF);
```

To get the file picker with multiple MIME type just send a String Array:

```
intent.putExtra(FilePickerConstants.FILE, true);
intent.putExtra(FilePickerConstants.MULTIPLE_TYPES, new String[]{FilePickerConstants.MIME_IMAGE, FilePickerConstants.MIME_PDF});
```

Here is how you would handle the onActivityResult:

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(FilePickerExampleActivity.this, FilePickerUriHelper.getUriString(intent), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FilePickerExampleActivity.this, "User Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_FIRST_USER) {
                Toast.makeText(FilePickerExampleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
```


### Please Note

This Library will handle the permissions and (if the phone has apps that can deal with that MIME type it will return the Uri.

If you want some helper methods please feel free to use FilePickerUriHelper class.

How to retrieve the Uri String from the intent:

```
String uriString = FilePickerUriHelper.getUriString(intent);
```

How to retrieve the Uri from the intent:

```
Uri uri = FilePickerUriHelper.getUri(intent);
```

How to get the file from the intent:

```
File file = FilePickerUriHelper.getFile(intent);
```

How to get the bitmap from the intent:

```
Bitmap bitmap = FilePickerUriHelper.getBitmap(intent);
```

How to load with Gliide or Picasso from the parsed Uri:

```
Glide.with(this).load(uri).into(imageView);
Picasso.with(this).load(uri).into(imageView);
```

## Download

Gradle:

```
dependencies {
    compile 'dk.nodes.filepicker:filepicker:1.1'
}
```
