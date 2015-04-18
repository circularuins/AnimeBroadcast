package com.circularuins.animebroadcast.Fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.circularuins.animebroadcast.Activity.RoomActivity;
import com.circularuins.animebroadcast.AnimeBroadcastApplication;
import com.circularuins.animebroadcast.Data.Population;
import com.circularuins.animebroadcast.Data.PopulationList;
import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.R;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;

/**
 * Created by natsuhikowake on 15/04/14.
 */
public class RoomsCardFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WebSocket ws;
    private Gson gson;
    private LinearLayout llCardRoom;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static RoomsCardFragment newInstance(ArrayList<Room> rooms) {
        RoomsCardFragment fragment = new RoomsCardFragment();
        fragment.setRetainInstance(true);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("rooms", rooms);
        fragment.setArguments(bundle);
        return fragment;
    }

    public RoomsCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 引っ張って更新処理のUI
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_rooms);
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

        ArrayList<Room> rooms = getArguments().getParcelableArrayList("rooms");
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        llCardRoom = (LinearLayout)view.findViewById(R.id.cardLinearRoom);
        llCardRoom.removeAllViews();
        // シングルトンのイメージローダーとキューを取得
        ImageLoader imageLoader = AnimeBroadcastApplication.getInstance().getImageLoader();
        RequestQueue queue = AnimeBroadcastApplication.getInstance().getRequestQueue();
        int i = 0;
        for(final Room room : rooms) {
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.card_room, null);
            final CardView cardView = (CardView) linearLayout.findViewById(R.id.cardViewRoom);
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
            // カードビューの背景色を画像から取得する
            queue.add(new ImageRequest(room.getImageUrl(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bm) {
                            Palette.generateAsync(bm, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    final int roomColor;
                                    if (palette != null) {
                                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                                        if (vibrantSwatch != null) {
                                            roomColor = vibrantSwatch.getRgb();
                                            cardView.setCardBackgroundColor(roomColor);
                                        } else {
                                            roomColor = R.color.white;
                                        }
                                        cardView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), RoomActivity.class);
                                                intent.putExtra("room_id", room.getRoomId());
                                                intent.putExtra("room_name", room.getRoomName());
                                                intent.putExtra("room_url", room.getImageUrl());
                                                intent.putExtra("room_color", roomColor);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                if (Build.VERSION.SDK_INT < 21) {
                                                    startActivity(intent);
                                                } else {
                                                    startActivity(intent,
                                                            ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }, 0, 0, Bitmap.Config.ARGB_8888, null));

            cardView.setTag(i);
            linearLayout.setTag(R.string.card_view_tag1, room.getRoomId());
            llCardRoom.addView(linearLayout, i);
            i++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // WebSocketで、各チャットルームの入室人数を取得する
        gson = new Gson();
        String wsUrl = "ws://circularuins.com:3003/stream";
        AsyncHttpClient.getDefaultInstance().websocket(
                wsUrl,
                "my-protocol",
                new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if(ex != null) {
                            ex.printStackTrace();
                            return;
                        }
                        ws = webSocket;
                        ws.setStringCallback(new WebSocket.StringCallback() {
                            @Override
                            public void onStringAvailable(final String recievePopulation) {
                                new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Object, Object>() {
                                    @Override
                                    protected Object doInBackgroundSafe(Void... voids) throws Exception {
                                        return null;
                                    }
                                }).done(new DoneCallback<Object>() {
                                    @Override
                                    public void onDone(Object result) {
                                        PopulationList populationList = gson.fromJson(recievePopulation, PopulationList.class);
                                        for(int i = 0; i < llCardRoom.getChildCount(); i++) {
                                            LinearLayout ll = (LinearLayout)llCardRoom.getChildAt(i);
                                            CardView c = (CardView)ll.getChildAt(0);
                                            LinearLayout l = (LinearLayout)c.getChildAt(0);
                                            RelativeLayout r = (RelativeLayout)l.getChildAt(0);
                                            TextView t = (TextView)r.getChildAt(1);
                                            t.setText("0人");
                                        }
                                        for(int i = 0; i < llCardRoom.getChildCount(); i++) {
                                            LinearLayout ll = (LinearLayout)llCardRoom.getChildAt(i);
                                            for(Population p : populationList.getPopulations()) {
                                                if(p.getRoomId().equals(ll.getTag(R.string.card_view_tag1))) {
                                                    CardView c = (CardView)ll.getChildAt(0);
                                                    LinearLayout l = (LinearLayout)c.getChildAt(0);
                                                    RelativeLayout r = (RelativeLayout)l.getChildAt(0);
                                                    TextView t = (TextView)r.getChildAt(1);
                                                    t.setText(p.getPopulation() + "人");
                                                }
                                            }
                                        }
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
                        });

                    }
                });
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
