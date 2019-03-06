package com.example.moriyaamar.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    private ArrayList<Item> basket;
    private ItemAdapter itemAdapter;
    private int currentPosition=-1;
    private int currentMenu=0;              //0 - add item menu , 1 - trash menu
    private boolean longClickIndicator=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        appDatabase = FirebaseDatabase.getInstance().getReference();
        Intent mainIntent = getIntent();                                        //get all data from previous activity
        final String listName = mainIntent.getExtras().getString("LIST_NAME");
        uid = mainIntent.getExtras().getString("UID");

        int state=1;                    //1 - new empty list, 2 - exist list, 3 - list from BroadcastReceiver
        state = mainIntent.getExtras().getInt("STATE");     //check if new or old list

        if(state==1) {
            currentList = new ShopList(listName);
            basket = currentList.getShoppingList();
        }
        else if(state==2){
            currentList = new ShopList(listName);
            basket = currentList.getShoppingList();
            appDatabase.child(LISTS).child(uid).child(listName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        String itemName = uniqueKeySnapshot.getKey();
                        int itemAmount = Integer.parseInt(uniqueKeySnapshot.getValue().toString());
                        Item i = new Item(itemName, itemAmount);
                        basket.add(i);
                        itemAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else if(state==3){
            ArrayList<Item> shoppingList = (ArrayList<Item>) mainIntent.getExtras().getSerializable("LIST");
            System.out.println(listName+" "+uid);
            appDatabase.child(LISTS).child(uid).child(listName).push().setValue("-1");
            for(Item i:shoppingList){
                appDatabase.child(LISTS).child(uid).child(listName).child(i.getItemName()).setValue(i.getAmount());
            }

            basket = shoppingList;
            /////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////CREATE NEW LIST IN DATABASE/////////////////////////
            /////////////////////////////////////////////////////////////////////////////////
        }

        ListView list = findViewById(R.id.basketListView);
        itemAdapter = new ItemAdapter(getApplicationContext(), R.layout.row_item, basket);
        itemAdapter.setNotifyOnChange(true);
        list.setAdapter(itemAdapter);

        if(state==1) {
            appDatabase.child(LISTS).child(uid).child(listName).push().setValue("-1");
        }

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

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                longClickIndicator=!longClickIndicator;

                if(longClickIndicator) {
                    view.setSelected(true);
                    currentMenu = 1;
                    invalidateOptionsMenu();
                }
                else {
                    view.setSelected(false);
                    currentMenu = 0;
                    invalidateOptionsMenu();
                }
                return true;
            }
        });

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem plusItem = menu.findItem(R.id.plusMenuItem);
        MenuItem trashItem = menu.findItem(R.id.trashMenuItem);
        MenuItem shareItem = menu.findItem(R.id.shareMenuItem);

        if(currentMenu==0) {
            plusItem.setEnabled(true).setVisible(true);
            trashItem.setEnabled(false).setVisible(false);
            shareItem.setEnabled(false).setVisible(false);
        }
        else{
            plusItem.setEnabled(false).setVisible(false);
            trashItem.setEnabled(true).setVisible(true);
            shareItem.setEnabled(false).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.plusMenuItem:
                new FragAddNewItemDialog().show(getSupportFragmentManager(),null);
                return true;
            case R.id.trashMenuItem:
                String itemname = basket.get(currentPosition).getItemName();
                basket.remove(currentPosition);
                itemAdapter.notifyDataSetChanged();
                appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(itemname).removeValue();
                currentMenu=0;
                invalidateOptionsMenu();
                Toast.makeText(getApplicationContext(), "Item was deleted successfully", Toast.LENGTH_SHORT).show();
               return true;
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

        //////////////////////////////////////////////////////////////
        ////////////////CHECK IF ITEM ALREADY EXISTS//////////////////
        //////////////////////////////////////////////////////////////

        appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(newItem.getItemName()).setValue(newItem.getAmount());        //insert item to database

    }


    public void removeItemFromList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete Item").setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basket.remove(currentPosition);
                        appDatabase.child(LISTS).child(uid).child(currentList.getListName()).removeValue();
                        currentMenu=0;
                        invalidateOptionsMenu();
                        Toast.makeText(getApplicationContext(), "Item was deleted successfully", Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });        //ask if you sure you want to delete item - currently not working****

        builder.create();
    }
}
