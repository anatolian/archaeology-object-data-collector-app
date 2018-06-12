package com.test.temp;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.VolleyError;

/**
 * Created by msenol on 14.04.2015.
 */
abstract public class ImageResponseWrapper {
    Context currentContext;

    public ImageResponseWrapper(Context aContext) {
        this.currentContext = aContext;
    }

    //these are abstract methods that will be implemented int he VolleyWrapper class
    abstract void responseMethod(Bitmap bitmap);
    abstract void errorMethod(VolleyError error);
}
