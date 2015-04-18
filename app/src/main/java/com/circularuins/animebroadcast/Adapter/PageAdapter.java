package com.circularuins.animebroadcast.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.circularuins.animebroadcast.Fragment.ChatFragment;
import com.circularuins.animebroadcast.Fragment.TestFragment1;

public class PageAdapter extends FragmentPagerAdapter {

    private String roomId;
    private String roomName;
    private int roomColor;

    public PageAdapter(FragmentManager fm, String roomId, String roomName, int roomColor) {
        super(fm);
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomColor = roomColor;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new TestFragment1();
            case 1:
                return ChatFragment.newInstance(roomId, roomName, roomColor);
            case 2:
                return new TestFragment1();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "詳細";
            case 1:
                return "チャット";
            case 2:
                return "過去ログ";
        }
        return null;
    }
}