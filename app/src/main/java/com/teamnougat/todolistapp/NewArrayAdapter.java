package com.teamnougat.todolistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewArrayAdapter extends ArrayAdapter<Item> {

    ArrayList<Item> newList = new ArrayList<>();

    public NewArrayAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        newList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_todo, null);
        TextView textTitle = (TextView) v.findViewById(R.id.task_title);
        TextView textType = (TextView) v.findViewById(R.id.task_type);
        TextView textDate = (TextView) v.findViewById(R.id.task_date);
        textTitle.setText(newList.get(position).getTitle());
        textType.setText(newList.get(position).getType());
        textDate.setText(newList.get(position).getDate());
        return v;
    }

    @Override
    public long getItemId(int position){
        return position;
    }


}
