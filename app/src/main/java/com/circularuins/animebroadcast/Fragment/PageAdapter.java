package com.circularuins.animebroadcast.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private String roomId;
    private String roomName;

    public PageAdapter(FragmentManager fm, String roomId, String roomName) {
        super(fm);
        this.roomId = roomId;
        this.roomName = roomName;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return ChatFragment.newInstance(roomId, roomName);
            case 1:
                return new TestFragment1();
            case 2:
                return new TestFragment1();
            default:
                return new TestFragment1();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "チャット";
            case 1:
                return "過去ログ";
            case 2:
                return "評価";
            default:
                return "";
        }
    }
}