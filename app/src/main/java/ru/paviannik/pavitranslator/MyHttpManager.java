package ru.paviannik.pavitranslator;

import android.app.Activity;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


// Класс который отправляем запрос к Яндексу

public class MyHttpManager {

    public String responsePostTranslate = "";
    private String responsePostLangs;

    public XmlPullParser langListXML = null;
    private Thread sendPostRequestToTranslateThread;
    private Thread sendPostRequestToGetLangListThread;


    public MyHttpManager(){}

    public void sendPostRequestToTranslate(String url,
                                           final List<BasicNameValuePair> queryParams,
                                           final TextView exportView){
        sendPostRequestToTranslateThread = null;
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost http = new HttpPost(url);

        sendPostRequestToTranslateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    http.setEntity(new UrlEncodedFormEntity(queryParams,"UTF-8"));
                    String respText = httpclient.execute(http, new BasicResponseHandler());
                    JSONObject dataJsonObj = new JSONObject(respText);
                    if (dataJsonObj.getJSONArray("text").getString(0).equals("")){
                        responsePostTranslate = exportView.getContext().getString(R.string.response_empty);
                    } else {
                        responsePostTranslate = dataJsonObj.getJSONArray("text").getString(0);
                    }
                    setNewTranslateToOutput(exportView);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        sendPostRequestToTranslateThread.start();
    }


    public void setNewTranslateToOutput(final TextView exportView) {
        Activity act = (Activity) exportView.getContext();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                exportView.setText(responsePostTranslate);
            }
        });
    }

    public void sendPostRequestToGetLangList(String url,
                                             final List<BasicNameValuePair> queryParams){
        sendPostRequestToGetLangListThread = null;
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost http = new HttpPost(url);

        sendPostRequestToGetLangListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    http.setEntity(new UrlEncodedFormEntity(queryParams,"UTF-8"));
                    responsePostLangs = httpclient.execute(http, new BasicResponseHandler());
                    parseXML(responsePostLangs);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });
        sendPostRequestToGetLangListThread.start();
    }


    static {
        final TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Not relevant.
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private void parseXML(String strXML) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        langListXML = factory.newPullParser();
        langListXML.setInput( new StringReader (strXML) );
    }
}
