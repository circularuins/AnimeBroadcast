package com.circularuins.animebroadcast.Fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.R;
import com.circularuins.animebroadcast.RoomActivity;
import com.circularuins.animebroadcast.Util.LruBitmapCache;
import com.circularuins.animebroadcast.Util.RoomViewHolder;

import java.util.ArrayList;

/**
 * Created by natsuhikowake on 15/04/05.
 */
public class RoomListFragment extends ListFragment {

    private ArrayList<Room> rooms;
    private RoomAdapter adapter;
    //Volley関連の変数
    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private RoomViewHolder holder; //innerclassで使用するので、メンバ変数にしておく

    //ファクトリーメソッド
    public static RoomListFragment newInstance(ArrayList<Room> rooms) {
        RoomListFragment fragment = new RoomListFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("rooms", rooms);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //queue for imageLoader
        mQueue = Volley.newRequestQueue(getActivity());

        rooms = getArguments().getParcelableArrayList("rooms");
        ArrayList<RowModel> list = new ArrayList<RowModel>();

        for (Room room : rooms) {
            list.add(new RowModel(room.image, room.getRoomId(), room.getImageUrl(), room.getRoomName(), room.getCountPosts(), room.getUpdatedOn(), room.getCreatedOn()));
        }

        adapter = new RoomAdapter(list);
        setListAdapter(adapter);
    }

    private RowModel getModel(int position) {
        return (((RoomAdapter) getListAdapter()).getItem(position));
    }

    class RoomAdapter extends ArrayAdapter<RowModel> {
        RoomAdapter(ArrayList<RowModel> list) {
            super(getActivity(), R.layout.room_row, list);
            imageLoader = new ImageLoader(mQueue, new LruBitmapCache());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.room_row, parent, false);
            }

            //holderパターンを使用して高速化を狙う
            holder = (RoomViewHolder) row.getTag();
            //その入れ物が空かどうか確認する
            if (holder == null) {
                //各行内の個別のオブジェクトの参照を一度だけfindViewByIdする
                holder = new RoomViewHolder(row);
                row.setTag(holder);
            }
            final RowModel model = getModel(position);
            //holder.image.setTag(new Integer(position));

            /*
             * 初めて画像を読み込む場合はvolleyでネットから。
             * それ以降は、tweetオブジェクトから読み込む。
            */
            if (model.image != null) {
                holder.image.setImageBitmap(model.image);
            } else {
                imageLoader.get(model.url, new ImageLoader.ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "Image Load Error: " + error.getMessage());
                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                        if (response.getBitmap() != null) {
                            //画像を非同期に読み込んで、ImageViewにセットする
                            holder.image.setImageBitmap(response.getBitmap());
                            //該当のTweetオブジェクトにBitmapをセット
                            for (Room room : rooms) {
                                if (model.id.equals(room.getRoomId()))
                                    room.image = response.getBitmap();
                            }
                        }
                    }
                });
            }

            holder.name.setText(model.name);
            holder.text.setText(model.name);
            holder.date.setText(model.updated);

            return row;
        }
    }

    /*
    * 各行のデータを保存するクラス
    *（エンティティ）
     */
    class RowModel {
        Bitmap image;
        String id;
        String url;
        String name;
        int posts;
        String updated;
        String created;

        RowModel(Bitmap image, String id, String url, String name, int posts, String updated, String created) {
            this.image = image;
            this.id = id;
            this.url = url;
            this.name = name;
            this.posts = posts;
            this.updated = updated;
            this.created = created;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        RowModel model = getModel(position);
        Intent intent = new Intent(getActivity(), RoomActivity.class);
        intent.putExtra("room", model.name);
        intent.putExtra("id", model.id);
        startActivity(intent);
    }
}
