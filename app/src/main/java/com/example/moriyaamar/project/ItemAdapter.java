package com.example.moriyaamar.project;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final ArrayList<Item> shopList;
//    private int newAmount;
//    private DatabaseReference appDatabase;
//    private String uid, listName;
//    private TextWatcher tw;

//    private TextView itemNameTextView;



    public ItemAdapter(@NonNull Context context, int resource, ArrayList<Item> values) {
        super(context, resource);

        this.context = context;
        this.shopList = values;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_item, parent, false);
        }

        final TextView itemNameTextView = (TextView)rowView.findViewById(R.id.itemNameTextView);
        final TextView itemAmountTextView = (TextView)rowView.findViewById(R.id.itemAmountTextView);
        CheckBox itemCheckBox = (CheckBox)rowView.findViewById(R.id.itemCheckBox);

        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {                      //Maybe these should be placed in "Go Shopping" activity or maybe "edit list"
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
          /*          PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                            .putBoolean("itemCheckBox", isChecked).commit();*/
                }
                else {
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
       /*             PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                            .putBoolean("itemCheckBox", isChecked).commit();*/
                }
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
