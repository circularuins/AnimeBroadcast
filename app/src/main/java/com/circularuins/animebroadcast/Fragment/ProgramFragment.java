package com.circularuins.animebroadcast.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.circularuins.animebroadcast.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgramFragment extends Fragment {

    private static final String PROGRAM_URL = "program_url";
    private String programUrl;

    @InjectView(R.id.webView)
    WebView webView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param programUrl Parameter 1.
     * @return A new instance of fragment testFragment3.
     */
    public static ProgramFragment newInstance(String programUrl) {
        ProgramFragment fragment = new ProgramFragment();
        Bundle args = new Bundle();
        args.putString(PROGRAM_URL, programUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            programUrl = getArguments().getString(PROGRAM_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_program, container, false);
        ButterKnife.inject(this, view); //フラグメントの場合はビューを渡す

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setAppCacheEnabled(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setSaveFormData(true);
        webView.loadUrl(programUrl);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        webView.loadData("", "text/html", "utf-8");
        ButterKnife.reset(this);
    }
}