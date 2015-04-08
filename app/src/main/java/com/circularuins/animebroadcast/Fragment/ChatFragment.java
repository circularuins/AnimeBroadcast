package com.circularuins.animebroadcast.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.circularuins.animebroadcast.Data.Chat;
import com.circularuins.animebroadcast.R;
import com.circularuins.animebroadcast.Util.HideKey;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROOM_ID = "room_id";
    private static final String ROOM_NAME = "room_name";

    private InputMethodManager inputMethodManager;
    private ArrayAdapter<String> adapter;
    private WebSocket ws;
    private Gson gson;
    private String mRoomId;
    private String mRoomName;

    @InjectView(R.id.llParent) LinearLayout llParent;
    @InjectView(R.id.editChat) EditText editChat;
    @InjectView(R.id.listChat) ListView listChat;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param roomId Parameter 1.
     * @param roomName Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(String roomId, String roomName) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ROOM_ID, roomId);
        args.putString(ROOM_NAME, roomName);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view); //フラグメントの場合はビューを渡す
        final Button btn = (Button)view.findViewById(R.id.btnPost);

        // キーボードを初期表示しない
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        gson = new Gson();

        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //リストタッチでソフトキーボードを隠す
        HideKey.hide(inputMethodManager, listChat);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        listChat.setAdapter(adapter);
        String userId = "hoge"; //TODO 一旦決め打ち 初回起動時にユニークIDを取得するようにする
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
                                                    if(chat.getChatText() != null) {
                                                        adapter.add(chat.getChatText());
                                                    } else {
                                                        adapter.add(chat.getReturnMessage());
                                                    }
                                                    adapter.notifyDataSetChanged();
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
    public void onDestroy() {
        super.onDestroy();

        // 切断コードを送信して、ルームから退出する
        if(ws != null) {
            ws.send("h8ze@91bmkfp3");
        }

        // 必須
        ButterKnife.reset(this);
    }
}