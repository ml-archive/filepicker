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
intent.putExtra(FilePickerActivity.CAMERA, true);

```

To get the file picker directly with only images:

```
intent.putExtra(FilePickerActivity.FILE, true);

```


To get the file picker directly with only 1 specific MIME type:

```
intent.putExtra(FilePickerActivity.FILE, true);
intent.putExtra(FilePickerActivity.TYPE, FilePickerActivity.MIME_PDF);

```

To get the file picker with multiple MIME type just send a String Array:

```
intent.putExtra(FilePickerActivity.FILE, true);
intent.putExtra(FilePickerActivity.MULTIPLE_TYPES, new String[]{FilePickerActivity.MIME_IMAGE, FilePickerActivity.MIME_PDF});

```

Here is how you would handle the onActivityResult:

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Uri uri = Uri.parse(data.getExtras().getString(FilePickerActivity.URI));
                //Create a file to send it to the server
                //new File(uri.getPath());
                //Load with Glide/Picasso
                //Glide/Picasso.with(this).load(uri).into(imageView);
                Toast.makeText(FilePickerExampleActivity.this, data.getExtras().getString(FilePickerActivity.URI), Toast.LENGTH_SHORT).show();
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

Make sure you know how to handle Uris, You can see commented out on RESULT_OK the 2 most basic examples but just in case.

How to retrieve the uri from the intent:

```
Uri uri = Uri.parse(data.getExtras().getString(FilePickerActivity.URI));

```

How to create a File from the parsed Uri:

```
new File(uri.getPath());

```

How to load with Gliide or Picasso from the parsed Uri:

```
Glide.with(this).load(uri).into(imageView);
Picasso.with(this).load(uri).into(imageView);

```

## Download

Gradle:

dependencies {
    compile 'dk.nodes.gutenberg:gutenberg:2.0'
}
