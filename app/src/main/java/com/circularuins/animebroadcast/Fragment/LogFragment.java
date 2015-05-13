package com.circularuins.animebroadcast.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.circularuins.animebroadcast.AnimeBroadcastApplication;
import com.circularuins.animebroadcast.Data.Article;
import com.circularuins.animebroadcast.R;
import com.circularuins.animebroadcast.Util.LogViewHolder;
import com.circularuins.animebroadcast.Web.ApiManager;
import com.google.gson.Gson;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ROOM_ID = "room_id";
    private static final String ROOM_COLOR = "room_color";

    private ArrayList<Article> articles = new ArrayList<>();
    private LogAdapter adapter;
    private String userId;
    private Gson gson;
    private String mRoomId;
    private int mRoomColor;
    private ImageLoader imageLoader;
    private LogViewHolder holder;

    @InjectView(R.id.llParent)
    LinearLayout llParent;
    @InjectView(R.id.listLog)
    ListView listLog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param roomId Parameter 1.
     * @param roomColor Parameter 2.
     * @return A new instance of fragment LogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String roomId, int roomColor) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putString(ROOM_ID, roomId);
        args.putInt(ROOM_COLOR, roomColor);
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRoomId = getArguments().getString(ROOM_ID);
            mRoomColor = getArguments().getInt(ROOM_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.inject(this, view); //フラグメントの場合はビューを渡す
        gson = new Gson();

        new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Object, Object>() {
            @Override
            protected Object doInBackgroundSafe(Void... voids) throws Exception {
                try {
                    ApiManager apiManager = new ApiManager();
                    articles = apiManager.loadArticles(mRoomId);
                } catch (Exception e) {

                }
                return null;
            }
        }).done(new DoneCallback<Object>() {
            @Override
            public void onDone(Object result) {
                ArrayList<RowModel> list = new ArrayList<RowModel>();
                for (Article article : articles) {
                    list.add(new RowModel(article.image, article.getUserId(), article.getProfileUrl(), article.getText(), article.getDate()));
                }

                adapter = new LogAdapter(list);
                listLog.setAdapter(adapter);
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

        return view;
    }

    private RowModel getModel(int position) {
        return (((LogAdapter)listLog.getAdapter()).getItem(position));
    }

    class LogAdapter extends ArrayAdapter<RowModel> {

        LogAdapter(ArrayList<RowModel> list) {
            super(getActivity(), R.layout.row_log, list);
            // シングルトンのイメージローダーを取得
            imageLoader = AnimeBroadcastApplication.getInstance().getImageLoader();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_log, parent, false);
            }

            //holderパターンを使用して高速化を狙う
            holder = (LogViewHolder) convertView.getTag();
            //その入れ物が空かどうか確認する
            if (holder == null) {
                //各行内の個別のオブジェクトの参照を一度だけfindViewByIdする
                holder = new LogViewHolder(convertView);
                convertView.setTag(holder);
            }
            final RowModel model = getModel(position);
            //holder.image.setTag(new Integer(position));

            if (model.image != null) {
                holder.image.setImageBitmap(model.image);
            } else {
                if(model.profurl != null) {
                    // リクエストのキャンセル処理
                    ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) holder.image.getTag();
                    if (imageContainer != null) {
                        imageContainer.cancelRequest();
                    }

                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.image,
                            R.drawable.munoji, R.drawable.kiseki_delete);
                    holder.image.setTag(imageLoader.get(model.profurl, listener));
                }
            }

            holder.id.setText(model.id);
            holder.text.setText(model.text);
            holder.date.setText(model.date);

            return convertView;
        }
    }

    /*
    * 各行のデータを保存するクラス
    *（エンティティ）
     */
    class RowModel {
        Bitmap image;
        String id;
        String profurl;
        String text;
        String date;

        public RowModel(Bitmap image, String id, String profurl, String text, String date) {
            this.image = image;
            this.id = id;
            this.profurl = profurl;
            this.text = text;
            this.date = date;
        }
    }
}