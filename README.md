# FilePicker
The purpose of this library is taking whatever URI comes back from SAF (Storage Access Framework) or other android
storage providers and turning it into an easy to read local file URI.

## Download
Gradle:
```
dependencies {
    compile 'dk.nodes.filepicker:filepicker:2.0.+'
}
```

The plus above might require you to upgrade the gradle plugin in AS:
open $PROJECT_ROOT/gradle/wrapper/gradle-wrapper.properties and correct the line to:

distributionUrl=https\://services.gradle.org/distributions/gradle-4.1-all.zip

## Wait, what is this?

**FilePicker** Is an Android library that will get the Uri you need. It even has some Uri helper methods!

## How is that even possible?

Calm down, **FilePicker** is just a transparent Activity that will handle all of the work related to getting an Uri from your phone, this includes taking a camera picture and in the next version even video recordings! Example Activity can be found [here](https://github.com/nodes-android/filepicker/blob/master/filepicker.example/src/main/java/dk/nodes/filepickerexample/FilePickerExampleActivity.java).

**FilePicker** Is a translucent Activity so you won't have to worry about breaking the design. It uses the Android Native chooser so it will adapt to the different Android versions and OEMs! You will only get the Native chooser if you don't add any extras to the intent.

![Chooser Screenshot](http://cketti.de/img/share-url-to-clipboard/screenshot_share.png)

## Usage

In the Activity or Fragment class:
```
Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
startActivityForResult(intent, MY_REQUEST_CODE);
```
This will prompt the native chooser with the options of Camera and File (only Images).

Since its an activity you can configure everything via intent.putExtra();

**To change the chooser text:**

```
intent.putExtra(FilePickerActivity.CHOOSER_TEXT, "Please select an action");
```

**To get the camera directly:**

```
intent.putExtra(FilePickerConstants.CAMERA, true);
```

**To get the file picker directly with only images:**

```
intent.putExtra(FilePickerConstants.FILE, true);
```


**To get the file picker directly with only 1 specific MIME type:**
```
intent.putExtra(FilePickerConstants.FILE, true);
intent.putExtra(FilePickerConstants.TYPE, FilePickerConstants.MIME_PDF);
```

**To get the file picker with multiple MIME type just send a String Array:**

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
                Toast.makeText(FilePickerExampleActivity.this, FilePickerUriHelper.getUriString(data), Toast.LENGTH_SHORT).show();
                //If its not an image we don't load any of the image views
                if (!isImage) {
                    return;
                }
                //Imageview setImageUri with URI
                uriIv.setImageURI(FilePickerUriHelper.getUri(data));
                //Glide loading with URI
                Glide.with(this).load(FilePickerUriHelper.getUri(data)).into(glideIv);
                //Imageview setImageUri with URI from File
                fileIv.setImageURI(Uri.fromFile(FilePickerUriHelper.getFile(this, data)));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(FilePickerExampleActivity.this, "User Canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CODE_FAILURE) {
                Toast.makeText(FilePickerExampleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
```


### Please Note

This Library will handle the permissions and (if the phone has apps that can deal with that MIME type) it will return the Uri.

If you want some helper methods please feel free to use FilePickerUriHelper class.

**How to retrieve the Uri String from the intent:**
```
String uriString = FilePickerUriHelper.getUriString(intent);
```

**How to retrieve the Uri from the intent:**
```
Uri uri = FilePickerUriHelper.getUri(intent);
```

**How to get the file from the intent:**
```
File file = FilePickerUriHelper.getFile(context, intent);
```

**How to load with Gliide from the parsed Uri:**
```
Uri uri = FilePickerUriHelper.getUri(intent);
Glide.with(this).load(uri).into(imageView);
```
