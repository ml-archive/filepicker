package dk.nodes.filepicker.processors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dk.nodes.filepicker.utils.Logger;

/**
 * Created by bison on 31/10/17.
 */

public class UriProcessor implements UriProcessListener {
    private static final String TAG = UriProcessor.class.getSimpleName();
    private List<IUriProcessor> processors = new ArrayList<>();
    private Iterator<IUriProcessor> currentProcessorIt;
    private Uri uri;
    private Context context;
    private UriProcessListener listener;

    public UriProcessor() {
        // register URI processors
        processors.add(new GooglePhotosProcessor());
        processors.add(new GoogleDocumentsProcessor());
        processors.add(new GoogleMediaProcessor());
        processors.add(new GoogleDriveProcessor());
        processors.add(new GenericContentProviderProcessor());
    }

    public void process(Context context, Uri uri, UriProcessListener listener)
    {
        this.listener = listener;
        this.uri = uri;
        this.context = context;
        currentProcessorIt = processors.iterator();
        processNext(context, uri);
    }

    private void processNext(Context context, Uri uri)
    {
        if(currentProcessorIt.hasNext())
        {
            Logger.logd(TAG, "Processing next");
            IUriProcessor processor = currentProcessorIt.next();
            processor.process(context, uri, this);
        }
        else {
            Logger.loge(TAG, "No more processors to process, propagate failure back to caller");
            if(listener != null)
                listener.onProcessingFailure();
        }
    }


    @Override
    public void onProcessingSuccess(Intent intent) {
        Logger.logd(TAG, "onProcessingSuccess");
        if(listener != null)
            listener.onProcessingSuccess(intent);

    }

    @Override
    public void onProcessingFailure() {
        processNext(context, uri);
    }
}
