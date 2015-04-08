package com.circularuins.animebroadcast.Util;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by natsuhikowake on 15/04/08.
 */
public class HideKey {

    public static void hide(final InputMethodManager inputMethodManager, final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //キーボードを隠す
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //タッチしたビューににフォーカスを移す
                view.requestFocus();
                return false;
            }
        });
    }
}
