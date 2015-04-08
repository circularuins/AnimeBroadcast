package com.circularuins.animebroadcast.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by natsuhikowake on 15/04/03.
 */
public class ChatAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> chats;

    public ChatAdapter(Activity activity, ArrayList<String> chats) {
        this.activity = activity;
        this.chats = chats;
    }

    public void addList(String chat) {
        chats.add(chat);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
