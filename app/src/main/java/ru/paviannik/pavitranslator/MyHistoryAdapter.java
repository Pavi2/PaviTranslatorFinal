package ru.paviannik.pavitranslator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyHistoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MyHistoryListItem> arrayList;

    public MyHistoryAdapter(Context context, ArrayList<MyHistoryListItem> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_row, null);
        }
        final MyHistoryListItem listItem = arrayList.get(position);
        if (listItem != null)
            ((TextView) convertView.findViewById(R.id.mainNameView)).setText(listItem.getTextInput());
            ((TextView) convertView.findViewById(R.id.translatedNameView)).setText(listItem.getTextOutput());
            ((TextView) convertView.findViewById(R.id.mainLangPairView)).setText(listItem.getLangPair());
            if(listItem.getIsBookmark()){
                ((ImageView) convertView.findViewById(R.id.isBookmarked)).setImageResource(R.drawable.ic_favorite_on);
            }else {
                ((ImageView) convertView.findViewById(R.id.isBookmarked)).setImageResource(R.drawable.ic_favorite_off);
            }

        return convertView;
    }
}
