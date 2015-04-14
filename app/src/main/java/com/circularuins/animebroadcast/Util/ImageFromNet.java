package com.circularuins.animebroadcast.Util;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by natsuhikowake on 15/04/15.
 */
public class ImageFromNet {

    public static Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.openConnection().getInputStream();
        return content;
    }

    public static Drawable createDrawable(String uri)
    {
        try {
            InputStream is = (InputStream) fetch(uri);
            Drawable img = Drawable.createFromStream(is, "");
            is.close();
            return img;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("createDrawable", "error:" + uri);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("createDrawable","error:"+uri);
            return null;
        }
    }
}
