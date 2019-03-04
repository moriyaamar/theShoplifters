package com.example.moriyaamar.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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

public class NewListActivity extends AppCompatActivity implements FragAddNewItemDialog.NewItemListener, FragNewList.OnFragmentInteractionListener {
    private ShopList currentList;
    private DatabaseReference appDatabase;
    private String uid;
    private static final String LISTS = "lists";
    private ArrayList<Item> basket;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        Intent mainIntent = getIntent();                                        //get all data from previous activity
        final String listName = mainIntent.getStringExtra("LIST_NAME");
        String firebaseUrl = mainIntent.getStringExtra("FIREBASE_URL");
        uid = mainIntent.getStringExtra("UID");

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.basketFragment, new FragNewList()).addToBackStack(null).commit();
        currentList = new ShopList(listName);

        basket = currentList.getShoppingList();

 /*       basket.add(new Item("Cola",3));
        basket.add(new Item("Bamba",2));
        basket.add(new Item("Cake",1));*/

        ListView list = (ListView)findViewById(R.id.basketListView);
        itemAdapter = new ItemAdapter(getApplicationContext(), R.layout.row_item, basket);
        itemAdapter.setNotifyOnChange(true);
        list.setAdapter(itemAdapter);

        appDatabase = FirebaseDatabase.getInstance().getReference();
        appDatabase.child(LISTS).child(uid).child(listName).push().setValue("-1");

        appDatabase.child(LISTS).child(uid).child(listName).addValueEventListener(new ValueEventListener() {        //update the shopping list every time
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapshot: dataSnapshot.getChildren()){
                    String itemName = uniqueKeySnapshot.getKey();
                    int itemAmount = Integer.parseInt(uniqueKeySnapshot.getValue().toString());
                    Item i = new Item(itemName, itemAmount);

                    if(itemAmount!=-1) {
                        if(!basket.contains(i)) {
                            basket.add(i);
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
