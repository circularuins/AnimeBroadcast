package com.circularuins.animebroadcast;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.circularuins.animebroadcast.Util.LruBitmapCache;
import com.deploygate.sdk.DeployGate;

/**
 * Created by natsuhikowake on 15/04/06.
 */
public class AnimeBroadcastApplication extends Application {

    private static AnimeBroadcastApplication MY_APPLICATION;
    private static Context sContext;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static AnimeBroadcastApplication getInstance() {
        if(MY_APPLICATION == null) {
            MY_APPLICATION = new AnimeBroadcastApplication();
            MY_APPLICATION.setRequestQueue();
        }
        return MY_APPLICATION;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        MY_APPLICATION = new AnimeBroadcastApplication();
        MY_APPLICATION.setRequestQueue();

        // DeployGateをdebugとreleaseで使用する
        DeployGate.install(this, null, true);
    }

    public Context getContext() {
        if(sContext == null) {
            sContext = getApplicationContext();
        }
        return sContext;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            setRequestQueue();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        if(mRequestQueue == null) {
            setRequestQueue();
        } else if(mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
        }
        return mImageLoader;
    }

    private void setRequestQueue() {
        mRequestQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
    }
}
