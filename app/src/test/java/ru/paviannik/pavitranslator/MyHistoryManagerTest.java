package ru.paviannik.pavitranslator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyHistoryManagerTest {
    @Test
    public void setNewHistoryItem_New() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        String name = prov.getHistoryKeyById(0);
        String translate = prov.getHistoryTranslationByKey(name);
        String pair = prov.getHistoryLangPairByKey(name);

        assertEquals("test",name);
        assertEquals("тест",translate);
        assertEquals("en-ru",pair);
    }

    @Test
    public void setNewHistoryItem_Exists() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");
        prov.setHistoryElement("test","тест","en-ru");

        int size = prov.getHistoryLength();

        assertEquals(1,size);
    }

    @Test
    public void setNewHistoryItem_Empty() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("","","en-ru");

        boolean exist = prov.checkIfHistoryItemExists("");

        assertEquals(false,exist);
    }

    @Test
    public void setNewBookmarksItem_New() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");
        prov.setFavoritesElement("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,bookmark);
    }

    @Test
    public void setNewBookmarksItem_Exist() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");
        prov.setFavoritesElement("test");
        prov.setFavoritesElement("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,bookmark);
    }

    @Test
    public void removeBookmarksItem_Remove() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");
        prov.setFavoritesElement("test");
        prov.removeFavoritesElement("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(false,bookmark);
    }

    @Test
    public void getHistoryLength_1() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        int length = prov.getHistoryLength();

        assertEquals(1,length);
    }

    @Test
    public void getHistoryKeyById_test() throws Exception {
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        String name = prov.getHistoryKeyById(0);

        assertEquals("test",name);
    }

    @Test
    public void getHistoryTranslationByKey_test() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        String name = prov.getHistoryTranslationByKey("test");

        assertEquals("тест",name);
    }

    @Test
    public void getHistoryLangPairByKey_test() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        String name = prov.getHistoryLangPairByKey("test");

        assertEquals("en-ru",name);
    }

    @Test
    public void getBookmarkIfExistByKey_test() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");
        prov.setFavoritesElement("test");

        boolean exist = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,exist);
    }

    @Test
    public void checkIfHistoryItemExists_test() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test","тест","en-ru");

        boolean exist = prov.checkIfHistoryItemExists("test");

        assertEquals(true,exist);
    }

    @Test
    public void setAppPath_testAppPath() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setAppPath("test");

        String appPath = prov.APP_PATH;
        /*Добавляем / к проверке, т.к. команда сама его добавляет*/
        assertEquals("test/",appPath);
    }

    @Test
    public void clearHistoryOnly_test1_test2() throws Exception{
        MyHistoryManager prov = new MyHistoryManager();
        prov.setHistoryElement("test1","тест1","en-ru");
        prov.setHistoryElement("test2","тест2","en-ru");
        prov.setFavoritesElement("test2");
        prov.clearHistoryOnly();

        boolean exists1 = prov.checkIfHistoryItemExists("test1");
        boolean exists2 = prov.checkIfHistoryItemExists("test2");
        int length = prov.getHistoryLength();

        assertEquals(false,exists1);
        assertEquals(true,exists2);
        assertEquals(1,length);
    }

}
