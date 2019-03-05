package com.example.moriyaamar.project;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final ArrayList<Item> shopList;
    private TextView itemNameTextView;



    public ItemAdapter(Context context, int resource, ArrayList<Item> values) {
        super(context, resource);

        this.context = context;
        shopList = values;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {

        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_item, parent, false);
        }

        itemNameTextView = (TextView)rowView.findViewById(R.id.itemNameTextView);
        TextView itemAmountTextView = (TextView)rowView.findViewById(R.id.itemAmountTextView);
        CheckBox itemCheckBox = (CheckBox)rowView.findViewById(R.id.itemCheckBox);

        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {                      //Maybe these should be placed in "Go Shopping" activity or maybe "edit list"
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });


        itemNameTextView.setText(shopList.get(position).getItemName());
        itemAmountTextView.setText(Integer.toString(shopList.get(position).getAmount()));


        return rowView;
    }

    @Override
    public int getCount() {
        return shopList.size();
    }
}
