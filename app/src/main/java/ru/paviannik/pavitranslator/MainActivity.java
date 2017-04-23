package ru.paviannik.pavitranslator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String PT_log = "PaviTranslator";

    private MyLayoutManager myLayoutManager;
    private MyTranslatorClass myTranslatorClass = new MyTranslatorClass();
    private MyHistoryManager myHistoryManager = new MyHistoryManager();
    private MyHistoryAdapter historyMyHistoryAdapter;

    public ImageButton btnTranslate;
    public ImageButton btnSwap;
    public ImageButton btnClear;
    public ImageButton btnHistoryClear;
    public ToggleButton btnToggleFavorites;

    public EditText myTextInput;
    public TextView myTextOutput;
    public TextView titleFavHistory;
    public ListView historyListView;

    public BottomNavigationView navigation;

    public Spinner FirstLangSpinner;
    public Spinner SecondLangSpinner;

    private static CharSequence emptyOutputFieldCharSequence;
    private static Editable emptyInputFieldEditable;
    public boolean onlyBookmarksIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApplication();
        myTranslatorClass.getLangsList(FirstLangSpinner, SecondLangSpinner,MainActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTranslatorClass.saveLanguageMapOnDisk();
        myHistoryManager.saveAllMapsOnDisk();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTranslatorClass.saveLanguageMapOnDisk();
        myHistoryManager.saveAllMapsOnDisk();
    }


    // Метод для работы с изменением ориетнации экрана
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int id = navigation.getSelectedItemId();
        SpinnerAdapter adapter = FirstLangSpinner.getAdapter();
        int beginId = FirstLangSpinner.getSelectedItemPosition();
        int endId = SecondLangSpinner.getSelectedItemPosition();
        Editable input = myTextInput.getText();
        CharSequence output = myTextOutput.getText();
        ((LinearLayout)findViewById(R.id.container)).removeAllViews();
        super.onConfigurationChanged(newConfig);

        startApplication();

        FirstLangSpinner.setAdapter(adapter);
        SecondLangSpinner.setAdapter(adapter);
        FirstLangSpinner.setSelection(beginId);
        SecondLangSpinner.setSelection(endId);
        myTextInput.setText(input);
        myTextOutput.setText(output);

        navigation.setSelectedItemId(id);
    }

    /* Листенер для нижнего меню
     вторая и третья кнопка ссылаются на один и тот же view,
     и меняют там заголок и отображение избранного */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_translate:
                    myLayoutManager.setSelectedLayout("main");
                    return true;
                case R.id.nav_history:
                    onlyBookmarksIsChecked = false;
                    myLayoutManager.setSelectedLayout("history");
                    titleFavHistory.setText(R.string.top_history);
                    setHistoryToView();
                    return true;
                case R.id.nav_favorites:
                    onlyBookmarksIsChecked = true;
                    myLayoutManager.setSelectedLayout("history");
                    titleFavHistory.setText(R.string.top_favorites);
                    setHistoryToView();
                    onlyBookmarksIsChecked = true;

                    return true;
            }
            return false;
        }

    };

    // Метод для отображения истории
    private void setHistoryToView() {
        // Получаем listview для работы с ним
        historyListView = (ListView) findViewById(R.id.historyListView);
        ArrayList<MyHistoryListItem> historyArrayList = new ArrayList<MyHistoryListItem>();
        // Создаем ArrayList с элементами истории и избранного
        int count = myHistoryManager.getHistoryLength()-1;
        for (int i = count;i >= 0;i--){
            String name = myHistoryManager.getHistoryKeyById(i);
            String translate = myHistoryManager.getHistoryTranslationByKey(name);
            String pair = myHistoryManager.getHistoryLangPairByKey(name);
            boolean isBook = myHistoryManager.isBookmarkWithThisKeyExists(name);

            // проверяем нужно ли показывать только избранное или нет
            if(onlyBookmarksIsChecked){
                if(isBook){
                    MyHistoryListItem listItem = getHistoryListItem(name, translate, pair, isBook);
                    historyArrayList.add(listItem);
                }
            } else {
                MyHistoryListItem listItem = getHistoryListItem(name, translate, pair, isBook);
                historyArrayList.add(listItem);
            }
        }

        // Назначаем адаптер для ArrayList и добавляем listener

        historyMyHistoryAdapter = new MyHistoryAdapter(this, historyArrayList);

        historyListView.setAdapter(historyMyHistoryAdapter);


        // при клике на элемент истории, мы отправляем его на перевод.

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView mainTextView = (TextView) view.findViewById(R.id.mainNameView);
                CharSequence inputText = mainTextView.getText();
                navigation.setSelectedItemId(R.id.nav_translate);
                myTextInput.setText(inputText);
                btnTranslate.performClick();

            }
        });
    }

    @NonNull
    private MyHistoryListItem getHistoryListItem(String name, String translate, String pair, boolean isBook) {
        MyHistoryListItem listItem = new MyHistoryListItem();
        listItem.setTextInput(name);
        listItem.setTextOutput(translate);
        listItem.setLangPair(pair.toUpperCase());
        listItem.setIsBookmark(isBook);
        return listItem;
    }

    // Инициируем элементы, которые нужны со старта
    private void startApplication() {
        setDrawableColor();

        setContentView(R.layout.activity_main);

        myLayoutManager = new MyLayoutManager(MainActivity.this);
        myHistoryManager.setAppPath(MainActivity.this.getApplicationInfo().dataDir);
        myHistoryManager.loadAllMapsFromDisk();

        findAllViews();

        emptyInputFieldEditable = myTextInput.getText();
        emptyOutputFieldCharSequence = myTextOutput.getText();
        myTextOutput.setMovementMethod(new ScrollingMovementMethod());

        startAllListeners();

    }

    // Присваиваем всем переменным свой view
    private void findAllViews() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        btnTranslate = (ImageButton)findViewById(R.id.id_btnTranslate);
        btnSwap = (ImageButton)findViewById(R.id.id_btnSwap);
        btnClear = (ImageButton) findViewById(R.id.id_btnClear);
        FirstLangSpinner = (Spinner)findViewById(R.id.FirstLangSpinner);
        SecondLangSpinner = (Spinner)findViewById(R.id.SecondLangSpinner);
        btnToggleFavorites = (ToggleButton)findViewById(R.id.addfavorite);
        myTextInput = (EditText)findViewById(R.id.textInput);
        myTextOutput = (TextView)findViewById(R.id.textOutput);
        btnHistoryClear = (ImageButton) findViewById(R.id.btnHistoryClear);
        titleFavHistory = (TextView) findViewById(R.id.titleFavHistory);
    }

    // Меняем цвет фаворит иконки
    private void setDrawableColor() {
        Drawable normalDrawable = getResources().getDrawable(R.drawable.ic_favorite_on);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.favorites));

        Drawable normalDrawable1 = getResources().getDrawable(R.drawable.ic_favorite_off);
        Drawable wrapDrawable1 = DrawableCompat.wrap(normalDrawable1);
        DrawableCompat.setTint(wrapDrawable1, getResources().getColor(R.color.favorites));
    }

    // Инициируем все листенеры
    private void startAllListeners() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // Кнопка отправляющия текст на перевод, заодно она убирает клавиатуру.
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnTranslate.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                getTranslate();
            }
        });

        // Выпадающее меню первого языка.
        FirstLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = FirstLangSpinner.getSelectedItem().toString();
                myTranslatorClass.setLangBegin(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Выпадающее меню второго языка.
        SecondLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = SecondLangSpinner.getSelectedItem().toString();
                myTranslatorClass.setLangEnd(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Кнопка, меняющия языки местами
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beg = FirstLangSpinner.getSelectedItemPosition();
                int end = SecondLangSpinner.getSelectedItemPosition();

                FirstLangSpinner.setSelection(end,true);
                SecondLangSpinner.setSelection(beg,true);

                myTextInput.setText("");
                myTextOutput.setText("");
            }
        });


        // Кнопка, очищающия поля ввода и вывода
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTextInput.setText("");
                myTextOutput.setText("");
            }
        });


        // Очичаем историю/избранное, в зависимость от того какой view выбран.
        btnHistoryClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlyBookmarksIsChecked) {
                    ClearFavoritesAlertDialog("Очистить избранное?", "");
                }
                else {
                    ClearHistoryAlertDialog("Очистить историю?", "");
                }
            }
        });




        // ToggleButton для добавления в избранное с экана перевода
        btnToggleFavorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if (myTextOutput.getText().toString() == "" ||
                            myTextOutput.getText() == emptyOutputFieldCharSequence){
                        btnToggleFavorites.setChecked(false);
                        return;
                    }
                    myHistoryManager.setFavoritesElement(myTextInput.getText().toString());
                }
                else
                {
                    myHistoryManager.removeFavoritesElement(myTextInput.getText().toString());
                }
            }
        });


        // При изменение текста в поле вывода, добавляем его в историю.
        myTextOutput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myHistoryManager.setHistoryElement(myTextInput.getText().toString(),s.toString(), myTranslatorClass.getLangPair());
                myHistoryManager.saveAllMapsOnDisk();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myTextOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        // В конце изменения текста, в поле ввода, проверяем есть ли этот текст в избранном, если да,
        // то меняем цвет иконки избранного
        myTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                btnToggleFavorites.setChecked(false);
                if(myHistoryManager
                        .isBookmarkWithThisKeyExists(myTextInput.getText().toString())){
                    btnToggleFavorites.setChecked(true);
                }
            }
        });

    }

    // Получаем историю и отправляем текст на перевод
    private void getTranslate() {
        if(myHistoryManager.checkIfHistoryItemExists(myTextInput.getText().toString())){
            String translate = myHistoryManager.getHistoryTranslationByKey(myTextInput.getText().toString());
            myTextOutput.setText(translate);
        }
        myTranslatorClass.getTranslate(myTextInput,myTextOutput);
    }

    // Класс меняет view (избранное и переводчик)
    public static class MyLayoutManager {

        private View translator;
        private View history;
        private Activity mainActivity;

        public MyLayoutManager(MainActivity activity){
            mainActivity = activity;
            translator = mainActivity.findViewById(R.id.id_translator);
            history = mainActivity.findViewById(R.id.id_history);
        }

        public void setSelectedLayout(String layout){
            switch (layout) {
                case "main":
                    translator.setVisibility(View.VISIBLE);
                    history.setVisibility(View.GONE);
                    break;
                case "history":
                    translator.setVisibility(View.GONE);
                    history.setVisibility(View.VISIBLE);

                    break;
            }
        }
    }

    // Диалоги для подтвердения очистки избранного
    private void ClearFavoritesAlertDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setPositiveButton("Да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myHistoryManager.clearFavoritesOnly();
                        myTextInput.setText("");
                        myTextOutput.setText("");
                        btnToggleFavorites.setChecked(false);
                        navigation.setSelectedItemId(R.id.nav_favorites);
                        showMessage("Избранное очищено");
                    }
                });
        builder.show();
    }

    // Диалоги для подтвердения очистки истории
    private void ClearHistoryAlertDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setPositiveButton("Да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myHistoryManager.clearHistoryOnly();
                        myTextInput.setText("");
                        myTextOutput.setText("");
                        btnToggleFavorites.setChecked(false);
                        navigation.setSelectedItemId(R.id.nav_history);
                        showMessage("История очищена");
                    }
                });
        builder.show();
    }

    // Вспомогательный метод для вывода всплывающих сообщений
    private void showMessage(String textInMessage) {
        Toast.makeText(getApplicationContext(), textInMessage, Toast.LENGTH_LONG).show();
    }
}
