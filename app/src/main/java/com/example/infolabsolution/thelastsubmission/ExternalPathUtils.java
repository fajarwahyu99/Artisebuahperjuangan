package com.example.infolabsolution.thelastsubmission;



import android.content.Context;

public class ExternalPathUtils {

    private static final String TAG = ExternalPathUtils.class.getSimpleName();

    public static String getExternalPathBasicFileName(Context context) {

        return context.getExternalCacheDir().getAbsolutePath();
    }
}
