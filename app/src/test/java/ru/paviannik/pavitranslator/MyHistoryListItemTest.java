package ru.paviannik.pavitranslator;

import org.junit.Assert;
import org.junit.Test;


public class MyHistoryListItemTest {
    @Test
    public void setTextInput() throws Exception {
        MyHistoryListItem tHistoryListItem = new MyHistoryListItem();
        tHistoryListItem.setTextInput("test");
        Assert.assertEquals("test",tHistoryListItem.getTextInput());
    }

    @Test
    public void setTextOutput() throws Exception {
        MyHistoryListItem tHistoryListItem = new MyHistoryListItem();
        tHistoryListItem.setTextOutput("test");
        Assert.assertEquals("test",tHistoryListItem.getTextOutput());
    }

    @Test
    public void setLangPair() throws Exception {
        MyHistoryListItem tHistoryListItem = new MyHistoryListItem();
        tHistoryListItem.setLangPair("test");
        Assert.assertEquals("test",tHistoryListItem.getLangPair());
    }

    @Test
    public void setIsBookmark() throws Exception {
        MyHistoryListItem tHistoryListItem = new MyHistoryListItem();
        tHistoryListItem.setIsBookmark(true);
        Assert.assertTrue(tHistoryListItem.getIsBookmark());
    }

}