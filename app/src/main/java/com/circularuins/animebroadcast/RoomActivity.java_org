package com.circularuins.animebroadcast;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.circularuins.animebroadcast.Data.Chat;
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


public class RoomActivity extends ActionBarActivity {

    @InjectView(R.id.llParent)
    LinearLayout llParent;
    @InjectView(R.id.roomTitle)
    TextView roomTitle;
    @InjectView(R.id.editChat)
    EditText editChat;

    private InputMethodManager inputMethodManager;
    private ListView listChat;
    private ArrayAdapter<String> adapter;
    private WebSocket ws;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        String roomId = getIntent().getStringExtra("room_id");
        String roomName = getIntent().getStringExtra("room_name");
        roomTitle.setText(roomName);
        gson = new Gson();

        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //背景タップでソフトキーボードを隠す
        llParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //キーボードを隠す
                inputMethodManager.hideSoftInputFromWindow(llParent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //背景にフォーカスを移す
                llParent.requestFocus();
                return false;
            }
        });

        final Button btn = (Button)findViewById(R.id.btnPost);
        listChat = (ListView)findViewById(R.id.listChat);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listChat.setAdapter(adapter);
        String userId = "hoge"; //TODO 一旦決め打ち 初回起動時にユニークIDを取得するようにする
        String wsUrl = "ws://circularuins.com:3003/chat?user=" + roomId + "/" + userId;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //切断コードを送信して、ルームから退出する
        if(ws != null) {
            ws.send("h8ze@91bmkfp3");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
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
