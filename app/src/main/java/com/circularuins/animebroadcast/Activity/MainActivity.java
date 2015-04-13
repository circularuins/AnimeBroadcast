package com.circularuins.animebroadcast.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.circularuins.animebroadcast.AnimeBroadcastApplication;
import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.Fragment.NavigationDrawerFragment;
import com.circularuins.animebroadcast.R;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_main);
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);

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
        rooms.add(new Room("r1", "Gのレコンギスタ 最終話", 5, "2015/04/03 01:35", "2015/04/03 01:35", "http://blog-imgs-74.fc2.com/s/a/b/sabusoku/gre_20150216080420ab8.jpg"));
        rooms.add(new Room("r2", "寄生獣 最終話", 23, "2015/04/03 01:35", "2015/04/03 01:35", "http://feelgoodokinawa1945.com/wp-content/uploads/2014/03/9fafd31a7971f7cdea200133a182f9de.jpg"));
        rooms.add(new Room("r3", "蒼穹のファフナーEXODUS 23話", 1000, "2015/04/03 01:35", "2015/04/03 01:35", "http://img.youtube.com/vi/e9X36_ELxWE/sddefault.jpg"));
        rooms.add(new Room("r4", "スペース☆ダンディ 23話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://livedoor.4.blogimg.jp/nizigami/imgs/a/4/a4927cb1.jpg"));
        rooms.add(new Room("r5", "魔法少女まどか☆マギカ 10話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://blog-imgs-62.fc2.com/n/o/s/nosweetwithoutsweat/kyoko.jpg"));
        rooms.add(new Room("r6", "攻殻機動隊 99話", 999, "2015/04/03 01:35", "2015/04/03 01:35", "http://image.eiga.k-img.com/images/movie/55879/original.jpg?1396890531"));

        /*RoomListFragment fragment = RoomListFragment.newInstance(rooms);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.listRooms, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();*/

        // 引っ張って更新処理のUI
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_rooms);
        // 色指定
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.cyan600);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // このアプリでは更新処理のダミーとして1秒後に更新終了処理を呼び出している
                mHandler.removeCallbacks(mRefreshDone);
                mHandler.postDelayed(mRefreshDone, 1000); // 3000にすると3秒間プログレスが表示されるよ
            }
        });

        LinearLayout llCardRoom = (LinearLayout)this.findViewById(R.id.cardLinearRoom);
        llCardRoom.removeAllViews();
        // シングルトンのイメージローダーを取得
        ImageLoader imageLoader = AnimeBroadcastApplication.getInstance().getImageLoader();
        int i = 0;
        for(final Room room : rooms) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.card_room, null);

            CardView cardView = (CardView) linearLayout.findViewById(R.id.cardViewRoom);
            ImageView cardImg = (ImageView) linearLayout.findViewById(R.id.cardImg);
            TextView cardName = (TextView) linearLayout.findViewById(R.id.cardName);
            TextView cardPost = (TextView) linearLayout.findViewById(R.id.cardPost);
            TextView cardDate = (TextView) linearLayout.findViewById(R.id.cardDate);

            cardName.setText(room.getRoomName());
            cardPost.setText(room.getCountPosts() + "投稿");
            cardDate.setText(room.getUpdatedOn());
            // リクエストのキャンセル処理
            ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer)cardImg.getTag();
            if (imageContainer != null) {
                imageContainer.cancelRequest();
            }
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(cardImg, R.drawable.munoji, R.drawable.kiseki_delete);
            cardImg.setTag(imageLoader.get(room.getImageUrl(), listener));
            cardView.setTag(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                    intent.putExtra("room_id", room.getRoomId());
                    intent.putExtra("room_name", room.getRoomName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if(Build.VERSION.SDK_INT < 21) {
                        startActivity(intent);
                    } else {
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    }
                }
            });

            llCardRoom.addView(linearLayout, i);
            i++;
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

    private Handler mHandler = new Handler();
    private final Runnable mRefreshDone = new Runnable() {

        @Override
        public void run() {
            // 3. プログレスを終了させる
            mSwipeRefreshLayout.setRefreshing(false);
        }

    };

}