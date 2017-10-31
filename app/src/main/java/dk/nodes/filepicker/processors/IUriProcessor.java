package dk.nodes.filepicker.processors;

import android.content.Context;
import android.net.Uri;

/**
 * Created by bison on 31/10/17.
 */

public interface IUriProcessor {
    void process(Context context, Uri uri, UriProcessListener uriProcessListener);
}
