package dk.nodes.filepicker.processors;

import android.content.Intent;

/**
 * Created by bison on 31/10/17.
 */

public interface UriProcessListener {
    void onProcessingSuccess(Intent intent);
    void onProcessingFailure();
}
