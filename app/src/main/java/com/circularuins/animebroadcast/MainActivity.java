package com.circularuins.animebroadcast;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.Fragment.RoomListFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room("r1", "Gのレコンギスタ 最終話", 5, "111", "222", "http://circularuins.com/static/img/raku_2010.jpg"));
        rooms.add(new Room("r2", "寄生獣 最終話", 23, "111", "222", "http://img08.shop-pro.jp/PA01036/793/product/60761767_o1.jpg?20130628160943"));
        rooms.add(new Room("r3", "蒼穹のファフナーEXODUS 23話", 1000, "111", "222", "http://img08.shop-pro.jp/PA01036/793/product/60761767_o1.jpg?20130628160943"));

        RoomListFragment fragment = RoomListFragment.newInstance(rooms);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.listRooms, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

/*
        String[] rooms = {"Gのレコンギスタ 最終話", "寄生獣 最終話", "蒼穹のファフナーEXODUS 23話", "url};
        final ListView listRoom = (ListView)findViewById(R.id.listRooms);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, rooms);
        listRoom.setAdapter(adapter);
        listRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("room", item);
                intent.putExtra("id", "123");
                startActivity(intent);
            }
        });
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
