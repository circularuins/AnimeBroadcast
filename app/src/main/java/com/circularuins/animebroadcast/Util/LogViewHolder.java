package com.circularuins.animebroadcast.Util;

import android.view.View;
import android.widget.TextView;

import com.circularuins.animebroadcast.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by natsuhikowake on 15/04/05.
 */
public class LogViewHolder {

    public CircleImageView image;
    public TextView id;
    public TextView text;
    public TextView date;

    public LogViewHolder(View base) {
        this.image = (CircleImageView) base.findViewById(R.id.rowImg);
        this.id = (TextView) base.findViewById(R.id.rowId);
        this.text = (TextView) base.findViewById(R.id.rowText);
        this.date = (TextView) base.findViewById(R.id.rowDate);
    }
}
