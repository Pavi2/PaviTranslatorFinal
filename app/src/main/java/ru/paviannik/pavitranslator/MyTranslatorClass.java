package ru.paviannik.pavitranslator;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static ru.paviannik.pavitranslator.MainActivity.PT_log;
import static ru.paviannik.pavitranslator.MyHistoryManager.APP_PATH;

/*
Класс для работы с переводом.
Отправляем информацию в MyHttpManager
 */

public class MyTranslatorClass {

    // Объявляем String переменные
    private static String appIdForSDKString =
            "trnsl.1.1.20170404T065716Z.c13276228c48afb1.c6f9c9c84d30440ee973929863d1ffd5eefb97fd";
    private static String getLangsFromSDKString =
            "https://translate.yandex.net/api/v1.5/tr/getLangs";
    private static String translateTextFromSDKString =
            "https://translate.yandex.net/api/v1.5/tr.json/translate";
    public String langBegin = "";
    public String langEnd = "";


    private MyHttpManager myHttpManager =new MyHttpManager();
    private Map<String, String> mainMapLanguages = new HashMap<String, String>();
    private Thread getLangListThread;
    private Thread getTranslateThread;


    public MyTranslatorClass(){}

    public MyTranslatorClass(MyHttpManager myHttpManager){
        this.myHttpManager = myHttpManager;
    }


    public void getTranslate(final EditText textField, final TextView exportView){

        getTranslateThread = null;

        if (!isInternetAvailable(textField.getContext())){
            Toast.makeText(textField.getContext(),
                    exportView.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG).show();
            return;
        }

        getTranslateThread = new Thread(new Runnable() {
            @Override
            public void run() {

                String textFromField = textField.getText().toString();
                Log.e(PT_log,textFromField);
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("text", textFromField));
                nameValuePairs.add(new BasicNameValuePair("lang",getLangPair()));


                myHttpManager.sendPostRequestToTranslate(translateTextFromSDKString,
                        nameValuePairs,
                        exportView);
            }
        });
        getTranslateThread.start();

    }
    public void getLangsList(final Spinner beginLangs,
                             final Spinner endLangs,
                             final Activity activity){

        getLangListThread = null;
        Log.d(PT_log, "System lang: " + Locale.getDefault().getLanguage());
        // загружаем список с диска, если он доступен
        loadLanguageFromDiskIfAvailabled(beginLangs,endLangs,activity);
        // проверяем интернет-соединение
        if (!isInternetAvailable(endLangs.getContext())){
            Toast.makeText(endLangs.getContext(),
                    beginLangs.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG).show();
            return;
        }



       // Инициируем новый поток и запускаем его
        getLangListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Заносим все данные в список.
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("ui",
                        Locale.getDefault().getLanguage()));
                // Отправляем запрос в MyHttpManager
                myHttpManager.sendPostRequestToGetLangList(getLangsFromSDKString,nameValuePairs);
                int secs = 0;
                while(myHttpManager.langListXML == null){
                    secs++;
                }
                Log.d(PT_log, "Last: "+secs);
                XmlPullParser parser = myHttpManager.langListXML;
                try {
                    while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
                        if (parser.getEventType() == XmlPullParser.START_TAG
                                && parser.getName().equals("Item")) {
                            mainMapLanguages.put(parser.getAttributeValue(1),
                                    parser.getAttributeValue(0));
                        }
                        parser.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = getLanguagesArrayAdapter(activity);
                setLangListToUI(adapter, beginLangs, endLangs);
            }
        });
        getLangListThread.start();
    }

    private void setLangListToUI(final ArrayAdapter<String> adapter,
                                 final Spinner beginLangs,
                                 final Spinner endLangs) {
        Activity act = (Activity) beginLangs.getContext();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beginLangs.setAdapter(adapter);
                beginLangs.setSelection(63);  // русский язык при запуске приложения
                endLangs.setAdapter(adapter);
                endLangs.setSelection(3); // английский язык при запуске приложения
            }
        });
    }

    // загружаем список языков с диска если он доступен
    private void loadLanguageFromDiskIfAvailabled(Spinner beginLangs,
                                                  Spinner endLangs,
                                                  Activity activity) {
        if (!isFileLanguageMapExists()) return;
        loadLanguageMapFromDisk();
        ArrayAdapter<String> adapter = getLanguagesArrayAdapter(activity);
        setLangListToUI(adapter, beginLangs, endLangs);
    }

    @NonNull
    private ArrayAdapter<String> getLanguagesArrayAdapter(Activity activity) {
        Set setOfLangs = mainMapLanguages.keySet();
        String[] arrayOfLangs = (String[]) setOfLangs.toArray(new String[setOfLangs.size()]);
        Arrays.sort(arrayOfLangs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item,
                arrayOfLangs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    // устанавливаем язык с которого переводим
    public void setLangBegin(String langSelected){
        this.langBegin = checkLangNameFromList(langSelected);
    }

    // устанавливаем язык на который переводим
    public void setLangEnd(String langSelected){
        this.langEnd = checkLangNameFromList(langSelected);
    }

    // получаем двухбуквенное обозначение языка (для запроса)
    private String checkLangNameFromList(String listName){
        Log.d(PT_log, "SelectedLang: "+listName);
        Log.d(PT_log, "CheckLang: "+mainMapLanguages.get(listName));

        return mainMapLanguages.get(listName);
    }

    // получаем пару языков.
    public String getLangPair(){
        if(!langBegin.equals("") && !langEnd.equals("")){
            return langBegin+"-"+langEnd;
        }else if(langBegin.equals("") && !langEnd.equals("")){
            return "ru-"+langEnd;
        }else if(!langBegin.equals("") && langEnd.equals("")){
            return langBegin+"-en";
        }else{
            return "ru-en";
        }
    }

    // проверяем интернет соединение
    public boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    // сохраняем список языков
    public void saveLanguageMapOnDisk() {
        try {
            FileOutputStream fileOut = new FileOutputStream(APP_PATH + "mainMapLanguages.ptr");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(mainMapLanguages);
            objOut.close();
        }catch (Exception e){
            Log.e(PT_log,e.getLocalizedMessage());
        }
    }

    // проверяем существует ли файл со списком языков
    private boolean isFileLanguageMapExists(){
        File file = new File(APP_PATH+"mainMapLanguages.ptr");
        return file.exists();
    }

    // загружаем список языков
    private void loadLanguageMapFromDisk() {
        try {
            FileInputStream fileOut = new FileInputStream(APP_PATH+"mainMapLanguages.ptr");
            ObjectInputStream objOut= new ObjectInputStream(fileOut);
            mainMapLanguages = (HashMap) objOut.readObject();
            objOut.close();
        }catch (Exception e){
            Log.e(PT_log,"Stack:"+e.getLocalizedMessage());
        }
    }
}
