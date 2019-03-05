package com.example.moriyaamar.project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> list;

    public ListAdapter(@NonNull Context context, int resource, ArrayList<String> values) {
        super(context, resource);
        this.context = context;
        this.list = values;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_of_lists_row_item, parent, false);
        }

        TextView listNameTextView = (TextView)rowView.findViewById(R.id.listNameTextView);

        listNameTextView.setText(list.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
