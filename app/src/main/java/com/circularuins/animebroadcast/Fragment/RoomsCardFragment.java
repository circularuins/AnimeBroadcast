package com.circularuins.animebroadcast.Fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.circularuins.animebroadcast.Activity.RoomActivity;
import com.circularuins.animebroadcast.AnimeBroadcastApplication;
import com.circularuins.animebroadcast.Data.Room;
import com.circularuins.animebroadcast.R;

import java.util.ArrayList;

/**
 * Created by natsuhikowake on 15/04/14.
 */
public class RoomsCardFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        LinearLayout llCardRoom = (LinearLayout)view.findViewById(R.id.cardLinearRoom);
        llCardRoom.removeAllViews();
        // シングルトンのイメージローダーを取得
        ImageLoader imageLoader = AnimeBroadcastApplication.getInstance().getImageLoader();
        int i = 0;
        for(final Room room : rooms) {
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
                    Intent intent = new Intent(getActivity(), RoomActivity.class);
                    intent.putExtra("room_id", room.getRoomId());
                    intent.putExtra("room_name", room.getRoomName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (Build.VERSION.SDK_INT < 21) {
                        startActivity(intent);
                    } else {
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }
                }
            });
            llCardRoom.addView(linearLayout, i);
            i++;
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
