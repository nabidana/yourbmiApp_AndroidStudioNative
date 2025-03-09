package com.example.yourmbi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private List<String> list = new ArrayList<String>();
    private Context context;
    public interface ListBtnClickListener {
        void onListBtnClick(int position, String s) ;
    }
    private ListBtnClickListener listBtnClickListener;
    public CustomAdapter(List<String> list, Context context, ListBtnClickListener clickListener){
        this.list = list;
        this.context = context;
        this.listBtnClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        View view = convertview;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_row, null);
        }
        String getStr = list.get(i);
        String[] parseStr = getStr.split(",");
        TextView tvConvert = (TextView) view.findViewById(R.id.rowText);
        tvConvert.setText( parseStr[0] + " 혈압 : " + parseStr[1] + " 혈당 : " + parseStr[2]);
        Button btn = (Button) view.findViewById(R.id.rowupdate);
        btn.setTag(i);
        btn.setOnClickListener(this::onclick);
        return view;
    }

    public void onclick(View v){
        if(this.listBtnClickListener != null){
            this.listBtnClickListener.onListBtnClick( (int)v.getTag(), list.get((int)v.getTag()));
        }
    }
}
