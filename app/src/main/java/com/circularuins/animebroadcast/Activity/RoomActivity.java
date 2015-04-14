package com.circularuins.animebroadcast.Activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.circularuins.animebroadcast.Adapter.PageAdapter;
import com.circularuins.animebroadcast.Fragment.NavigationDrawerFragment;
import com.circularuins.animebroadcast.R;
import com.circularuins.animebroadcast.Util.ImageFromNet;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;


public class RoomActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;
    private TabWidget tabWidget;
    private View mIndicator;
    private Drawable img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // インテント経由で部屋情報を受け取る
        String roomId = getIntent().getStringExtra("room_id");
        String roomName = getIntent().getStringExtra("room_name");
        final String roomUrl = getIntent().getStringExtra("room_url");

        // ナビゲーションドロワー
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_room);
        mTitle = getTitle();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_room);

        // ツールバー
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    mToolbar,
                    R.id.navigation_drawer_room,
                    drawerLayout);
            mToolbar.setSubtitle(roomName);
            // ツールバーの背景に画像を読み込む
            new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Object, Object>() {
                @Override
                protected Object doInBackgroundSafe(Void... voids) throws Exception {
                    img = ImageFromNet.createDrawable(roomUrl);
                    return null;
                }
            }).done(new DoneCallback<Object>() {
                @Override
                public void onDone(Object result) {
                    mToolbar.setBackground(img);
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable result) {
                }
            }).always(new AlwaysCallback<Object, Throwable>() {
                @Override
                public void onAlways(Promise.State state, Object resolved, Throwable rejected) {
                }
            });
        }

        // ビューページャー
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        mIndicator = findViewById(R.id.indicator);

        /** タブ関連 */
        // ViewPagerのセットアップ
        PagerAdapter adapter = new PageAdapter(getSupportFragmentManager(), roomId, roomName);
        pager.setAdapter(adapter);
        // タブの設定
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);
        // タブの構築
        for (int i=0; i<adapter.getCount(); i++) {
            TextView tv = (TextView) inflater.inflate(R.layout.tab_widget, tabWidget, false);
            tv.setText( adapter.getPageTitle(i) );
            tabHost.addTab( tabHost
                    .newTabSpec( String.valueOf(i) )
                    .setIndicator( tv )
                    .setContent( android.R.id.tabcontent ));
        }

        // リスナー。タブのスクロールとインジケーターのスクロールで呼ばれる。
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // タブのタッチでページ移動とスクロール移動に繋がる
                pager.setCurrentItem(Integer.valueOf(tabId));
            }
        });
        pager.setOnPageChangeListener(new PageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // この記述が無いと、
                // スライドでページ移動後にタブタッチで移動ができなかったり、
                // ２つ隣のページに一気に飛べなかったりする。
                tabHost.setCurrentTab(position);
            }
        });
        pager.setCurrentItem(1, false); //チャットページを初期表示
        //ページ切り替え後すぐにページのスクロールをキャンセルする
        pager.dispatchTouchEvent(MotionEvent.obtain
                (SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));

        // タブウィジェット
        // タブの区切り線を消す
        tabWidget.setStripEnabled(false);
        tabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        // タブの下に影を作る（この方法だと21以降でないと反映されない）
        if(Build.VERSION.SDK_INT >= 21) {
            float elevation = 4 * getResources().getDisplayMetrics().density;
            tabHost.setElevation(elevation);
        }
    }

    // スクロールできるタブ関連
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        private int mScrollingState = ViewPager.SCROLL_STATE_IDLE;
        @Override	// ページが移動したら呼ばれる
        public void onPageSelected(int position) {
            Log.v("testMA", "PageChangeListener / onPageSelected");
            // スクロール中はonPageScrolled()で描画するのでここではしない
            if (mScrollingState == ViewPager.SCROLL_STATE_IDLE) {
                updateIndicatorPosition(position, 0);
            }
            // ページのスクロールでウィジェットの動作（色変更など）に対応させる
            tabWidget.setCurrentTab(position);
        }
        @Override	// スクロールで呼ばれる
        public void onPageScrollStateChanged(int state) {
            Log.v("testMA", "PageChangeListener / onPageScrollStateChanged");
            // ？　無くても動く
            //mScrollingState = state;
        }
        @Override	// スクロールしたら呼ばれる
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.v("testMA", "PageChangeListener / onPageScrolled");
            updateIndicatorPosition(position, positionOffset);
        }
        // インジケーターの動き設定
        private void updateIndicatorPosition(int position, float positionOffset) {
            Log.v("testMA", "PageChangeListener / updateIndicatorPosition");
            // 現在の位置のタブのView
            View tabView = tabWidget.getChildAt(position);
            // 現在の位置の次のタブの横幅
            //int width2 = view2 == null ? width : view2.getWidth();
            // インディケータの幅（現在位置のタブ幅）
            int indicatorWidth = tabView.getWidth();
            // インディケータの左端の位置
            int indicatorLeft = (int) ((position + positionOffset) * indicatorWidth);
            // インディケータの幅と左端の位置をセット
            final FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
            layoutParams.width = indicatorWidth;
            layoutParams.setMargins(indicatorLeft, 0, 0, 0);
            mIndicator.setLayoutParams(layoutParams);

            // インディケータが画面に入る様に、タブの領域をスクロール
            //mTrackScroller.scrollTo(indicatorLeft - mIndicatorOffset, 0);
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
            ((RoomActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}