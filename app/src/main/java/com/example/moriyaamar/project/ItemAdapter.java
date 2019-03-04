package com.example.moriyaamar.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final ArrayList<Item> shopList;



    public ItemAdapter(Context context, int resource, ArrayList<Item> values) {
        super(context, resource);

        this.context = context;
        shopList = values;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        //return super.getView(position, convertView, parent);

//        Item currentItem = getItem(position);
        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_item, parent, false);
        }

        TextView itemNameTextView = (TextView)rowView.findViewById(R.id.itemNameTextView);
        TextView itemAmountTextView = (TextView)rowView.findViewById(R.id.itemAmountTextView);

       // Item currentItem = this.shopList.get(position);
        itemNameTextView.setText(shopList.get(position).getItemName());
        itemAmountTextView.setText(Integer.toString(shopList.get(position).getAmount()));


        return rowView;
    }

    @Override
    public int getCount() {
        return shopList.size();
    }
}
