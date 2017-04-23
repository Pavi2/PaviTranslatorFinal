package ru.paviannik.pavitranslator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MyTranslatorClassTest {

    @Test
    public void getLangPair_test_onlyBeginLangChanged() throws Exception {
        MyTranslatorClass tTranslatorClass = new MyTranslatorClass();
        tTranslatorClass.langBegin = "de";
        Assert.assertEquals("de-en",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_onlyEndLangChanged() throws Exception {
        MyTranslatorClass tTranslatorClass = new MyTranslatorClass();
        tTranslatorClass.langEnd = "de";
        Assert.assertEquals("ru-de",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_bothLangChanged() throws Exception {
        MyTranslatorClass tTranslatorClass = new MyTranslatorClass();
        tTranslatorClass.langBegin = "de";
        tTranslatorClass.langEnd = "de";
        Assert.assertEquals("de-de",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_noLangChanged() throws Exception {
        MyTranslatorClass tTranslatorClass = new MyTranslatorClass();
        Assert.assertEquals("ru-en",tTranslatorClass.getLangPair());
    }
}
