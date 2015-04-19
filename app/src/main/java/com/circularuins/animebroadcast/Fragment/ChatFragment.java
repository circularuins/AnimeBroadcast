package com.circularuins.animebroadcast.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.circularuins.animebroadcast.AnimeBroadcastApplication;
import com.circularuins.animebroadcast.Data.Chat;
import com.circularuins.animebroadcast.R;
import com.circularuins.animebroadcast.Util.CustomListView;
import com.circularuins.animebroadcast.Util.HideKey;
import com.fmsirvent.ParallaxEverywhere.PEWImageView;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.apache.commons.lang.RandomStringUtils;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements CustomListView.OnKeyboardAppearedListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROOM_ID = "room_id";
    private static final String ROOM_NAME = "room_name";
    private static final String ROOM_COLOR = "room_color";

    private InputMethodManager inputMethodManager;
    private List<Chat> chats = new ArrayList<>();
    private ChatItemAdapter adapter;
    private String userId;
    private WebSocket ws;
    private Gson gson;
    private String mRoomId;
    private String mRoomName;
    private int mRoomColor;
    private ImageLoader imageLoader;

    @InjectView(R.id.llParent) LinearLayout llParent;
    @InjectView(R.id.editChat) EditText editChat;
    @InjectView(R.id.listChat) CustomListView listChat;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param roomId Parameter 1.
     * @param roomName Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(String roomId, String roomName, int roomColor) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ROOM_ID, roomId);
        args.putString(ROOM_NAME, roomName);
        args.putInt(ROOM_COLOR, roomColor);
        fragment.setArguments(args);
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRoomId = getArguments().getString(ROOM_ID);
            mRoomName = getArguments().getString(ROOM_NAME);
            mRoomColor = getArguments().getInt(ROOM_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view); //フラグメントの場合はビューを渡す
        final ImageButton btn = (ImageButton)view.findViewById(R.id.btnPost);
        final ImageButton btnPhoto = (ImageButton)view.findViewById(R.id.btnPhoto);
        listChat.setListener(this);
        listChat.setBackgroundColor(mRoomColor);

        // キーボードを初期表示しない
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        gson = new Gson();

        userId = RandomStringUtils.randomAlphanumeric(6); //TODO 一旦ランダム 初回起動時にユニークIDを取得するようにする
        // シングルトンのイメージローダーを取得
        imageLoader = AnimeBroadcastApplication.getInstance().getImageLoader();

        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //リストタッチでソフトキーボードを隠す
        HideKey.hide(inputMethodManager, listChat);

        adapter = new ChatItemAdapter();
        listChat.setAdapter(adapter);
        String wsUrl = "ws://circularuins.com:3003/chat?user=" + mRoomId + "/" + userId;

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
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String postChat = editChat.getText().toString();

                                if(postChat.length()!= 0) {
                                    ws.send(postChat);
                                    ws.setStringCallback(new WebSocket.StringCallback() {
                                        @Override
                                        public void onStringAvailable(final String recieveChat) {
                                            new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Object, Object>() {
                                                @Override
                                                protected Object doInBackgroundSafe(Void... voids) throws Exception {
                                                    return null;
                                                }
                                            }).done(new DoneCallback<Object>() {
                                                @Override
                                                public void onDone(Object result) {
                                                    Chat chat = gson.fromJson(recieveChat, Chat.class);
                                                    chats.add(chat);
                                                    adapter.notifyDataSetChanged();
                                                    listChat.smoothScrollToPosition(adapter.getCount());
                                                    editChat.getEditableText().clear();
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

                                    inputMethodManager.hideSoftInputFromWindow(llParent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                    //背景にフォーカスを移す
                                    llParent.requestFocus();
                                }
                            }
                        });
                    }
                });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(ws != null) {
            ws.close();
            ws = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 切断コードを送信して、ルームから退出する
        if(ws != null) {
            ws.send("h8ze@91bmkfp3");
            ws.close();
            ws = null;
        }

        // 必須
        ButterKnife.reset(this);
    }

    private class ChatItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chats.size();
        }

        @Override
        public Object getItem(int position) {
            return chats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView chatId;
            TextView chatText;
            TextView chatDate;
            TextView chatMsg;
            PEWImageView chatImg;
            TextView chatImgDate;
            View v = convertView;
            Chat chat = (Chat)getItem(position);

            //if(v==null){
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(chat.getChatText() != null) {
                    if(chat.getUserId().equals(userId)) {
                        v = inflater.inflate(R.layout.chat_item1, null);

                        chatId = (TextView) v.findViewById(R.id.chatID1);
                        chatText = (TextView) v.findViewById(R.id.chatText1);
                        chatDate = (TextView) v.findViewById(R.id.chatDate1);

                        chatId.setText("ID : " + chat.getUserId());
                        chatText.setText(chat.getChatText());
                        chatDate.setText(chat.getPostTime());
                    } else {
                        v = inflater.inflate(R.layout.chat_item2, null);

                        chatId = (TextView) v.findViewById(R.id.chatID2);
                        chatText = (TextView) v.findViewById(R.id.chatText2);
                        chatDate = (TextView) v.findViewById(R.id.chatDate2);

                        chatId.setText("ID : " + chat.getUserId());
                        chatText.setText(chat.getChatText());
                        chatDate.setText(chat.getPostTime());
                    }
                } else if(chat.getReturnMessage() != null) {
                    v = inflater.inflate(R.layout.chat_message, null);

                    chatMsg = (TextView) v.findViewById(R.id.chatMessage);
                    chatMsg.setText(chat.getReturnMessage());
                } else if(chat.getImageUrl() != null) {
                    v = inflater.inflate(R.layout.chat_image, null);

                    chatImg  = (PEWImageView)v.findViewById(R.id.chatImg);
                    ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer)chatImg.getTag();
                    if (imageContainer != null) {
                        imageContainer.cancelRequest();
                    }
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(chatImg, R.drawable.munoji, R.drawable.kiseki_delete);
                    chatImg.setTag(imageLoader.get(chat.getImageUrl(), listener));

                    chatImgDate = (TextView)v.findViewById(R.id.chatImgDate);
                    chatImgDate.setText(chat.getPostTime());
                }
            //}
            return v;
        }

    }

    @Override
    public void onKeyboardAppeared(boolean isChange) {

        //ListView生成済、且つサイズ変更した（キーボードが出現した）場合
        if(isChange){

            //リストアイテムの総数-1（0番目から始まって最後のアイテム）にスクロールさせる
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //リストアイテムの総数-1（0番目から始まって最後のアイテム）にフォーカスさせる
                    listChat.smoothScrollToPosition(listChat.getCount()-1);
                }
            };
            handler.postDelayed(runnable, 300);

            //スクロールアニメーションが要らない場合はこれでOK
            //listView.setSelection(listView.getCount()-1);
        }
    }
}