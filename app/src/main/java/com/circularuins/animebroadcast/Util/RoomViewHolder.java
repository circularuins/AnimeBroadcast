package com.circularuins.animebroadcast.Util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.circularuins.animebroadcast.R;

/**
 * Created by natsuhikowake on 15/04/05.
 */
public class RoomViewHolder {

    public ImageView image;
    public TextView name;
    public TextView text;
    public TextView date;

    public RoomViewHolder(View base) {
        this.image = (ImageView) base.findViewById(R.id.rowImg);
        this.name = (TextView) base.findViewById(R.id.rowName);
        this.text = (TextView) base.findViewById(R.id.rowText);
        this.date = (TextView) base.findViewById(R.id.rowDate);
    }
}
