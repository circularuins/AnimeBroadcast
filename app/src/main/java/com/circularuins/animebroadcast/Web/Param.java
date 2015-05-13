package com.circularuins.animebroadcast.Web;

import java.util.ArrayList;

/**
 * Created by natsuhikowake on 15/05/13.
 */
public class Param {

    final public String key;
    public Object value;
    public ArrayList<Param> params;

    public Param(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Param(String key, ArrayList<Param> params) {
        this.key = key;
        this.params = params;
    }
}
