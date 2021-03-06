package com.circularuins.animebroadcast.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.Fragment.NavigationDrawerFragment;
import com.circularuins.animebroadcast.Fragment.RoomsCardFragment;
import com.circularuins.animebroadcast.R;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ナビゲーションドロワーの設定
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_main);
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        // ツールバーとナビゲーションドロワーを連動させる
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    mToolbar,
                    R.id.navigation_drawer_main,
                    mDrawerLayout);
        }

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room("r1", "Gのレコンギスタ 最終話", 5, "2015/04/03 01:35", "2015/04/03 01:35", "http://blog-imgs-74.fc2.com/s/a/b/sabusoku/gre_20150216080420ab8.jpg", "http://www.anitube.se/video/82445/Soukyuu-no-Fafner-Dead-Aggressor--Exodus-13-Final"));
        rooms.add(new Room("r2", "寄生獣 最終話", 23, "2015/04/03 01:35", "2015/04/03 01:35", "http://feelgoodokinawa1945.com/wp-content/uploads/2014/03/9fafd31a7971f7cdea200133a182f9de.jpg", "http://ja.wikipedia.org/wiki/%E5%AF%84%E7%94%9F%E7%8D%A3"));
        rooms.add(new Room("r3", "蒼穹のファフナーEXODUS 23話", 1000, "2015/04/03 01:35", "2015/04/03 01:35", "http://img.youtube.com/vi/e9X36_ELxWE/sddefault.jpg", "http://www.anitube.se/video/82445/Soukyuu-no-Fafner-Dead-Aggressor--Exodus-13-Final"));
        rooms.add(new Room("r4", "スペース☆ダンディ 23話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://livedoor.4.blogimg.jp/nizigami/imgs/a/4/a4927cb1.jpg", "http://www.anitube.se/video/82445/Soukyuu-no-Fafner-Dead-Aggressor--Exodus-13-Final"));
        rooms.add(new Room("r5", "魔法少女まどか☆マギカ 9話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://blog-imgs-62.fc2.com/n/o/s/nosweetwithoutsweat/kyoko.jpg", "http://www.anitube.se/video/57456/Mahou-Shoujo-Madoka-Magika-09"));
        rooms.add(new Room("r6", "攻殻機動隊 99話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://image.eiga.k-img.com/images/movie/55879/original.jpg?1396890531", "http://www.anitube.se/video/82445/Soukyuu-no-Fafner-Dead-Aggressor--Exodus-13-Final"));

        // 部屋一覧のcardviewをセットする
        // RoomsCardFragmentのファクトリーメソッドに、表示させる部屋データを渡す
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.cardRooms, RoomsCardFragment.newInstance(rooms))
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.navi_menu1);
                break;
            case 2:
                mTitle = getString(R.string.navi_menu2);
                break;
            case 3:
                mTitle = getString(R.string.navi_menu3);
                break;
        }
    }

    public void restoreActionBar() {

        mToolbar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



}