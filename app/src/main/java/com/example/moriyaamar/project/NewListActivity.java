package com.example.moriyaamar.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewListActivity extends AppCompatActivity implements FragAddNewItemDialog.NewItemListener {
    private ShopList currentList;
    private DatabaseReference appDatabase;
    private String uid;
    private static final String LISTS = "lists";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        Intent mainIntent = getIntent();
        final String listName = mainIntent.getStringExtra("LIST_NAME");
        String firebaseUrl = mainIntent.getStringExtra("FIREBASE_URL");
        uid = mainIntent.getStringExtra("UID");
        currentList = new ShopList(listName);
        final ArrayList<Item> basket = currentList.getShoppingList();


        appDatabase = FirebaseDatabase.getInstance().getReference();
        appDatabase.child(LISTS).child(uid).child(listName).push().setValue("0");

        appDatabase.child(LISTS).child(uid).child(listName).addValueEventListener(new ValueEventListener() {        //update the shopping list every time
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapshot: dataSnapshot.getChildren()){
                   basket.add(new Item(uniqueKeySnapshot.toString(), Integer.parseInt((String)uniqueKeySnapshot.getValue())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ListView list = (ListView)findViewById(R.id.basketListView);
        ItemAdapter itemAdapter = new ItemAdapter(this, R.layout.row_item, basket);
        list.setAdapter(itemAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.addMenuItem:
                new FragAddNewItemDialog().show(getSupportFragmentManager(),null);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewItemApproved(String itemName, int itemAmount) {
        Item newItem = new Item();
        newItem.setItemName(itemName);
        newItem.setAmount(itemAmount);

        currentList.setItemInShoppingList(newItem);

        appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(newItem.getItemName()).setValue(newItem.getAmount());        //insert item to database

    }
}
