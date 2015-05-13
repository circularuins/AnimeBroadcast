package com.circularuins.animebroadcast.Web;

import com.circularuins.animebroadcast.Data.Article;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by natsuhikowake on 15/05/13.
 */
public class ApiManager {

    private Gson gson;
    private DefaultHttpClient httpClient;

    private static final String USER_AGENT = "Anibro Client";
    private static final String BASE_URL = "http://circularuins.com:3003/";
    // コネクションタイムアウト (ms)
    private static Integer CONNECTION_TIMEOUT = 300 * 1000;
    // ソケットタイムアウト (ms)
    private static Integer SOCKET_TIMEOUT = 300 * 1000;

    public ApiManager() {
        this.gson = new Gson();
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);

        this.httpClient = new DefaultHttpClient(httpParams);
        this.httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
    }

    public ArrayList<Article> loadArticles(String room) throws IOException, JSONException {
        String url = BASE_URL + "articles";

        List<Param> params = new ArrayList<Param>();
        if (room != null) {
            params.add(new Param("room", room));
        }

        String json = httpGet(url, params);
        ArrayList<Article> result = parseJson(json, new TypeToken<ArrayList<Article>>() {}.getType());
        return result;
    }

    protected String httpGet(String url, List<Param> params) {
        List<NameValuePair> reqParams = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Param param : params) {
                reqParams.add(new BasicNameValuePair(param.key, String.valueOf(param.value)));
            }
        }

        String query = URLEncodedUtils.format(reqParams, "UTF-8");
        HttpGet get = new HttpGet(url + "?" + query);
        return httpRequest(get);
    }

    protected String httpRequest(HttpRequestBase requestBase) {
        int httpStatus = 0;
        String resText = null;
        try {
            HttpResponse res = httpClient.execute(requestBase);
            httpStatus = res.getStatusLine().getStatusCode();
            resText = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);

            if (httpStatus == HttpStatus.SC_OK ) {
                return resText;
            } else {
                return null;
            }
        } catch (Exception e) {
            /*Crashlytics.log(Log.DEBUG, "[URL]", requestBase.getURI().toString());
            Crashlytics.log(Log.DEBUG, "[HTTPステータス]", "" + httpStatus);
            Crashlytics.log(Log.DEBUG, "[HTTPレスポンス]", resText);
            Crashlytics.logException(e);*/
            return null;
        } finally {
            if (requestBase != null) {
                requestBase.abort();
            }
        }
    }

    protected <T> T parseJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonParseException e) {
            throw e;
        }
    }
}
