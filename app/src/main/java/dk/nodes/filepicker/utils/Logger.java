package dk.nodes.filepicker.utils;

import android.util.Log;

import dk.nodes.filepicker.BuildConfig;

/**
 * Created by bison on 31/10/17.
 */

public class Logger {
    public static void loge(String tag, String msg)
    {
        if(BuildConfig.DEBUG)
        {
            Log.e(tag, msg);
        }
    }

    public static void logd(String tag, String msg)
    {
        if(BuildConfig.DEBUG)
        {
            Log.d(tag, msg);
        }
    }
}
