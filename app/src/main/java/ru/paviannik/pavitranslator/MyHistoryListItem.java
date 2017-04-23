package ru.paviannik.pavitranslator;

// Элементы списка истории/избранного.


public class MyHistoryListItem {

    public String getTextInput() {
        return textInput;
    }

    public void setTextInput(String mainText) {
        this.textInput = mainText;
    }

    public String getTextOutput() {
        return textOutput;
    }

    public void setTextOutput(String textOutput) {
        this.textOutput = textOutput;
    }

    public String getLangPair(){
        return langPair;
    }

    public void setLangPair(String langPair){
        this.langPair = langPair;
    }

    public boolean getIsBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(boolean bookmark) {
        this.isBookmark = bookmark;
    }

    private String textInput;
    private String textOutput;
    private String langPair;
    private boolean isBookmark;
}