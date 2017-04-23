package ru.paviannik.pavitranslator;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static ru.paviannik.pavitranslator.MainActivity.PT_log;


/*
Класс для работы с историей и избранным
 */

public class MyHistoryManager {

    public static Map<String,Boolean> mainBookmarksMap = new HashMap<String,Boolean>();
    public Map<String,String> mainHistoryTransMap = new LinkedHashMap<String,String>();
    public Map<String,String> mainHistoryLangPairMap = new HashMap<String,String>();
    public static String APP_PATH;

    public MyHistoryManager(){}

    public void setAppPath(String path){
        APP_PATH = path+"/";
        Log.d(PT_log,"AppPathDir: "+APP_PATH);
    }
    public void saveAllMapsOnDisk() {
        try {
            saveFinalFavoritesMap();
            saveFinalHistoryTranslationMap();
            saveFinalHistoryLanguageMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadAllMapsFromDisk() {
        try {
            loadFinalFavoritesMap();
            loadFinalHistoryTranslationMap();
            loadFinalHistoryLanguageMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void saveFinalFavoritesMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainBookmarksMap.ptr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainBookmarksMap);
        objOut.close();
    }
    private void saveFinalHistoryTranslationMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainHistoryTransMap.ptr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainHistoryTransMap);
        objOut.close();
    }
    private void saveFinalHistoryLanguageMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainHistoryLangPairMap.ptr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainHistoryLangPairMap);
        objOut.close();
    }
    private void loadFinalFavoritesMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainBookmarksMap.ptr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainBookmarksMap = (HashMap) objOut.readObject();
        objOut.close();
    }
    private void loadFinalHistoryTranslationMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainHistoryTransMap.ptr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainHistoryTransMap = (LinkedHashMap) objOut.readObject();
        objOut.close();
    }
    private void loadFinalHistoryLanguageMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainHistoryLangPairMap.ptr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainHistoryLangPairMap = (HashMap) objOut.readObject();
        objOut.close();
    }

    public void setHistoryElement(String text, String translatedText, String langPair){
        if (checkIfHistoryItemExists(text)){return;}
        if (text.equals("")||translatedText.equals("")){return;}
        mainHistoryTransMap.put(text,translatedText);
        mainHistoryLangPairMap.put(text,langPair);
    }

    public void setFavoritesElement(String text){
        if (checkIfBookmarksItemExists(text)){return;}
        mainBookmarksMap.put(text,true);
    }

    public void removeFavoritesElement(String text){
        if (!checkIfBookmarksItemExists(text)){return;}
        mainBookmarksMap.remove(text);
    }

    public int getHistoryLength(){
        return mainHistoryTransMap.size();
    }

    public String getHistoryKeyById(int id){
        Set<String> keysSet = mainHistoryTransMap.keySet();
        String keyName = keysSet.toArray()[id].toString();
        return  keyName;
    }

    public String getHistoryTranslationByKey(String key){
        return mainHistoryTransMap.get(key);
    }

    public String getHistoryLangPairByKey(String key){
        return mainHistoryLangPairMap.get(key);
    }

    public  Boolean isBookmarkWithThisKeyExists(String key){
        boolean contains = mainBookmarksMap.containsKey(key);
        if(contains){
            return mainBookmarksMap.get(key);
        }
        return false;
    }

    public boolean checkIfHistoryItemExists(String text){
        return mainHistoryTransMap.containsKey(text);
    }

    public static boolean checkIfBookmarksItemExists(String text){
        return mainBookmarksMap.containsKey(text);
    }

    public void clearHistoryOnly(){
        int count = mainHistoryTransMap.size()-1;
        int toDelete = 0;
        Map<Integer,String> saveToDelete = new HashMap<>();
        for(int i = 0; i <= count; i++){
            boolean b = isBookmarkWithThisKeyExists(getHistoryKeyById(i));
            if(b){} else {
                saveToDelete.put(toDelete,getHistoryKeyById(i));
                toDelete++;
            }
        }
        for (int i = 0; i<=toDelete; i++) {
            mainHistoryTransMap.remove(saveToDelete.get(i));
            mainHistoryLangPairMap.remove(saveToDelete.get(i));
        }
        mainHistoryTransMap.remove("");
        mainHistoryLangPairMap.remove("");
        saveAllMapsOnDisk();
    }

    public void clearFavoritesOnly(){
        int count = mainHistoryTransMap.size()-1;
        int toDelete = 0;
        Map<Integer,String> needsToDelete = new HashMap<>();
        for(int i = 0; i <= count; i++){
            boolean b = isBookmarkWithThisKeyExists(getHistoryKeyById(i));
            if(b)
            {
                needsToDelete.put(toDelete,getHistoryKeyById(i));
                toDelete++;
            } else {
            }
        }
        for (int i = 0; i<=toDelete; i++) {
            mainHistoryTransMap.remove(needsToDelete.get(i));
            mainHistoryLangPairMap.remove(needsToDelete.get(i));
        }
        saveAllMapsOnDisk();
    }
}
